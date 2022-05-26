package dev.wsgroup.main.views.activities;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIDiscountCaller;
import dev.wsgroup.main.models.apis.callers.APINotificationCaller;
import dev.wsgroup.main.models.dtos.CustomerDiscount;
import dev.wsgroup.main.models.dtos.Notification;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewNotificationAdapter;
import dev.wsgroup.main.models.services.FirebaseReferences;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.account.AccountInformationActivity;
import dev.wsgroup.main.views.activities.order.OrderActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;
import dev.wsgroup.main.views.dialogbox.DialogBoxDiscountList;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class NotificationActivity extends AppCompatActivity {

    private ImageView imgBackFromNotification, imgNotificationHome;
    private RelativeLayout layoutLoading, layoutNoNotification;
    private LinearLayout layoutFailed, layoutList;
    private TextView lblRetry, txtUnreadCount, lblUnreadCount, lblMarkAsRead;
    private RecyclerView recViewNotificationList;

    private SharedPreferences sharedPreferences;
    private String token, accountId;
    private FirebaseReferences databaseReferences;
    private boolean notificationLoading;
    private RecViewNotificationAdapter adapter;
    private List<Notification> notificationList;
    private DialogBoxLoading dialogBoxLoading;
    private DialogBoxDiscountList dialogBoxDiscount;
    private int unreadCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        this.getSupportActionBar().hide();

        imgBackFromNotification = findViewById(R.id.imgBackFromNotification);
        imgNotificationHome = findViewById(R.id.imgNotificationHome);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutNoNotification = findViewById(R.id.layoutNoNotification);
        layoutFailed = findViewById(R.id.layoutFailed);
        layoutList = findViewById(R.id.layoutList);
        lblRetry = findViewById(R.id.lblRetry);
        txtUnreadCount = findViewById(R.id.txtUnreadCount);
        lblUnreadCount = findViewById(R.id.lblUnreadCount);
        lblMarkAsRead = findViewById(R.id.lblMarkAsRead);
        recViewNotificationList = findViewById(R.id.recViewNotificationList);

        adapter = new RecViewNotificationAdapter(getApplicationContext()) {
            @Override
            public void onOrderNotificationClicked(Notification notification) {
                selectOrderNotification(notification);
            }

            @Override
            public void onDiscountNotificationClicked(Notification notification) {
                selectDiscountNotification(notification);
            }

            @Override
            public void onEWalletNotificationClicked(Notification notification) {
                selectEWalletNotification(notification);
            }
        };
        recViewNotificationList.setAdapter(adapter);
        recViewNotificationList.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL,false));

        getNotificationList();
        setupFirebase();
        
        imgBackFromNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });

        imgNotificationHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        lblRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNotificationList();
            }
        });
        lblMarkAsRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogBoxConfirm dialogBoxConfirm = new DialogBoxConfirm(NotificationActivity.this,
                        StringUtils.MES_CONFIRM_NOTIFICATION) {
                    @Override
                    public void onYesClicked() {
                        clearUnreadNotification();
                    }
                };
                dialogBoxConfirm.getWindow()
                        .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxConfirm.show();
            }
        });
    }

    private void setupFirebase() {
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        accountId = sharedPreferences.getString("ACCOUNT_ID", "");
        databaseReferences = new FirebaseReferences();
        databaseReferences.getUserNotifications(accountId)
                          .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!notificationLoading) {
                    getNotificationList();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });
    }

    private void getNotificationList() {
        setLoadingState();
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
        unreadCount = 0;
        notificationLoading = true;
        if (!token.isEmpty()) {
            APINotificationCaller.getUsersNotifications(token, getApplication(), new APIListener() {
                @Override
                public void onNotificationListFound(List<Notification> list) {
                    notificationList = list;
                    if (list.size() > 0) {
                        adapter.setNotificationList(list);
                        notificationLoading = false;
                        for (Notification notification : list) {
                            if (!notification.getNotificationRead()) {
                                unreadCount++;
                            }
                        }
                        if (unreadCount > 0) {
                            lblMarkAsRead.setVisibility(View.VISIBLE);
                            txtUnreadCount.setVisibility(View.VISIBLE);
                            txtUnreadCount.setText(unreadCount + "");
                            lblUnreadCount.setText(unreadCount > 1 ?
                                    "New Notifications" : "New Notification");
                        } else {
                            lblMarkAsRead.setVisibility(View.GONE);
                            txtUnreadCount.setVisibility(View.GONE);
                            lblUnreadCount.setText("No new Notification");
                        }
                        setListLoadedState();
                    } else {
                        notificationLoading = false;
                        setNoListState();
                    }
                }

                @Override
                public void onFailedAPICall(int code) {
                    if (code == IntegerUtils.ERROR_NO_USER) {
                        MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                                NotificationActivity.this);
                    } else {
                        notificationLoading = false;
                        setFailedState();
                    }
                }
            });
        }
    }

    private void clearUnreadNotification() {
        setLoadingState();
        List<Notification> unreadList = new ArrayList<>();
        for (Notification notification : notificationList) {
            if (!notification.getNotificationRead()) {
                unreadList.add(notification);
            }
        }
        if (!unreadList.isEmpty()) {
            APINotificationCaller.updateReadNotification(token, notificationList,
                    getApplication(), new APIListener() {
                @Override
                public void onUpdateSuccessful() {
                    getNotificationList();
                }

                @Override
                public void onFailedAPICall(int code) {
                    setFailedState();
                }
            });
        }
    }

    private void selectOrderNotification(Notification notification) {
        dialogBoxLoading = new DialogBoxLoading(NotificationActivity.this);
        dialogBoxLoading.getWindow()
                        .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxLoading.show();
        if (!notification.getNotificationRead()) {
            notificationList = new ArrayList<>();
            notificationList.add(notification);
            APINotificationCaller.updateReadNotification(token, notificationList,
                    getApplication(), new APIListener() {
                @Override
                public void onUpdateSuccessful() {
                    if (dialogBoxLoading.isShowing()) {
                        dialogBoxLoading.dismiss();
                    }
                    Intent orderDetailIntent = new Intent(getApplicationContext(), OrderActivity.class);
                    orderDetailIntent.putExtra("ORDER_CODE", notification.getLink());
                    orderDetailIntent.putExtra("REQUEST_CODE", IntegerUtils.REQUEST_COMMON);
                    startActivityForResult(orderDetailIntent, IntegerUtils.REQUEST_COMMON);
                }

                @Override
                public void onFailedAPICall(int code) {
                    if (dialogBoxLoading.isShowing()) {
                        dialogBoxLoading.dismiss();
                    }
                    if (code == IntegerUtils.ERROR_NO_USER) {
                        MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                                NotificationActivity.this);
                    } else {
                        MethodUtils.displayErrorAPIMessage(NotificationActivity.this);
                    }
                }
            });
        } else {
            if (dialogBoxLoading.isShowing()) {
                dialogBoxLoading.dismiss();
            }
            Intent orderDetailIntent = new Intent(getApplicationContext(), OrderActivity.class);
            orderDetailIntent.putExtra("ORDER_CODE", notification.getLink());
            orderDetailIntent.putExtra("REQUEST_CODE", IntegerUtils.REQUEST_COMMON);
            startActivityForResult(orderDetailIntent, IntegerUtils.REQUEST_COMMON);
        }
    }

    private void selectDiscountNotification(Notification notification) {
        dialogBoxLoading = new DialogBoxLoading(NotificationActivity.this);
        dialogBoxLoading.getWindow()
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxLoading.show();
        if (!notification.getNotificationRead()) {
            notificationList = new ArrayList<>();
            notificationList.add(notification);
            APINotificationCaller.updateReadNotification(token, notificationList,
                    getApplication(), new APIListener() {
                        @Override
                        public void onUpdateSuccessful() {
                            String code = notification.getMessage();
                            code = code.substring(code.indexOf(":") + 2);
                            getDiscount(code, notification.getLink());
                        }

                        @Override
                        public void onFailedAPICall(int code) {
                            if (dialogBoxLoading.isShowing()) {
                                dialogBoxLoading.dismiss();
                            }
                            if (code == IntegerUtils.ERROR_NO_USER) {
                                MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                                        NotificationActivity.this);
                            } else {
                                MethodUtils.displayErrorAPIMessage(NotificationActivity.this);
                            }
                        }
                    });
        } else {
            String code = notification.getMessage();
            code = code.substring(code.indexOf(":") + 2);
            getDiscount(code, notification.getLink());
        }
    }

    private void selectEWalletNotification(Notification notification) {
        dialogBoxLoading = new DialogBoxLoading(NotificationActivity.this);
        dialogBoxLoading.getWindow()
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxLoading.show();
        if (!notification.getNotificationRead()) {
            notificationList = new ArrayList<>();
            notificationList.add(notification);
            APINotificationCaller.updateReadNotification(token, notificationList,
                    getApplication(), new APIListener() {
                        @Override
                        public void onUpdateSuccessful() {
                            Intent accountInfoIntent = new Intent(getApplicationContext(),
                                    AccountInformationActivity.class);
                            accountInfoIntent.putExtra("REQUEST_CODE",
                                    IntegerUtils.REQUEST_UPDATE_E_WALLET);
                            startActivityForResult(accountInfoIntent, IntegerUtils.REQUEST_COMMON);
                        }

                        @Override
                        public void onFailedAPICall(int code) {
                            if (dialogBoxLoading.isShowing()) {
                                dialogBoxLoading.dismiss();
                            }
                            if (code == IntegerUtils.ERROR_NO_USER) {
                                MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                                        NotificationActivity.this);
                            } else {
                                MethodUtils.displayErrorAPIMessage(NotificationActivity.this);
                            }
                        }
                    });
        } else {
            Intent accountInfoIntent = new Intent(getApplicationContext(), AccountInformationActivity.class);
            accountInfoIntent.putExtra("MAIN_TAB_POSITION", 2);
            startActivityForResult(accountInfoIntent, IntegerUtils.REQUEST_COMMON);
        }
    }

    private void getDiscount(String code, String supplierId) {
        APIDiscountCaller.getDiscountByDiscountCode(token, supplierId, code,
                getApplication(), new APIListener() {
            @Override
            public void onDiscountListFound(List<CustomerDiscount> discountList) {
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                if (discountList.isEmpty()) {
                    MethodUtils.displayErrorAPIMessage(NotificationActivity.this);
                } else {
                    dialogBoxDiscount = new DialogBoxDiscountList( NotificationActivity.this,
                            getApplicationContext(), discountList,
                            IntegerUtils.IDENTIFIER_VIEW_ONLY);
                    dialogBoxDiscount.getWindow()
                                     .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogBoxDiscount.show();
                }
            }

            @Override
            public void onFailedAPICall(int code) {
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                if (code == IntegerUtils.ERROR_NO_USER) {
                    MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                            NotificationActivity.this);
                } else {
                    MethodUtils.displayErrorAPIMessage(NotificationActivity.this);
                }
            }
        });
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutFailed.setVisibility(View.INVISIBLE);
        layoutNoNotification.setVisibility(View.INVISIBLE);
        layoutList.setVisibility(View.INVISIBLE);
    }

    private void setListLoadedState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutFailed.setVisibility(View.INVISIBLE);
        layoutNoNotification.setVisibility(View.INVISIBLE);
        layoutList.setVisibility(View.VISIBLE);
    }

    private void setNoListState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutFailed.setVisibility(View.INVISIBLE);
        layoutNoNotification.setVisibility(View.VISIBLE);
        layoutList.setVisibility(View.INVISIBLE);
    }

    private void setFailedState() {
        layoutLoading.setVisibility(View.INVISIBLE);
        layoutFailed.setVisibility(View.VISIBLE);
        layoutNoNotification.setVisibility(View.INVISIBLE);
        layoutList.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        imgBackFromNotification.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getNotificationList();
        if (databaseReferences == null) {
            setupFirebase();
        }
    }
}