package dev.wsgroup.main.views.fragments.tabs.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.callers.APIUserCaller;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Message;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.ObjectSerializer;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.CartActivity;
import dev.wsgroup.main.views.activities.DiscountActivity;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.activities.account.AccountInformationActivity;
import dev.wsgroup.main.views.activities.account.DeliveryAddressActivity;
import dev.wsgroup.main.views.activities.account.PasswordChangeActivity;
import dev.wsgroup.main.views.activities.message.MessageListActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;

public class ProfileTab extends Fragment {

    private ImageView imgAccountAvatar;
    private TextView txtProfileTabUsername, txtNotificationCount, txtMessageCount, txtCartCount;
    private ProgressBar progressBarLoading;
    private LinearLayout layoutProfileAvatar, layoutDiscount, layoutAccountInfo,
            layoutChangePassword, layoutDeliveryAddress, layoutLogout;
    private CardView cardViewNotificationCount, cardViewMessageCount, cardViewCartCount;
    private LinearLayout layoutNotification, layoutMessage, layoutCart;

    private SharedPreferences sharedPreferences;
    private List<CartProduct> retailList, campaignList;
    private List<Message> messageList;
    private String token, username;
    private int cartCount, messageCount;
    private GoogleSignInOptions options;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
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
        progressBarLoading = view.findViewById(R.id.progressBarLoading);
        layoutProfileAvatar = view.findViewById(R.id.layoutProfileAvatar);
        layoutDiscount = view.findViewById(R.id.layoutDiscount);
        layoutAccountInfo = view.findViewById(R.id.layoutAccountInfo);
        layoutChangePassword = view.findViewById(R.id.layoutChangePassword);
        layoutDeliveryAddress = view.findViewById(R.id.layoutDeliveryAddress);
        layoutLogout = view.findViewById(R.id.layoutLogout);
        cardViewNotificationCount = view.findViewById(R.id.cardViewNotificationCount);
        cardViewMessageCount = view.findViewById(R.id.cardViewMessageCount);
        cardViewCartCount = view.findViewById(R.id.cardViewCartCount);
        layoutNotification = view.findViewById(R.id.layoutNotification);
        layoutMessage = view.findViewById(R.id.layoutMessage);
        layoutCart = view.findViewById(R.id.layoutCart);

        username = sharedPreferences.getString("USERNAME","");
        token = sharedPreferences.getString("TOKEN","");

        progressBarLoading.setVisibility(View.VISIBLE);
        layoutProfileAvatar.setVisibility(View.INVISIBLE);
        cardViewCartCount.setVisibility(View.INVISIBLE);
        cardViewMessageCount.setVisibility(View.INVISIBLE);
        cardViewNotificationCount.setVisibility(View.INVISIBLE);

        APIUserCaller.findUserByToken(token, getActivity().getApplication(), new APIListener() {
            @Override
            public void onUserFound(User user, String message) {
                user.setUsername(username);
                setUpSimpleProfile(user);
                editCartCountByUser();
                editUnreadMessageCountByUser();
            }

            @Override
            public void onFailedAPICall(int errorCode) {
            }
        });

        layoutNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        layoutMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent messageIntent = new Intent(getContext(), MessageListActivity.class);
//                messageIntent.putExtra("MAIN_TAB_POSITION", 2);
//                startActivityForResult(messageIntent, IntegerUtils.REQUEST_COMMON);
            }
        });

        layoutCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(getActivity().getApplicationContext(), CartActivity.class);
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
                startActivityForResult(accountInfoIntent, IntegerUtils.REQUEST_COMMON);
            }
        });

        layoutDeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent accountInfoIntent = new Intent(getContext(), DeliveryAddressActivity.class);
                accountInfoIntent.putExtra("MAIN_TAB_POSITION", 2);
                startActivityForResult(accountInfoIntent, IntegerUtils.REQUEST_COMMON);
            }
        });

        layoutLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogBoxConfirm confirmLogoutBox = new DialogBoxConfirm(getActivity(), StringUtils.MES_CONFIRM_LOG_OUT) {
                    @Override
                    public void onYesClicked() {
                        sharedPreferences.edit().clear().apply();
                        options = new GoogleSignInOptions
                                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
                                .build();
                        GoogleSignIn.getClient(getContext(), options).signOut();
                        startActivity(new Intent(getContext(), MainActivity.class));
                    }
                };
                confirmLogoutBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                confirmLogoutBox.show();
            }
        });
    }

    private void setUpSimpleProfile(User user) {
        txtProfileTabUsername.setText(user.getFirstName() + " " + user.getLastName());
        Glide.with(getContext()).load(user.getAvatarLink()).into(imgAccountAvatar);
        progressBarLoading.setVisibility(View.INVISIBLE);
        layoutProfileAvatar.setVisibility(View.VISIBLE);
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
        if(retailList == null && campaignList == null) {
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

    private void editUnreadMessageCountByUser() {
        try {
            messageList = (List<Message>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("MESSAGE_LIST", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (messageList == null) {
            cardViewMessageCount.setVisibility(View.INVISIBLE);
        } else {
            if (messageList.size() == 0) {
                cardViewMessageCount.setVisibility(View.INVISIBLE);
            } else {
                messageCount = 0;
                for (Message message : messageList) {
                    if (!message.getMessageRead()) {
                        messageCount++;
                    }
                }
                if (messageCount > 0) {
                    cardViewMessageCount.setVisibility(View.VISIBLE);
                    txtMessageCount.setText(messageCount + "");
                } else {
                    cardViewMessageCount.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}