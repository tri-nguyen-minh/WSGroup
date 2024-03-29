package dev.wsgroup.main.views.activities.account;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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
import dev.wsgroup.main.views.dialogbox.DialogBoxOTP;

public class PhoneInputActivity extends AppCompatActivity {

    private Button btnSendOTP, btnSignIn;
    private EditText editPhone;
    private ImageView imgBackFromPhoneInput, imgPhoneInputHome;
    private LinearLayout layoutExistingAccount;
    private ConstraintLayout layoutParent;

    private int requestCode;
    private String errorMessage, phoneString;
    private DialogBoxLoading dialogBoxLoading;
    private DialogBoxAlert dialogBoxAlert;
    private User user;

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

        requestCode = getIntent().getIntExtra("REQUEST_CODE",
                                                IntegerUtils.REQUEST_REGISTER);

        if(requestCode != IntegerUtils.REQUEST_REGISTER) {
            layoutExistingAccount.setVisibility(View.INVISIBLE);
        }

        btnSendOTP.setEnabled(false);
        btnSendOTP.getBackground().setTint(getResources().getColor(R.color.gray_light));

        editPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    MethodUtils.hideKeyboard(view, getApplicationContext());
                }
            }
        });

        editPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phoneString = MethodUtils.formatPhoneWithCountryCode(editPhone.getText().toString());
                if(!phoneString.matches(StringUtils.PHONE_REGEX)) {
                    btnSendOTP.setEnabled(false);
                    btnSendOTP.getBackground()
                              .setTint(getResources()
                              .getColor(R.color.gray_light));
                } else {
                    btnSendOTP.setEnabled(true);
                    btnSendOTP.getBackground()
                              .setTint(getResources()
                              .getColor(R.color.blue_main));
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
                dialogBoxLoading.getWindow()
                                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                phoneString = MethodUtils.formatPhoneWithCountryCode(editPhone.getText().toString());
                APIUserCaller.findUserByPhoneNumber(phoneString,
                        getApplication(), new APIListener() {
                    @Override
                    public void onUserFound(User foundUser, String message) {
                        user = foundUser;
                        if(requestCode != IntegerUtils.REQUEST_REGISTER) {
                            if (foundUser.getGoogleId() == null
                                    || foundUser.getGoogleId().equals("null")) {
                                sendOTP();
                            } else {
                                errorMessage = StringUtils.MES_ALERT_PHONE_LINKED_GOOGLE;
                                displayError();
                            }
                        } else {
                            errorMessage = StringUtils.MES_ERROR_DUPLICATE_NUMBER;
                            displayError();
                        }
                    }
                    @Override
                    public void onFailedAPICall(int errorCode) {
                        switch (errorCode) {
                            case IntegerUtils.ERROR_API:
                            case IntegerUtils.ERROR_PARSING_JSON: {
                                errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                                displayError();
                                break;
                            }
                            case IntegerUtils.ERROR_NO_USER: {
                                if(requestCode != IntegerUtils.REQUEST_REGISTER) {
                                    errorMessage = StringUtils.MES_ERROR_NO_NUMBER_FOUND;
                                    displayError();
                                } else {
                                    user = new User();
                                    user.setPhoneNumber(phoneString);
                                    sendOTP();
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

    private void sendOTP() {
        DialogBoxOTP dialogBoxOTP = new DialogBoxOTP(PhoneInputActivity.this,
                getApplicationContext(), phoneString) {
            @Override
            public void onVerificationSuccessful() {
                DialogBoxAlert dialogBox = new DialogBoxAlert(PhoneInputActivity.this,
                        IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS,
                        StringUtils.MES_SUCCESSFUL_OTP,"") {
                            @Override
                            public void onClickAction() {
                                if (dialogBoxLoading.isShowing()) {
                                    dialogBoxLoading.dismiss();
                                }
                                Intent nextIntent;
                                if (requestCode == IntegerUtils.REQUEST_REGISTER) {
                                    nextIntent = new Intent(getApplicationContext(),
                                                            CompleteAccountActivity.class);
                                } else {
                                    nextIntent = new Intent(getApplicationContext(),
                                                            PasswordChangeActivity.class);
                                    nextIntent.putExtra("REQUEST_CODE",
                                                        IntegerUtils.REQUEST_PASSWORD_FORGET);
                                }
                                nextIntent.putExtra("USER", user);
                                nextIntent.putExtra("PHONE", phoneString);
                                startActivityForResult(nextIntent, requestCode);
                            }
                        };
                dialogBox.show();
                dismiss();
            }

            @Override
            public void onVerificationFailed() {
                errorMessage = StringUtils.MES_ERROR_INVALID_OTP;
                displayError();
            }

            @Override
            public void onClosingDialogBox() {
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
            }

            @Override
            public void onSendOTPFailed() {
                errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                displayError();
            }
        };
        dialogBoxOTP.getWindow()
                    .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxOTP.show();
    }

    private void displayError() {
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        dialogBoxAlert = new DialogBoxAlert(PhoneInputActivity.this,
                        IntegerUtils.CONFIRM_ACTION_CODE_FAILED, errorMessage,"");
        dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_CANCELED) {
            setResult(Activity.RESULT_CANCELED, getIntent());
            finish();
        } else if (resultCode == RESULT_OK) {
            if (requestCode == IntegerUtils.REQUEST_REGISTER) {
                getIntent().putExtra("USER", data.getSerializableExtra("USER"));
                setResult(Activity.RESULT_OK, getIntent());
            }
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        imgBackFromPhoneInput.performClick();
    }
}