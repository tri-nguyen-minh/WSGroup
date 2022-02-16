package dev.wsgroup.main.views.activities.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIUserCaller;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.services.FirebasePhoneAuthService;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;
import dev.wsgroup.main.views.dialogbox.DialogBoxOTP;

public class PhoneInputActivity extends AppCompatActivity {

    private Button btnSendOTP, btnSignIn;
    private EditText editPhone;
    private ImageView imgBackFromPhoneInput, imgPhoneInputHome;
    private LinearLayout layoutExistingAccount;
    private ConstraintLayout layoutParent;

    private int requestCode;
    private String errorMessage;
    private DialogBoxLoading dialogBoxLoading;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_input);
        this.getSupportActionBar().hide();

        btnSendOTP = findViewById(R.id.btnSendOTP);
        btnSignIn = findViewById(R.id.btnConfirm);
        editPhone = findViewById(R.id.editPhoneSignUp);
        imgBackFromPhoneInput = findViewById(R.id.imgBackFromPhoneInput);
        imgPhoneInputHome = findViewById(R.id.imgPhoneInputHome);
        layoutExistingAccount = findViewById(R.id.layoutExistingAccount);
        layoutParent = findViewById(R.id.layoutParent);

        requestCode = getIntent().getIntExtra("REQUEST_CODE", IntegerUtils.REQUEST_REGISTER);

        editPhone.setText("091392173");
        btnSendOTP.setEnabled(false);
        btnSendOTP.setBackground(getResources().getDrawable(R.color.gray_light));

        editPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        editPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String stringPhone = "0" + editPhone.getText().toString();
                if(!stringPhone.matches(StringUtils.PHONE_REGEX)) {
                    btnSendOTP.setEnabled(false);
                    btnSendOTP.setBackground(getResources().getDrawable(R.color.gray_light));
                } else {
                    btnSendOTP.setEnabled(true);
                    btnSendOTP.setBackground(getResources().getDrawable(R.color.blue_main));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editPhone.hasFocus()) {
                    editPhone.clearFocus();
                }
            }
        });

        if(requestCode != IntegerUtils.REQUEST_REGISTER) {
            layoutExistingAccount.setVisibility(View.INVISIBLE);
        }

        imgBackFromPhoneInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED, getIntent());
                finish();
            }
        });
        imgPhoneInputHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        btnSendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBoxLoading = new DialogBoxLoading(PhoneInputActivity.this);
                dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                String stringPhone = "0" + editPhone.getText().toString();

                APIUserCaller.findUserByPhoneNumber(stringPhone, getApplication(), new APIListener() {
                    @Override
                    public void onUserFound(User user) {
                        super.onUserFound(user);
                        if(requestCode != IntegerUtils.REQUEST_REGISTER) {
                            sendOTP(stringPhone);
                        } else {
                            if (dialogBoxLoading.isShowing()) {
                                dialogBoxLoading.dismiss();
                            }
                            errorMessage = StringUtils.MES_ERROR_DUPLICATE_NUMBER;
                            displayError();
                        }
                    }
                    @Override
                    public void onFailedAPICall(int errorCode) {
                        super.onFailedAPICall(errorCode);
                        switch (errorCode) {
                            case IntegerUtils.ERROR_API:
                            case IntegerUtils.ERROR_PARSING_JSON: {
                                if (dialogBoxLoading.isShowing()) {
                                    dialogBoxLoading.dismiss();
                                }
                                errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                                displayError();
                                break;
                            }
                            case IntegerUtils.ERROR_NO_USER: {
                                if (dialogBoxLoading.isShowing()) {
                                    dialogBoxLoading.dismiss();
                                }
                                if(requestCode != IntegerUtils.REQUEST_REGISTER) {
                                    errorMessage = StringUtils.MES_ERROR_NO_NUMBER_FOUND;
                                    displayError();
                                } else {
                                    sendOTP(stringPhone);
                                }
                                break;
                            }
                        }
                    }
                });
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED, getIntent());
                finish();
            }
        });
    }

    private void sendOTP(String stringPhone) {
        DialogBoxOTP dialogBoxOTP =
                new DialogBoxOTP(PhoneInputActivity.this, getApplicationContext(), stringPhone, requestCode);
        dialogBoxOTP.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxOTP.show();
    }

    private void displayError() {
        DialogBoxAlert dialogBox =
                new DialogBoxAlert(PhoneInputActivity.this,
                        IntegerUtils.CONFIRM_ACTION_CODE_FAILED, errorMessage,"");
        dialogBox.show();
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CANCELED) {
            setResult(Activity.RESULT_CANCELED, getIntent());
            finish();
        } else if (resultCode == RESULT_OK) {
            getIntent().putExtra("USER", (User) data.getSerializableExtra("USER"));
            setResult(Activity.RESULT_OK, getIntent());
            finish();
        }
    }
}