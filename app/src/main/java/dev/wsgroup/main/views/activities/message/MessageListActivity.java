package dev.wsgroup.main.views.activities.message;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIChatCaller;
import dev.wsgroup.main.models.apis.callers.APISupplierCaller;
import dev.wsgroup.main.models.dtos.Message;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.views.activities.MainActivity;

public class MessageListActivity extends AppCompatActivity {

    private ImageView imgBackFromMessageList, imgMessageListHome;
    private RelativeLayout layoutLoading, layoutNoMessage;
    private LinearLayout layoutFailedGettingMessage;
    private TextView lblRetryGetMessage;
    private RecyclerView recViewMessageList;

    private SharedPreferences sharedPreferences;
    private List<Message> messageList;
    private List<Supplier> conversationList;
    private String token, accountId, foreignAccountId;
    private final String CUSTOMER_SERVICE_ACCOUNT_ID = "SERVICE";

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

        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
        accountId = sharedPreferences.getString("ACCOUNT_ID", "");
        getMessageList();

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

    private void getMessageList() {
        setLoadingState();
        APIChatCaller.getCustomerChatMessages(token, messageList, getApplication(), new APIListener() {
            @Override
            public void onMessageListFound(List<Message> list) {
                if (list.size() > 0) {
                    messageList = list;
                    findSuppliers();
                } else {
                    setNoMessageState();
                }
            }

            @Override
            public void onFailedAPICall(int code) {
                setFailedState();
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
            supplierAccountIdSet.add(foreignAccountId);
        }
        conversationList = new ArrayList<>();
        Supplier service = new Supplier();
        service.setId(CUSTOMER_SERVICE_ACCOUNT_ID);
        conversationList.add(service);
        APISupplierCaller.getSupplierListByAccountId(supplierAccountIdSet, conversationList,
                getApplication(), new APIListener() {
            @Override
            public void onSupplierListFound(List<Supplier> list) {
                conversationList = list;
                setConversationList();
            }

            @Override
            public void onFailedAPICall(int code) {
                setFailedState();
            }
        });
    }

    private void setConversationList() {

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

}