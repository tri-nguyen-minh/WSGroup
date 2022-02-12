package dev.wsgroup.main.views.activities.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.callers.APIUserCaller;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;

public class SignInActivity extends AppCompatActivity {

    private Button btnSignIn, btnSignUp;
    private TextView txtForgetPassword;
    private EditText editUsername, editPassword;
    private ImageView imgDisplayPassword, imgBackFromSignIn, imgSignInfoHome;
    private ConstraintLayout layoutParent;

    private boolean passwordDisplayedFlag;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        this.getSupportActionBar().hide();

        btnSignIn = findViewById(R.id.btnConfirm);
        btnSignUp = findViewById(R.id.btnSignUp);
        txtForgetPassword = findViewById(R.id.txtForgetPassword);
        editUsername = findViewById(R.id.editLoginUsername);
        editPassword = findViewById(R.id.editLoginPassword);
        imgDisplayPassword = findViewById(R.id.imgDisplayPassword);
        imgBackFromSignIn = findViewById(R.id.imgBackFromSignIn);
        imgSignInfoHome = findViewById(R.id.imgSignInfoHome);
        layoutParent = findViewById(R.id.layoutParent);

        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        passwordDisplayedFlag = false;

        editUsername.setText("Customer");
        editPassword.setText("Password1!");

        editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        btnSignIn.setEnabled(false);
        btnSignIn.setBackground(getResources().getDrawable(R.color.gray_light));

        editUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        editPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUsername.clearFocus();
                editPassword.clearFocus();
            }
        });

        imgBackFromSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED, getIntent());
                finish();
            }
        });

        imgSignInfoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        editUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0) {
                    if (!editPassword.getText().toString().isEmpty()) {
                        btnSignIn.setEnabled(true);
                        btnSignIn.setBackground(getResources().getDrawable(R.color.blue_main));
                    }
                } else {
                    btnSignIn.setEnabled(false);
                    btnSignIn.setBackground(getResources().getDrawable(R.color.gray_light));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() > 0) {
                    if (!editUsername.getText().toString().isEmpty()) {
                        btnSignIn.setEnabled(true);
                        btnSignIn.setBackground(getResources().getDrawable(R.color.blue_main));
                    }
                } else {
                    btnSignIn.setEnabled(false);
                    btnSignIn.setBackground(getResources().getDrawable(R.color.gray_light));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        imgDisplayPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editPassword.getText().toString().isEmpty()) {
                    if (passwordDisplayedFlag) {
                        editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        imgDisplayPassword.setImageResource(R.drawable.ic_visibility_off);
                        passwordDisplayedFlag = false;
                    } else {
                        editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
                        imgDisplayPassword.setImageResource(R.drawable.ic_visibility_on);
                        passwordDisplayedFlag = true;
                    }
                }
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editUsername.getText().toString();
                String password = editPassword.getText().toString();
                if(username.isEmpty() || password.isEmpty()) {
                    DialogBoxAlert dialogBox =
                            new DialogBoxAlert(SignInActivity.this,
                                    IntegerUtils.CONFIRM_ACTION_CODE_FAILED, StringUtils.MES_ERROR_FIELD_EMPTY, "");
                    dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogBox.show();
                } else {
                    loginWithUsernameAndPassword(username, password);
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(getApplicationContext(), PhoneInputActivity.class);
                signUpIntent.putExtra("REQUEST_CODE", IntegerUtils.REQUEST_REGISTER);
                startActivityForResult(signUpIntent, IntegerUtils.REQUEST_REGISTER);
            }
        });

        txtForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUpIntent = new Intent(getApplicationContext(), PhoneInputActivity.class);
                signUpIntent.putExtra("REQUEST_CODE", IntegerUtils.REQUEST_CHANGE_PASSWORD);
                startActivityForResult(signUpIntent, IntegerUtils.REQUEST_CHANGE_PASSWORD);
            }
        });
    }

    private void loginWithUsernameAndPassword(String stringUsername, String stringPassword) {
        APIUserCaller.findUserByUsernameAndPassword(stringUsername, stringPassword, getApplication(), new APIListener() {
            @Override
            public void onUserFound(User user) {
                signInAccount(user);
            }

            @Override
            public void onFailedAPICall(int errorCode) {
                DialogBoxAlert dialogBox;
                if(errorCode == IntegerUtils.ERROR_API) {
                    dialogBox = new DialogBoxAlert(SignInActivity.this,
                            IntegerUtils.CONFIRM_ACTION_CODE_FAILED, StringUtils.MES_ERROR_FAILED_LOG_IN, "");
                } else {
                    dialogBox = new DialogBoxAlert(SignInActivity.this,
                            IntegerUtils.CONFIRM_ACTION_CODE_FAILED, StringUtils.MES_ERROR_FAILED_API_CALL, "");
                }
                dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBox.show();
            }
        });
    }

    private void signInAccount(User user) {
        editor = sharedPreferences.edit();
        editor.putString("USER_ID", user.getUserId());
        editor.putString("USERNAME", user.getUsername());
        editor.putString("PHONE", user.getPhoneNumber());
        editor.putString("PASSWORD", editPassword.getText().toString());
        editor.putString("TOKEN", user.getToken());
        editor.commit();
        setResult(Activity.RESULT_OK, getIntent());
        finish();
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            User user = (User) data.getSerializableExtra("USER");
            signInAccount(user);
        }
    }
}