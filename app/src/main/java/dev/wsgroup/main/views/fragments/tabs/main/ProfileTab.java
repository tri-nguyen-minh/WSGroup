package dev.wsgroup.main.views.fragments.tabs.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.callers.APIUserCaller;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Supplier;
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
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
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
    private List<Supplier> supplierRetailList, supplierCampaignList;
    private HashMap<String, List<CartProduct>> retailCart, campaignCart;
    private List<CartProduct> cartProductList;
    private String token, username;
    private int cartCount;

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

        APIUserCaller.findUserByToken(token, getActivity().getApplication(), new APIListener() {
            @Override
            public void onUserFound(User user) {
                super.onUserFound(user);
                user.setUsername(username);
                setUpSimpleProfile(user);
                editCartCountByUser();
            }

            @Override
            public void onFailedAPICall(int errorCode) {
                super.onFailedAPICall(errorCode);
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

            }
        });

        layoutCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent cartIntent = new Intent(getActivity().getApplicationContext(), CartActivity.class);
                startActivityForResult(cartIntent, IntegerUtils.REQUEST_COMMON);
            }
        });

        layoutDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent discountIntent = new Intent(getContext(), DiscountActivity.class);
                discountIntent.putExtra("MAIN_TAB_POSITION", 2);
                discountIntent.putExtra("IDENTIFIER", IntegerUtils.IDENTIFIER_DISCOUNT_VIEW);
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
                        sharedPreferences.edit().clear().commit();
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
        if (!user.getAvatarLink().isEmpty()) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReference();
                    StorageReference ref = storageReference.child(user.getAvatarLink());
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            try {
                                Glide.with(getContext()).load(uri).into(imgAccountAvatar);
                                progressBarLoading.setVisibility(View.INVISIBLE);
                                layoutProfileAvatar.setVisibility(View.VISIBLE);
                            } catch (Exception e) {
                                imgAccountAvatar.setImageResource(R.drawable.ic_profile_circle);
                                progressBarLoading.setVisibility(View.INVISIBLE);
                                layoutProfileAvatar.setVisibility(View.VISIBLE);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            imgAccountAvatar.setImageResource(R.drawable.ic_profile_circle);
                            progressBarLoading.setVisibility(View.INVISIBLE);
                            layoutProfileAvatar.setVisibility(View.VISIBLE);
                            e.printStackTrace();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(Exception e) {
                    DialogBoxAlert dialogBox = new DialogBoxAlert(getActivity(),
                            IntegerUtils.CONFIRM_ACTION_CODE_FAILED, StringUtils.MES_ERROR_FAILED_API_CALL,"");
                    dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogBox.show();
                    imgAccountAvatar.setImageResource(R.drawable.ic_profile_circle);
                    progressBarLoading.setVisibility(View.INVISIBLE);
                    layoutProfileAvatar.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }
            });
        }
    }

    private int getRetailCartCount() {
        int count = 0;
        for (Supplier supplier : supplierRetailList) {
            cartProductList = retailCart.get(supplier.getId());
            count += cartProductList.size();
        }
        return count;
    }

    private int getCampaignCartCount() {
        int count = 0;
        for (Supplier supplier : supplierCampaignList) {
            cartProductList = campaignCart.get(supplier.getId());
            count += cartProductList.size();
        }
        return count;
    }

    private void editCartCountByUser() {
        try {
            retailCart = (HashMap<String, List<CartProduct>>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("RETAIL_CART", ""));
            campaignCart = (HashMap<String, List<CartProduct>>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("CAMPAIGN_CART", ""));
            supplierRetailList = (ArrayList<Supplier>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("SUPPLIER_RETAIL_LIST", ""));
            supplierCampaignList = (ArrayList<Supplier>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("SUPPLIER_CAMPAIGN_LIST", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(supplierRetailList == null && supplierCampaignList == null) {
            cardViewCartCount.setVisibility(View.INVISIBLE);
        } else {
            if (supplierRetailList.size() > 0 || supplierCampaignList.size() > 0) {
                cartCount = 0;
                cardViewCartCount.setVisibility(View.VISIBLE);
                if (supplierRetailList.size() > 0) {
                    cartCount += getRetailCartCount();
                }
                if (supplierCampaignList.size() > 0) {
                    cartCount += getCampaignCartCount();
                }
                txtCartCount.setText(cartCount + "");
            } else {
                cardViewCartCount.setVisibility(View.INVISIBLE);
            }
        }
    }
}