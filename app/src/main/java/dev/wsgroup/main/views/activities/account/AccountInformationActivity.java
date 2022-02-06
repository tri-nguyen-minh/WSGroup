package dev.wsgroup.main.views.activities.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIUserCaller;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;

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
}