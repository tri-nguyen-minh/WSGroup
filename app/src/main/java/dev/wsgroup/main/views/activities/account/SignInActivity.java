package dev.wsgroup.main.views.activities.account;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIUserCaller;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class SignInActivity extends AppCompatActivity {

    private Button btnSignIn, btnSignUp;
    private TextView txtForgetPassword;
    private TextInputLayout inputLoginUsername, inputLoginPassword;
    private TextInputEditText editUsername, editPassword;
    private ImageView imgBackFromSignIn, imgSignInfoHome, imgGoogle;
    private ConstraintLayout layoutParent;

    private SharedPreferences sharedPreferences;
    private DialogBoxLoading dialogBoxLoading;
    private DialogBoxAlert dialogBoxAlert;
    private GoogleSignInOptions options;
    private GoogleSignInClient client;
    private final int REQUEST_GOOGLE_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        this.getSupportActionBar().hide();

        btnSignIn = findViewById(R.id.btnConfirm);
        btnSignUp = findViewById(R.id.btnSignUp);
        txtForgetPassword = findViewById(R.id.txtForgetPassword);
        inputLoginUsername = findViewById(R.id.inputLoginUsername);
        inputLoginPassword = findViewById(R.id.inputLoginPassword);
        editUsername = findViewById(R.id.editLoginUsername);
        editPassword = findViewById(R.id.editLoginPassword);
        imgBackFromSignIn = findViewById(R.id.imgBackFromSignIn);
        imgSignInfoHome = findViewById(R.id.imgSignInfoHome);
        imgGoogle = findViewById(R.id.imgGoogle);
        layoutParent = findViewById(R.id.layoutParent);

        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        options = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        client = GoogleSignIn.getClient(getApplicationContext(), options);
        btnSignIn.setEnabled(false);
        btnSignIn.getBackground().setTint(getResources().getColor(R.color.gray_light));


        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!checkEmptyField()) {
                    btnSignIn.setEnabled(true);
                    btnSignIn.getBackground()
                             .setTint(getResources()
                             .getColor(R.color.blue_main));
                } else {
                    btnSignIn.setEnabled(false);
                    btnSignIn.getBackground()
                             .setTint(getResources()
                             .getColor(R.color.gray_light));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    MethodUtils.hideKeyboard(view, getApplicationContext());
                }
            }
        };


        layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editUsername.hasFocus()) {
                    editUsername.clearFocus();
                }
                if (editPassword.hasFocus()) {
                    editPassword.clearFocus();
                }
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

        editUsername.addTextChangedListener(textWatcher);
        editPassword.addTextChangedListener(textWatcher);
        editUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    MethodUtils.hideKeyboard(view, getApplicationContext());
                } else {
                }
            }
        });
        editPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    MethodUtils.hideKeyboard(view, getApplicationContext());
                } else {
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
                                    IntegerUtils.CONFIRM_ACTION_CODE_FAILED,
                                    StringUtils.MES_ERROR_FIELD_EMPTY, "");
                    dialogBox.getWindow()
                             .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
                signUpIntent.putExtra("REQUEST_CODE", IntegerUtils.REQUEST_PASSWORD_FORGET);
                startActivityForResult(signUpIntent, IntegerUtils.REQUEST_PASSWORD_FORGET);
            }
        });

        imgGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = client.getSignInIntent();
                startActivityForResult(signInIntent, REQUEST_GOOGLE_SIGN_IN);
            }
        });
    }

    private void loginWithUsernameAndPassword(String stringUsername, String stringPassword) {
        dialogBoxLoading = new DialogBoxLoading(SignInActivity.this);
        dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxLoading.show();
        APIUserCaller.logInWithUsernameAndPassword(stringUsername, stringPassword,
                getApplication(), new APIListener() {
            @Override
            public void onUserFound(User user, String message) {
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                signInAccount(user);
            }

            @Override
            public void onNoJSONFound() {
                displayAlert(StringUtils.MES_ERROR_FAILED_LOG_IN);
            }

            @Override
            public void onFailedAPICall(int errorCode) {
                displayAlert(StringUtils.MES_ERROR_FAILED_API_CALL);
            }
        });
    }

    private void signInAccount(User user) {
        sharedPreferences.edit()
                .putString("USERNAME", user.getUsername())
                .putString("GOOGLE_ID", user.getGoogleId())
                .putString("USER_ID", user.getUserId())
                .putString("ACCOUNT_ID", user.getAccountId())
                .putString("PHONE", user.getPhoneNumber())
                .putString("PASSWORD", editPassword.getText().toString())
                .putString("TOKEN", user.getToken())
                .commit();
        setResult(Activity.RESULT_OK, getIntent());
        finish();
    }

    private boolean checkEmptyField() {
        if (editUsername.getText().toString().isEmpty()
                || editPassword.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    private void loginGoogle(User user) {
        APIUserCaller.loginWithGoogle(user, getApplication(), new APIListener() {
            @Override
            public void onUserFound(User user, String message) {
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                if (user.getPhoneNumber().equals(user.getGoogleId())) {
                    dialogBoxAlert = new DialogBoxAlert(SignInActivity.this,
                            IntegerUtils.CONFIRM_ACTION_CODE_ALERT,
                            StringUtils.MES_ALERT_INCOMPLETE_ACCOUNT,
                            StringUtils.DESC_REQUIRE_COMPLETE_ACCOUNT) {
                        @Override
                        public void onClickAction() {
                            GoogleSignIn.getClient(getApplicationContext(), options).signOut();
                            Intent nextIntent = new  Intent(getApplicationContext(),
                                    CompleteAccountActivity.class);
                            nextIntent.putExtra("USER", user);
                            startActivityForResult(nextIntent, IntegerUtils.REQUEST_REGISTER);
                        }
                    };
                    dialogBoxAlert.show();
                } else {
                    signInAccount(user);
                }
            }

            @Override
            public void onFailedAPICall(int code) {
                displayAlert(StringUtils.MES_ERROR_FAILED_API_CALL);
                GoogleSignIn.getClient(getApplicationContext(), options).signOut();
            }
        });
    }

    private void displayAlert(String message) {
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        dialogBoxAlert = new DialogBoxAlert(SignInActivity.this,
                IntegerUtils.CONFIRM_ACTION_CODE_FAILED, message, "");
        dialogBoxAlert.getWindow()
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GOOGLE_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                dialogBoxLoading = new DialogBoxLoading(SignInActivity.this);
                dialogBoxLoading.getWindow()
                        .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount googleAccount = task.getResult();
                    User newUser = new User();
                    newUser.setGoogleId(googleAccount.getId());
                    newUser.setFirstName(googleAccount.getGivenName());
                    newUser.setLastName(googleAccount.getFamilyName());
                    newUser.setPhoneNumber(googleAccount.getId());
                    newUser.setMail(googleAccount.getEmail());
                    APIUserCaller.findUserByMail(googleAccount.getEmail(),
                            getApplication(), new APIListener() {
                        @Override
                        public void onUserFound(User foundUser, String message) {
                            if (foundUser.getGoogleId().equals(googleAccount.getId())) {
                                loginGoogle(newUser);
                            } else {
                                displayAlert(StringUtils.MES_ERROR_DUPLICATE_MAIL);
                                GoogleSignIn.getClient(getApplicationContext(), options).signOut();
                            }
                        }

                        @Override
                        public void onFailedAPICall(int code) {
                            if (code == IntegerUtils.ERROR_NO_USER) {
                                loginGoogle(newUser);
                            } else {
                                displayAlert(StringUtils.MES_ERROR_FAILED_API_CALL);
                                GoogleSignIn.getClient(getApplicationContext(), options).signOut();
                            }
                        }
                    });
                } catch (Exception e) {
                    displayAlert(StringUtils.MES_ERROR_FAILED_API_CALL);
                    GoogleSignIn.getClient(getApplicationContext(), options).signOut();
                    e.printStackTrace();
                }
            }
        } else if (resultCode == RESULT_OK) {
            if (requestCode == IntegerUtils.REQUEST_REGISTER) {
                User user = (User) data.getSerializableExtra("USER");
                signInAccount(user);
            }
        }
    }

    @Override
    public void onBackPressed() {
        imgBackFromSignIn.performClick();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GoogleSignIn.getClient(getApplicationContext(), options).signOut();
    }
}