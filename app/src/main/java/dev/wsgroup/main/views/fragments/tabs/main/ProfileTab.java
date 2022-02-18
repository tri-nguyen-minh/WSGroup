package dev.wsgroup.main.views.fragments.tabs.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.callers.APIUserCaller;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.activities.account.AccountInformationActivity;
import dev.wsgroup.main.views.activities.account.DeliveryAddressActivity;
import dev.wsgroup.main.views.activities.account.PasswordChangeActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;

public class ProfileTab extends Fragment {

    private ImageView imgAccountAvatar;
    private TextView txtProfileTabUsername;
    private ProgressBar progressBarLoading;
    private LinearLayout layoutProfileAvatar,
            layoutAccountInfo, layoutChangePassword, layoutDeliveryAddress, layoutLogout;

    private SharedPreferences sharedPreferences;
    private String token, username;

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
        progressBarLoading = view.findViewById(R.id.progressBarLoading);
        layoutProfileAvatar = view.findViewById(R.id.layoutProfileAvatar);
        layoutChangePassword = view.findViewById(R.id.layoutChangePassword);
        layoutAccountInfo = view.findViewById(R.id.layoutAccountInfo);
        layoutDeliveryAddress = view.findViewById(R.id.layoutDeliveryAddress);
        layoutLogout = view.findViewById(R.id.layoutLogout);

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
            }

            @Override
            public void onFailedAPICall(int errorCode) {
                super.onFailedAPICall(errorCode);
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
                            Glide.with(getContext()).load(uri).into(imgAccountAvatar);
                            progressBarLoading.setVisibility(View.INVISIBLE);
                            layoutProfileAvatar.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            System.out.println("failure");
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
}