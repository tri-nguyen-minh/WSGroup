package dev.wsgroup.main.views.activities.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;

public class PasswordChangeActivity extends AppCompatActivity {

    private ImageView imgBackFromChangePassword, imgChangePasswordHome;
    private EditText editOldPassword, editNewPassword, editConfirmPassword;
    private Button btnSaveEdit;
    private ConstraintLayout layoutParent;

    private SharedPreferences sharedPreferences;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        this.getSupportActionBar().hide();

        imgBackFromChangePassword = findViewById(R.id.imgBackFromChangePassword);
        imgChangePasswordHome = findViewById(R.id.imgChangePasswordHome);
        editOldPassword = findViewById(R.id.editOldPassword);
        editNewPassword = findViewById(R.id.editNewPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        btnSaveEdit = findViewById(R.id.btnSaveEdit);
        layoutParent = findViewById(R.id.layoutParent);

        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("USERNAME", "");

        enableButtonUpdate();
        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(view);
                }
            }
        };

        imgBackFromChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });

        imgChangePasswordHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editOldPassword.hasFocus()) {
                    editOldPassword.clearFocus();
                }
                if (editNewPassword.hasFocus()) {
                    editNewPassword.clearFocus();
                }
                if (editConfirmPassword.hasFocus()) {
                    editConfirmPassword.clearFocus();
                }
            }
        });

        editOldPassword.setOnFocusChangeListener(listener);
        editNewPassword.setOnFocusChangeListener(listener);
        editConfirmPassword.setOnFocusChangeListener(listener);

        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private boolean checkEmptyFields() {
        if(editOldPassword.getText().toString().isEmpty()) {
            return true;
        }
        if (editNewPassword.getText().toString().isEmpty()) {
            return true;
        }
        if (editConfirmPassword.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    private boolean checkMatchingPassword() {
        String password = editNewPassword.getText().toString();
        String confirmPassword = editConfirmPassword.getText().toString();
        return password.equals(confirmPassword);
    }

    private void enableButtonUpdate() {
        if(checkEmptyFields() || !checkMatchingPassword()) {
            btnSaveEdit.setEnabled(false);
            btnSaveEdit.setBackground(getResources().getDrawable(R.color.gray_light));
        } else {
            btnSaveEdit.setEnabled(true);
            btnSaveEdit.setBackground(getResources().getDrawable(R.color.blue_main));
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}