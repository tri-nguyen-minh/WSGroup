package dev.wsgroup.main.views.activities.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.utils.StringUtils;

public class PasswordChangeActivity extends AppCompatActivity {

    private TextView btnSignIn, txtPasswordError;
    private EditText editPassword, editPasswordConfirm;

    private String errorMessage, phoneNumber;
    private boolean passwordDisplayed, passwordConfirmedDisplayed;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        this.getSupportActionBar().hide();

        btnSignIn = findViewById(R.id.btnConfirm);
        editPassword = findViewById(R.id.editLoginPassword);
        editPasswordConfirm = findViewById(R.id.editPasswordConfirm);
        ImageView imgDisplayPassword = findViewById(R.id.imgDisplayPassword);
        ImageView imgDisplayPasswordConfirm = findViewById(R.id.imgDisplayPasswordConfirm);
        CardView cardViewBackFromPassword = findViewById(R.id.cardViewBackFromPassword);

        activity = this;
        passwordDisplayed = false;
        passwordConfirmedDisplayed = false;
        phoneNumber = getIntent().getStringExtra("PHONE");

        txtPasswordError.setVisibility(View.INVISIBLE);
        editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editPasswordConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        cardViewBackFromPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED, getIntent());
                finish();
            }
        });

        imgDisplayPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editPassword.getText().toString().isEmpty()) {
                    if (passwordDisplayed) {
                        editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        imgDisplayPassword.setImageResource(R.drawable.ic_visibility_off);
                        passwordDisplayed = false;
                    } else {
                        editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                        imgDisplayPassword.setImageResource(R.drawable.ic_visibility_on);
                        passwordDisplayed = true;
                    }
                }
            }
        });

        imgDisplayPasswordConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editPasswordConfirm.getText().toString().isEmpty()) {
                    if (passwordConfirmedDisplayed) {
                        editPasswordConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        imgDisplayPasswordConfirm.setImageResource(R.drawable.ic_visibility_off);
                        passwordConfirmedDisplayed = false;
                    } else {
                        editPasswordConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                        imgDisplayPasswordConfirm.setImageResource(R.drawable.ic_visibility_on);
                        passwordConfirmedDisplayed = true;
                    }
                }
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtPasswordError.setVisibility(View.INVISIBLE);
                errorMessage = checkValidInput();
                if(errorMessage.isEmpty()) {

//                    API change password

                } else {
                    txtPasswordError.setText(errorMessage);
                    txtPasswordError.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private String checkValidInput() {
        if (editPassword.getText().toString().isEmpty()) {
            return "Please fill the required fields";
        }
        if (!editPassword.getText().toString().matches(StringUtils.PASSWORD_REGEX)) {
            return "This password is invalid";
        }
        if (editPasswordConfirm.getText().toString().isEmpty()) {
            return "Please fill the required fields";
        }
        if (!editPassword.getText().toString().equals(editPasswordConfirm.getText().toString())) {
            return "Please re-enter your password correctly";
        }
        return "";
    }
}