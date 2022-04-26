package dev.wsgroup.main.views.activities.message;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIChatCaller;
import dev.wsgroup.main.models.apis.callers.APISupplierCaller;
import dev.wsgroup.main.models.dtos.Conversation;
import dev.wsgroup.main.models.dtos.Message;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewMessageListAdapter;
import dev.wsgroup.main.models.services.FirebaseDatabaseReferences;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class MessageListActivity extends AppCompatActivity {

    private ImageView imgBackFromMessageList, imgMessageListHome;
    private RelativeLayout layoutLoading, layoutNoMessage;
    private LinearLayout layoutFailedGettingMessage;
    private TextView lblRetryGetMessage;
    private RecyclerView recViewMessageList;

    private SharedPreferences sharedPreferences;
    private List<Message> messageList;
    private List<Conversation> conversationList;
    private Conversation conversation;
    private User user;
    private String token, accountId, foreignAccountId, customerServiceId;
    private boolean customerServiceMessaged, messageListLoading;
    private RecViewMessageListAdapter adapter;
    private DialogBoxLoading dialogBoxLoading;
    private FirebaseDatabaseReferences databaseReferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_list);
        this.getSupportActionBar().hide();

        imgBackFromMessageList = findViewById(R.id.imgBackFromMessageList);
        imgMessageListHome = findViewById(R.id.imgMessageListHome);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutNoMessage = findViewById(R.id.layoutNoMessage);
        layoutFailedGettingMessage = findViewById(R.id.layoutFailedGettingMessage);
        lblRetryGetMessage = findViewById(R.id.lblRetryGetMessage);
        recViewMessageList = findViewById(R.id.recViewMessageList);

        setupFirebase();

        imgBackFromMessageList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });
        imgMessageListHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        lblRetryGetMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMessageList();
            }
        });
    }

    private void setupFirebase() {
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
        accountId = sharedPreferences.getString("ACCOUNT_ID", "");
        user = new User();
        user.setAccountId(accountId);
        customerServiceMessaged = false; messageListLoading = true;
        setLoadingState();
        if (databaseReferences == null) {
            databaseReferences = new FirebaseDatabaseReferences();
        }
        databaseReferences.getUserNotifications(accountId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!messageListLoading) {
                    setupFirebase();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });
        databaseReferences.getCustomerServiceId().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        customerServiceId = ds.getKey();
                        getMessageList();
                    }
                } else {
                    setFailedState();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {

            }
        });
    }

    private void getMessageList() {
        APIChatCaller.getCustomerChatMessages(token, messageList,
                getApplication(), new APIListener() {
            @Override
            public void onMessageListFound(List<Message> list) {
                messageList = list;
                if (messageList.isEmpty()) {
                    messageList = new ArrayList<>();
                }
                findSuppliers();
            }

            @Override
            public void onFailedAPICall(int code) {
                if (code == IntegerUtils.ERROR_NO_USER) {
                    MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                            MessageListActivity.this);
                } else {
                    setFailedState();
                }
            }
        });
    }

    private void findSuppliers() {
        Set<String> supplierAccountIdSet = new LinkedHashSet<>();
        for (Message message : messageList) {
            if (message.getFromId().equals(accountId)) {
                foreignAccountId = message.getToId();
            } else {
                foreignAccountId = message.getFromId();
            }
            if (!foreignAccountId.equals(customerServiceId)) {
                supplierAccountIdSet.add(foreignAccountId);
            }
        }
        List<Supplier> supplierList = new ArrayList<>();
        Supplier service = new Supplier();
        service.setId(customerServiceId);
        service.setName("Customer Service");

        supplierList.add(service);

        APISupplierCaller.getSupplierListByAccountId(supplierAccountIdSet, supplierList,
                getApplication(), new APIListener() {
            @Override
            public void onSupplierListFound(List<Supplier> list) {
                conversationList = new ArrayList<>();
                for (Supplier supplier : list) {
                    conversation = new Conversation();
                    conversation.setMainUser(user);
                    conversation.setSupplier(supplier);
                    conversation.setLastMessage(getLastMessage(supplier.getAccountId()));
                    conversationList.add(conversation);
                }
                setConversationList();
            }

            @Override
            public void onFailedAPICall(int code) {
                setFailedState();
            }
        });
    }

    private void setConversationList() {
        adapter = new RecViewMessageListAdapter(getApplicationContext()) {
            @Override
            public void onConversationSelected(String userId, String supplierId) {
                dialogBoxLoading = new DialogBoxLoading(MessageListActivity.this);
                dialogBoxLoading.getWindow()
                        .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                APIChatCaller.updateReadMessages(token, userId, supplierId, getApplication(), new APIListener() {
                    @Override
                    public void onUpdateSuccessful() {
                        if (dialogBoxLoading.isShowing()) {
                            dialogBoxLoading.dismiss();
                        }
                        Intent messageIntent = new Intent(getApplicationContext(), MessageActivity.class);
                        messageIntent.putExtra("USER_ID", userId);
                        messageIntent.putExtra("SUPPLIER_ID", supplierId);
                        startActivityForResult(messageIntent, IntegerUtils.REQUEST_COMMON);
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        if (code == IntegerUtils.ERROR_NO_USER) {
                            MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                                    MessageListActivity.this);
                        } else {
                            MethodUtils.displayErrorAPIMessage(MessageListActivity.this);
                        }
                    }
                });
            }
        };
        adapter.setConversationList(conversationList);
        recViewMessageList.setAdapter(adapter);
        recViewMessageList.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));
        messageListLoading = false;
        setListFoundState();
    }

    private Message getLastMessage(String id) {
        for (int i = messageList.size() - 1;i >= 0;i--) {
            if (messageList.get(i).getFromId().equals(id)
                    || messageList.get(i).getToId().equals(id)) {
                return messageList.get(i);
            }
        }
        return null;
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoMessage.setVisibility(View.GONE);
        layoutFailedGettingMessage.setVisibility(View.GONE);
        recViewMessageList.setVisibility(View.GONE);
    }

    private void setNoMessageState() {
        layoutLoading.setVisibility(View.GONE);
        layoutNoMessage.setVisibility(View.VISIBLE);
        layoutFailedGettingMessage.setVisibility(View.GONE);
        recViewMessageList.setVisibility(View.GONE);

    }

    private void setFailedState() {
        layoutLoading.setVisibility(View.GONE);
        layoutNoMessage.setVisibility(View.GONE);
        layoutFailedGettingMessage.setVisibility(View.VISIBLE);
        recViewMessageList.setVisibility(View.GONE);
    }

    private void setListFoundState() {
        layoutLoading.setVisibility(View.GONE);
        layoutNoMessage.setVisibility(View.GONE);
        layoutFailedGettingMessage.setVisibility(View.GONE);
        recViewMessageList.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        imgBackFromMessageList.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getMessageList();
    }
}