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

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIUserCaller;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class SignInActivity extends AppCompatActivity {

    private Button btnSignIn, btnSignUp;
    private TextView txtForgetPassword;
    private EditText editUsername, editPassword;
    private ImageView imgDisplayPassword, imgBackFromSignIn, imgSignInfoHome, imgGoogle;
    private ConstraintLayout layoutParent;

    private boolean passwordDisplayedFlag;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private DialogBoxLoading dialogBoxLoading;
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
        editUsername = findViewById(R.id.editLoginUsername);
        editPassword = findViewById(R.id.editLoginPassword);
        imgDisplayPassword = findViewById(R.id.imgDisplayPassword);
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
        editUsername.setText("Customer");
        editPassword.setText("Password1!");

        editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
                    btnSignIn.getBackground().setTint(getResources().getColor(R.color.blue_main));
                } else {
                    btnSignIn.setEnabled(false);
                    btnSignIn.getBackground().setTint(getResources().getColor(R.color.gray_light));
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
                    hideKeyboard(view);
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
        editUsername.setOnFocusChangeListener(listener);
        editPassword.setOnFocusChangeListener(listener);

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

        imgGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordDisplayedFlag = false;
                Intent signInIntent = client.getSignInIntent();
                startActivityForResult(signInIntent, REQUEST_GOOGLE_SIGN_IN);
            }
        });
    }

    private void loginWithUsernameAndPassword(String stringUsername, String stringPassword) {
        dialogBoxLoading = new DialogBoxLoading(SignInActivity.this);
        dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxLoading.show();
        APIUserCaller.logInWithUsernameAndPassword(stringUsername, stringPassword, getApplication(), new APIListener() {
            @Override
            public void onUserFound(User user, String message) {
                signInAccount(user);
            }

            @Override
            public void onFailedAPICall(int errorCode) {
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
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
//        editor.putString("USERNAME", user.getUsername());
        editor.putString("PHONE", user.getPhoneNumber());
        editor.putString("PASSWORD", editPassword.getText().toString());
        editor.putString("TOKEN", user.getToken());
        editor.commit();
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        setResult(Activity.RESULT_OK, getIntent());
        finish();
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean checkEmptyField() {
        if (editUsername.getText().toString().isEmpty() || editPassword.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    private void loginGoogle(User user) {
        APIUserCaller.loginWithGoogle(user, getApplication(), new APIListener() {
            @Override
            public void onUserFound(User user, String message) {
                System.out.println(message);
                System.out.println(user.getGoogleId());
                System.out.println(user.getFirstName());
                System.out.println(user.getLastName());
                System.out.println(user.getPhoneNumber());
                signInAccount(user);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GOOGLE_SIGN_IN) {
            dialogBoxLoading = new DialogBoxLoading(SignInActivity.this);
            dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogBoxLoading.show();
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount googleAccount = task.getResult();
                User user = new User();
                user.setGoogleId(googleAccount.getId());
                user.setFirstName(googleAccount.getGivenName());
                user.setLastName(googleAccount.getFamilyName());
                user.setPhoneNumber("1");
                user.setMail(googleAccount.getEmail());
                loginGoogle(user);
//                System.out.println(googleAccount.getIdToken());
//                System.out.println(googleAccount.getDisplayName());
//                System.out.println(googleAccount.getEmail());
//                System.out.println(googleAccount.getFamilyName());
//                System.out.println(googleAccount.getGivenName());
//                System.out.println(googleAccount.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (resultCode == RESULT_OK) {
            User user = (User) data.getSerializableExtra("USER");
            signInAccount(user);
        }
    }
}