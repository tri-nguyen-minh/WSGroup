package dev.wsgroup.main.views.activities.account;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.net.URI;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIUserCaller;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxSelectImage;

public class AccountInformationActivity extends AppCompatActivity {

    private ImageView imgBackFromAccountInformation, imgAccountInfoHome, imgAccountAvatar;
    private CardView cardViewEditProfileAvatar;
    private EditText editAccountInfoUsername, editAccountInfoFirstName, editAccountInfoLastName,
                     editAccountInfoPhone, editAccountInfoMail;
    private Button btnSaveEdit;

    private SharedPreferences sharedPreferences;
    private String token, username, phone;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_information);
        this.getSupportActionBar().hide();

        imgBackFromAccountInformation = findViewById(R.id.imgBackFromAccountInformation);
        imgAccountInfoHome = findViewById(R.id.imgAccountInfoHome);
        imgAccountAvatar = findViewById(R.id.imgAccountAvatar);
        cardViewEditProfileAvatar = findViewById(R.id.cardViewEditProfileAvatar);
        editAccountInfoUsername = findViewById(R.id.editAccountInfoUsername);
        editAccountInfoFirstName = findViewById(R.id.editAccountInfoFirstName);
        editAccountInfoLastName = findViewById(R.id.editAccountInfoLastName);
        editAccountInfoPhone = findViewById(R.id.editAccountInfoPhone);
        editAccountInfoMail = findViewById(R.id.editAccountInfoMail);
        btnSaveEdit = findViewById(R.id.btnSaveEdit);

        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
        username = sharedPreferences.getString("USERNAME", "");
        phone = sharedPreferences.getString("PHONE", "");

        APIUserCaller.findUserByToken(token, getApplication(), new APIListener() {
            @Override
            public void onUserFound(User foundUser) {
                super.onUserFound(foundUser);
                user = foundUser;
                user.setToken(token);
                user.setUsername(username);
                user.setPhoneNumber(phone);
                editAccountInfoUsername.setText(username);
                editAccountInfoFirstName.setText(user.getFirstName());
                editAccountInfoLastName.setText(user.getLastName());
                editAccountInfoPhone.setText(user.getPhoneNumber());
                editAccountInfoMail.setText(user.getMail());
            }
        });

        editAccountInfoUsername.setEnabled(false);
        editAccountInfoPhone.setEnabled(false);
        editAccountInfoUsername.setTextColor(getResources().getColor(R.color.gray));
        editAccountInfoPhone.setTextColor(getResources().getColor(R.color.gray));

        imgBackFromAccountInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });
        imgAccountInfoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        cardViewEditProfileAvatar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                DialogBoxSelectImage dialogBox = new DialogBoxSelectImage(AccountInformationActivity.this) {
                    @Override
                    public void executeTakePhoto() {
                        super.executeTakePhoto();
                        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, 100);
                        } else {
                            takeImage();
                        }
                    }

                    @Override
                    public void executeSelectPhoto() {
                        super.executeSelectPhoto();
                        selectImage();
                    }
                };
                dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBox.show();
            }
        });
    }

    private boolean checkEmptyFields() {
        if(editAccountInfoFirstName.getText().toString().isEmpty()) {
            return true;
        }
        if (editAccountInfoLastName.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    private String checkValidInput() {
        if(!editAccountInfoMail.getText().toString().isEmpty()) {
            if (!editAccountInfoMail.getText().toString().matches(StringUtils.MAIL_REGEX)) {
                return StringUtils.MES_ERROR_INVALID_MAIL;
            }
        }
        return null;
    }

    public void takeImage() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, IntegerUtils.REQUEST_TAKE_IMAGE);
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IntegerUtils.REQUEST_SELECT_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takeImage();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(requestCode);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == IntegerUtils.REQUEST_TAKE_IMAGE) {
                System.out.println(data.getExtras().get("data"));
                imgAccountAvatar.setImageBitmap((Bitmap) data.getExtras().get("data"));
            } else if (requestCode == IntegerUtils.REQUEST_SELECT_IMAGE) {
                Uri uri = data.getData();
                System.out.println(uri.toString());
                imgAccountAvatar.setImageURI(uri);
            }
        }
    }
}