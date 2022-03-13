package dev.wsgroup.main.views.activities.message;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIChatCaller;
import dev.wsgroup.main.models.apis.callers.APISupplierCaller;
import dev.wsgroup.main.models.dtos.Message;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewMessageAdapter;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewMessageListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class MessageActivity extends AppCompatActivity {

    private ConstraintLayout layoutParent;
    private ImageView imgBackFromMessage, imgMessageHome;
    private RelativeLayout layoutLoading;
    private LinearLayout layoutNoMessage, layoutFailedGettingMessage;
    private RecyclerView recViewMessage;
    private EditText editWriteMessage;
    private TextView txtSend, lblRetryGetMessage;
    private FrameLayout layoutBody;

    private SharedPreferences sharedPreferences;
    private List<Message> messageList;
    private String token, userAccountId, supplierAccountId;
    private Supplier supplier;
    private final String CUSTOMER_SERVICE_ACCOUNT_ID = "SERVICE";
    private boolean messageCheck, profileCheck;
    private RecViewMessageAdapter adapter;
    private View.OnClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        this.getSupportActionBar().hide();

        layoutParent = findViewById(R.id.layoutParent);
        imgBackFromMessage = findViewById(R.id.imgBackFromMessage);
        imgMessageHome = findViewById(R.id.imgMessageHome);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutNoMessage = findViewById(R.id.layoutNoMessage);
        layoutFailedGettingMessage = findViewById(R.id.layoutFailedGettingMessage);
        recViewMessage = findViewById(R.id.recViewMessage);
        editWriteMessage = findViewById(R.id.editWriteMessage);
        txtSend = findViewById(R.id.txtSend);
        lblRetryGetMessage = findViewById(R.id.lblRetryGetMessage);
        layoutBody = findViewById(R.id.layoutBody);

        getConversation();
        listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editWriteMessage.hasFocus()) {
                    editWriteMessage.clearFocus();
                }
            }
        };

        imgBackFromMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
        imgMessageHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        lblRetryGetMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getConversation();
            }
        });
        editWriteMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editWriteMessage.setMaxLines(4);
                    editWriteMessage.setEllipsize(null);
                } else {
                    hideKeyboard(view);
                    editWriteMessage.setMaxLines(1);
                    editWriteMessage.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        });
        layoutParent.setOnClickListener(listener);
        recViewMessage.setOnClickListener(listener);
        layoutBody.setOnClickListener(listener);
        txtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void getConversation() {
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
        userAccountId = getIntent().getStringExtra("USER_ID");
        supplierAccountId = getIntent().getStringExtra("SUPPLIER_ID");
        setLoadingState();
        messageCheck = false; profileCheck = false;
        getMessageList();
        getProfile();
    }

    private void getMessageList() {
        APIChatCaller.getConversation(token, userAccountId, supplierAccountId, getApplication(), new APIListener() {
            @Override
            public void onMessageListFound(List<Message> list) {
                messageList = list;
                messageCheck = true;
                setConversation();
            }

            @Override
            public void onFailedAPICall(int code) {
                setFailedState();
            }
        });
    }

    private void getProfile() {
        if (supplierAccountId.equals(CUSTOMER_SERVICE_ACCOUNT_ID)) {
            supplier = new Supplier();
            supplier.setAccountId(CUSTOMER_SERVICE_ACCOUNT_ID);
            supplier.setName("Customer Service");
            profileCheck = true;
            setConversation();
        } else {
            Set<String> idSet = new LinkedHashSet<>();
            idSet.add(supplierAccountId);
            APISupplierCaller.getSupplierListByAccountId(idSet, null, getApplication(), new APIListener() {
                @Override
                public void onSupplierListFound(List<Supplier> supplierList) {
                    if (supplierList.size() > 0) {
                        supplier = supplierList.get(0);
                        profileCheck = true;
                        setConversation();
                    } else {
                        setFailedState();
                    }
                }

                @Override
                public void onFailedAPICall(int code) {
                    setFailedState();
                }
            });
        }
    }

    private void setConversation() {
        if (messageCheck && profileCheck) {
            if (messageList.isEmpty()) {
                setNoMessageState();
            } else {
                adapter = new RecViewMessageAdapter(getApplicationContext(), userAccountId) {
                    @Override
                    public void onClick() {
                        if (editWriteMessage.hasFocus()) {
                            editWriteMessage.clearFocus();
                        }
                    }
                };
                adapter.setMessageList(messageList);
                recViewMessage.setAdapter(adapter);
                recViewMessage.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                        LinearLayoutManager.VERTICAL, true));
                setListFoundState();
            }
        }
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoMessage.setVisibility(View.GONE);
        layoutFailedGettingMessage.setVisibility(View.GONE);
        recViewMessage.setVisibility(View.GONE);
    }

    private void setNoMessageState() {
        layoutLoading.setVisibility(View.GONE);
        layoutNoMessage.setVisibility(View.VISIBLE);
        layoutFailedGettingMessage.setVisibility(View.GONE);
        recViewMessage.setVisibility(View.GONE);

    }

    private void setFailedState() {
        layoutLoading.setVisibility(View.GONE);
        layoutNoMessage.setVisibility(View.GONE);
        layoutFailedGettingMessage.setVisibility(View.VISIBLE);
        recViewMessage.setVisibility(View.GONE);
    }

    private void setListFoundState() {
        layoutLoading.setVisibility(View.GONE);
        layoutNoMessage.setVisibility(View.GONE);
        layoutFailedGettingMessage.setVisibility(View.GONE);
        recViewMessage.setVisibility(View.VISIBLE);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager
                = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}