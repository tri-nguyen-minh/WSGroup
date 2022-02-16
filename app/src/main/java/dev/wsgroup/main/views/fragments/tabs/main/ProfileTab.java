package dev.wsgroup.main.views.fragments.tabs.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.File;
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
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;

public class ProfileTab extends Fragment {

    private ImageView imgAccountAvatar;
    private TextView txtProfileTabUsername;
    private LinearLayout layoutAccountInfo, layoutDeliveryAddress, layoutLogout;

    private SharedPreferences sharedPreferences;
    private String token;

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
        layoutAccountInfo = view.findViewById(R.id.layoutAccountInfo);
        layoutDeliveryAddress = view.findViewById(R.id.layoutDeliveryAddress);
        layoutLogout = view.findViewById(R.id.layoutLogout);

        token = sharedPreferences.getString("TOKEN", "");

//        String imageLink = sharedPreferences.getString("AVATAR", "");
//        System.out.println("test " + imageLink);
//        if (!imageLink.isEmpty()) {
//            System.out.println(imageLink);
//            System.out.println("end 2");
//        }

        APIUserCaller.findUserByToken(token, getActivity().getApplication(), new APIListener() {
            @Override
            public void onUserFound(User user) {
                super.onUserFound(user);
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
                Intent accountInfoIntent = new Intent(getActivity().getApplicationContext(), AccountInformationActivity.class);
                accountInfoIntent.putExtra("MAIN_TAB_POSITION", 2);
                startActivityForResult(accountInfoIntent, IntegerUtils.REQUEST_COMMON);
            }
        });

        layoutDeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent accountInfoIntent = new Intent(getActivity().getApplicationContext(), DeliveryAddressActivity.class);
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

        imgAccountAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                FirebaseStorage storage = FirebaseStorage.getInstance();
//                StorageReference storageReference = storage.getReference();
//                StorageReference ref = storageReference.child("images/fa8fa87e-b28b-483b-af80-37ba8674ec88");
//                StorageReference httpReference = f
//
//                Glide.with(getContext())
//                        .using(new FirebaseImageLoader())
//                        .load(storageReference)
//                        .asBitmap()
//                        .diskCacheStrategy(DiskCacheStrategy.NONE)
//                        .priority(Priority.IMMEDIATE)
//                        .into(new BitmapImageViewTarget(pic) {
//                            @Override
//                            protected void setResource(Bitmap resource) {
//                                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(context.getResources(),
//                                        Bitmap.createScaledBitmap(resource, 150, 150, false));
//                                drawable.setCircular(false);
//                                pic.setImageDrawable(drawable);
//                            }
//                        });

//                Task<Uri> urlTask = ref.getDownloadUrl();
//                urlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
//                    @Override
//                    public void onSuccess(Uri uri) {
//                        System.out.println(uri);
//                        imgAccountAvatar.setImageURI(uri);
//                    }
//                });
//                urlTask.addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(Exception e) {
//                        System.out.println("failed");
//                        System.out.println(e.getMessage());
//                    }
//                });

//                final long ONE_MEGABYTE = 1024 * 1024;
//                ref.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                    @Override
//                    public void onSuccess(byte[] bytes) {
//                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                        imgAccountAvatar.setImageBitmap(bmp);
//                    }
//                }).addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(Exception e) {
//                        System.out.println("failed");
//                        System.out.println(e.getMessage());
//                    }
//                });

            }
        });
    }

    private void setUpSimpleProfile(User user) {
        txtProfileTabUsername.setText(user.getFirstName() + " " + user.getLastName());
//        if (!user.getAvatarLink().isEmpty()) {
//            FirebaseAuth mAuth = FirebaseAuth.getInstance();
//            mAuth.signInAnonymously().addOnSuccessListener(new OnSuccessListener<AuthResult>() {
//                @Override
//                public void onSuccess(AuthResult authResult) {
//                    FirebaseStorage storage = FirebaseStorage.getInstance();
//                    StorageReference storageReference = storage.getReference();
//                    StorageReference ref = storageReference.child("images/" + user.getUsername() + "_avatar");
//                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            Glide.with(getContext()).load(uri).into(imgAccountAvatar);
//                        }
//                    });
//                }
//            });
//        }

        if (!user.getAvatarLink().isEmpty()) {
            String uriString = user.getAvatarLink();
            uriString = uriString.substring(1, uriString.length()-1);
//            Uri uri = Uri.parse(uriString);
            URI uri;
            Uri nUri = Uri.fromFile(new File(uriString));
            try {
                URL url = new URL(uriString);
                URLConnection conn = url.openConnection();
                Bitmap bm = null;
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
                uri = url.toURI();
                imgAccountAvatar.setImageBitmap(bm);
            } catch (Exception e) {
                System.out.println("error");
                e.printStackTrace();
            }
//            imgAccountAvatar.setImageURI(nUri);
//            Glide.with(getContext()).load(uri).into(imgAccountAvatar);
        }
    }
}