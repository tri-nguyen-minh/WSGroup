package dev.wsgroup.main.views.fragments.tabs.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.callers.APIChatCaller;
import dev.wsgroup.main.models.apis.callers.APINotificationCaller;
import dev.wsgroup.main.models.apis.callers.APIUserCaller;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Message;
import dev.wsgroup.main.models.dtos.Notification;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.services.FirebaseReferences;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.ObjectSerializer;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.CartActivity;
import dev.wsgroup.main.views.activities.DiscountActivity;
import dev.wsgroup.main.views.activities.NotificationActivity;
import dev.wsgroup.main.views.activities.account.AccountInformationActivity;
import dev.wsgroup.main.views.activities.address.AddressListActivity;
import dev.wsgroup.main.views.activities.account.PasswordChangeActivity;
import dev.wsgroup.main.views.activities.message.MessageListActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;

public class ProfileTab extends Fragment {

    private ImageView imgAccountAvatar;
    private TextView txtProfileTabUsername, txtNotificationCount,
            txtMessageCount, txtCartCount, lblRetry;
    private LinearLayout layoutProfileAvatar, layoutDiscount, layoutLoyalty, layoutAccountInfo,
            layoutChangePassword, layoutDeliveryAddress, layoutLogout, layoutNotification,
            layoutMessage, layoutCart, layoutFailed;
    private CardView cardViewNotificationCount, cardViewMessageCount, cardViewCartCount;
    private ConstraintLayout layoutScreen;
    private RelativeLayout layoutLoading;

    private SharedPreferences sharedPreferences;
    private List<CartProduct> retailList, campaignList;
    private String token, username, accountId, googleId;
    private int cartCount, messageCount, notificationCount;
    private boolean messageLoading, notificationLoading;

    private FirebaseReferences firebaseReferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_profile_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imgAccountAvatar = view.findViewById(R.id.imgAccountAvatar);
        txtProfileTabUsername = view.findViewById(R.id.txtProfileTabUsername);
        txtNotificationCount = view.findViewById(R.id.txtNotificationCount);
        txtMessageCount = view.findViewById(R.id.txtMessageCount);
        txtCartCount = view.findViewById(R.id.txtCartCount);
        lblRetry = view.findViewById(R.id.lblRetry);
        layoutProfileAvatar = view.findViewById(R.id.layoutProfileAvatar);
        layoutDiscount = view.findViewById(R.id.layoutDiscount);
        layoutLoyalty = view.findViewById(R.id.layoutLoyalty);
        layoutAccountInfo = view.findViewById(R.id.layoutAccountInfo);
        layoutChangePassword = view.findViewById(R.id.layoutChangePassword);
        layoutDeliveryAddress = view.findViewById(R.id.layoutDeliveryAddress);
        layoutLogout = view.findViewById(R.id.layoutLogout);
        layoutNotification = view.findViewById(R.id.layoutNotification);
        layoutMessage = view.findViewById(R.id.layoutMessage);
        layoutCart = view.findViewById(R.id.layoutCart);
        layoutFailed = view.findViewById(R.id.layoutFailed);
        cardViewNotificationCount = view.findViewById(R.id.cardViewNotificationCount);
        cardViewMessageCount = view.findViewById(R.id.cardViewMessageCount);
        cardViewCartCount = view.findViewById(R.id.cardViewCartCount);
        layoutScreen = view.findViewById(R.id.layoutScreen);
        layoutLoading = view.findViewById(R.id.layoutLoading);

        setupView();

        layoutNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent messageIntent = new Intent(getContext(), NotificationActivity.class);
                messageIntent.putExtra("MAIN_TAB_POSITION", 2);
                startActivityForResult(messageIntent, IntegerUtils.REQUEST_COMMON);
            }
        });

        layoutMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent messageIntent = new Intent(getContext(), MessageListActivity.class);
                messageIntent.putExtra("MAIN_TAB_POSITION", 2);
                startActivityForResult(messageIntent, IntegerUtils.REQUEST_COMMON);
            }
        });

        layoutCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(getContext(), CartActivity.class);
                cartIntent.putExtra("MAIN_TAB_POSITION", 2);
                startActivityForResult(cartIntent, IntegerUtils.REQUEST_COMMON);
            }
        });

        layoutDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent discountIntent = new Intent(getContext(), DiscountActivity.class);
                discountIntent.putExtra("MAIN_TAB_POSITION", 2);
                startActivityForResult(discountIntent, IntegerUtils.REQUEST_COMMON);
            }
        });

        layoutAccountInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent accountInfoIntent = new Intent(getContext(), AccountInformationActivity.class);
                accountInfoIntent.putExtra("MAIN_TAB_POSITION", 2);
                startActivityForResult(accountInfoIntent, IntegerUtils.REQUEST_COMMON);
            }
        });

        layoutChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent accountInfoIntent = new Intent(getContext(), PasswordChangeActivity.class);
                accountInfoIntent.putExtra("MAIN_TAB_POSITION", 2);
                accountInfoIntent.putExtra("REQUEST_CODE",
                        IntegerUtils.REQUEST_PASSWORD_UPDATE);
                startActivityForResult(accountInfoIntent, IntegerUtils.REQUEST_COMMON);
            }
        });

        layoutDeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent accountInfoIntent = new Intent(getContext(), AddressListActivity.class);
                accountInfoIntent.putExtra("MAIN_TAB_POSITION", 2);
                startActivityForResult(accountInfoIntent, IntegerUtils.REQUEST_COMMON);
            }
        });

        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogBoxConfirm confirmLogoutBox = new DialogBoxConfirm(getActivity(),
                        StringUtils.MES_CONFIRM_LOG_OUT) {
                    @Override
                    public void onYesClicked() {
                        MethodUtils.logoutAction(getContext(), getActivity());
                    }
                };
                confirmLogoutBox.getWindow()
                        .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                confirmLogoutBox.show();
            }
        });
        lblRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupView();
            }
        });
    }

    private void setupView() {
        setLoadingState();
        if (getActivity() == null) {
            setFailedState();
        } else {
            sharedPreferences = getActivity().getSharedPreferences("PREFERENCE",
                    Context.MODE_PRIVATE);
            token = sharedPreferences.getString("TOKEN", "");
            if (!token.isEmpty()) {
                APIUserCaller.findUserByToken(token,
                        getActivity().getApplication(), new APIListener() {
                    @Override
                    public void onUserFound(User user, String message) {
                        user.setToken(token);
                        username = user.getUsername();
                        accountId = user.getAccountId();
                        googleId = user.getGoogleId();
                        txtProfileTabUsername.setText(user.getDisplayName());
                        Glide.with(getContext()).load(user.getAvatarLink()).into(imgAccountAvatar);
                        if (!googleId.isEmpty() && !googleId.equals("null") ) {
                            layoutChangePassword.setVisibility(View.GONE);
                        }
                        editCartCountByUser();
                        editUnreadMessageCountByUser();
                        editUnreadNotificationCountByUser();
                        setRealtimeFirebase();
                        setReadyState();
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        if (code == IntegerUtils.ERROR_NO_USER) {
                            MethodUtils.displayErrorAccountMessage(getContext(), getActivity());
                        } else {
                            setFailedState();
                        }
                    }
                });
            }
        }
    }

    private void setRealtimeFirebase() {
        firebaseReferences = new FirebaseReferences();
        firebaseReferences.getUserMessages(accountId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!messageLoading) {
                    editUnreadMessageCountByUser();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });
        firebaseReferences.getUserNotifications(accountId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!notificationLoading) {
                    editUnreadNotificationCountByUser();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });
    }

    private void editUnreadMessageCountByUser() {
        messageLoading = true;
        if (getActivity() == null) {
            setFailedState();
        } else {
            APIChatCaller.getCustomerChatMessages(token,
                    getActivity().getApplication(), new APIListener() {
                @Override
                public void onMessageListFound(List<Message> list) {
                    getUnreadMessagesCount(list);
                    if (messageCount == 0) {
                        cardViewMessageCount.setVisibility(View.INVISIBLE);
                    } else {
                        cardViewMessageCount.setVisibility(View.VISIBLE);
                        txtMessageCount.setText(messageCount + "");
                    }
                    messageLoading = false;
                }

                @Override
                public void onFailedAPICall(int code) {
                    if (code == IntegerUtils.ERROR_NO_USER) {
                        MethodUtils.displayErrorAccountMessage(getContext(), getActivity());
                    } else {
                        cardViewMessageCount.setVisibility(View.INVISIBLE);
                        messageLoading = false;
                    }
                }
            });
        }
    }

    private void editUnreadNotificationCountByUser() {
        notificationLoading = true;
        if (getActivity() == null) {
            setFailedState();
        } else {
            APINotificationCaller.getUsersNotifications(token,
                    getActivity().getApplication(), new APIListener() {
                @Override
                public void onNotificationListFound(List<Notification> list) {
                    getUnreadNotificationsCount(list);
                    if (notificationCount == 0) {
                        cardViewNotificationCount.setVisibility(View.INVISIBLE);
                    } else {
                        cardViewNotificationCount.setVisibility(View.VISIBLE);
                        txtNotificationCount.setText(notificationCount + "");
                    }
                    notificationLoading = false;
                }

                @Override
                public void onFailedAPICall(int code) {
                    if (code == IntegerUtils.ERROR_NO_USER) {
                        MethodUtils.displayErrorAccountMessage(getContext(), getActivity());
                    } else {
                        cardViewNotificationCount.setVisibility(View.INVISIBLE);
                        notificationLoading = false;
                    }
                }
            });
        }
    }

    private void getUnreadMessagesCount(List<Message> list) {
        messageCount = 0;
        if (list.size() > 0) {
            for (Message message : list) {
                if (message.getToId().equals(accountId) && !message.getMessageRead()) {
                    messageCount++;
                }
            }
        }
    }

    private void getUnreadNotificationsCount(List<Notification> list) {
        notificationCount = 0;
        if (list.size() > 0) {
            for (Notification notification : list) {
                if (!notification.getNotificationRead()) {
                    notificationCount++;
                }
            }
        }
    }

    private void editCartCountByUser() {
        try {
            retailList = (List<CartProduct>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("RETAIL_CART", ""));
            campaignList = (List<CartProduct>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("CAMPAIGN_CART", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (retailList == null && campaignList == null) {
            cardViewCartCount.setVisibility(View.INVISIBLE);
        } else {
            if (retailList.size() > 0 || campaignList.size() > 0) {
                cardViewCartCount.setVisibility(View.VISIBLE);
                cartCount = retailList.size() + campaignList.size();
                txtCartCount.setText(cartCount + "");
            } else {
                cardViewCartCount.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutFailed.setVisibility(View.GONE);
        layoutScreen.setVisibility(View.GONE);
    }

    private void setFailedState() {
        layoutLoading.setVisibility(View.GONE);
        layoutFailed.setVisibility(View.VISIBLE);
        layoutScreen.setVisibility(View.GONE);
    }

    private void setReadyState() {
        layoutLoading.setVisibility(View.GONE);
        layoutFailed.setVisibility(View.GONE);
        layoutScreen.setVisibility(View.VISIBLE);
    }

}