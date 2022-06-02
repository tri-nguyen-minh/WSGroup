package dev.wsgroup.main.views.activities.account;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.textfield.TextInputEditText;

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

public class CompleteAccountActivity extends AppCompatActivity {

    private TextInputEditText editUsername, editFirstName, editLastName,
            editPassword, editPasswordConfirm, editPhoneNumber, editMail;
    private Button btnConfirm;
    private TextView txtLoginMethod;
    private ImageView imgBackFromAccountDetailRegister, imgAccountInfoHome;
    private ConstraintLayout layoutParent, layoutUsername;
    private LinearLayout layoutPassword;

    private User user;
    private String googleId, errorMessage, phone;
    private Activity activity;
    private DialogBoxLoading dialogBoxLoading;
    private DialogBoxAlert dialogBoxAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_account);
        this.getSupportActionBar().hide();

        btnConfirm = findViewById(R.id.btnConfirm);
        txtLoginMethod = findViewById(R.id.txtLoginMethod);
        editUsername = findViewById(R.id.editUsername);
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        editPassword = findViewById(R.id.editLoginPassword);
        editPasswordConfirm = findViewById(R.id.editPasswordConfirm);
        editMail = findViewById(R.id.editMail);
        imgBackFromAccountDetailRegister = findViewById(R.id.imgBackFromAccountDetailRegister);
        imgAccountInfoHome = findViewById(R.id.imgAccountInfoHome);
        layoutParent = findViewById(R.id.layoutParent);
        layoutUsername = findViewById(R.id.layoutUsername);
        layoutPassword = findViewById(R.id.layoutPassword);

        activity = this;
        user = (User) getIntent().getSerializableExtra("USER");
        if(user.getGoogleId() == null || user.getGoogleId().equals("null")) {
            txtLoginMethod.setText("WSGroup's Account");
            editPhoneNumber.setText(user.getPhoneNumber());
            editPhoneNumber.setEnabled(false);
            layoutUsername.setVisibility(View.VISIBLE);
            layoutPassword.setVisibility(View.VISIBLE);
        } else {
            txtLoginMethod.setText("Linked Google's Account");
            googleId = user.getGoogleId();
            editPhoneNumber.setText(StringUtils.VIETNAM_COUNTRY_CODE);
            layoutUsername.setVisibility(View.GONE);
            layoutPassword.setVisibility(View.GONE);
            if (user.getFirstName().isEmpty() || user.getFirstName().equals("null")) {
                editFirstName.setText("");
            } else {
                editFirstName.setText(user.getFirstName());
            }
            if (user.getLastName().isEmpty() || user.getLastName().equals("null")) {
                editFirstName.setText("");
            } else {
                editFirstName.setText(user.getLastName());
            }
            editMail.setText(user.getMail());
            editMail.setEnabled(false);
        }
        enableButtonConfirm();

        View.OnClickListener clearFocusListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editUsername.hasFocus()) {
                    editUsername.clearFocus();
                }
                if (editPassword.hasFocus()) {
                    editPassword.clearFocus();
                }
                if (editPasswordConfirm.hasFocus()) {
                    editPasswordConfirm.clearFocus();
                }
                if (editPhoneNumber.hasFocus()) {
                    editPhoneNumber.clearFocus();
                }
                if (editFirstName.hasFocus()) {
                    editFirstName.clearFocus();
                }
                if (editLastName.hasFocus()) {
                    editLastName.clearFocus();
                }
                if (editMail.hasFocus()) {
                    editMail.clearFocus();
                }
            }
        };

        layoutParent.setOnClickListener(clearFocusListener);
        txtLoginMethod.setOnClickListener(clearFocusListener);

        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    MethodUtils.hideKeyboard(view, getApplicationContext());
                }
            }
        };

        editUsername.setOnFocusChangeListener(focusChangeListener);
        editPassword.setOnFocusChangeListener(focusChangeListener);
        editPasswordConfirm.setOnFocusChangeListener(focusChangeListener);
        editPhoneNumber.setOnFocusChangeListener(focusChangeListener);
        editFirstName.setOnFocusChangeListener(focusChangeListener);
        editLastName.setOnFocusChangeListener(focusChangeListener);
        editMail.setOnFocusChangeListener(focusChangeListener);

        imgBackFromAccountDetailRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED, getIntent());
                finish();
            }
        });

        imgAccountInfoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableButtonConfirm();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        editUsername.addTextChangedListener(textWatcher);
        editFirstName.addTextChangedListener(textWatcher);
        editLastName.addTextChangedListener(textWatcher);
        editPassword.addTextChangedListener(textWatcher);
        editPasswordConfirm.addTextChangedListener(textWatcher);

        editPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phone = editPhoneNumber.getText().toString();
                if (phone.length() < 3 || !phone.startsWith(StringUtils.VIETNAM_COUNTRY_CODE)) {
                    editPhoneNumber.setText(StringUtils.VIETNAM_COUNTRY_CODE);
                    editPhoneNumber.setSelection(3);
                }
                enableButtonConfirm();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBoxLoading = new DialogBoxLoading(activity);
                dialogBoxLoading.getWindow()
                        .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                errorMessage = checkValidInput();
                if(errorMessage != null) {
                    if (dialogBoxLoading.isShowing()) {
                        dialogBoxLoading.dismiss();
                    }
                    displayErrorMessage();
                } else if (googleId == null) {
                    User newUser = new User();
                    newUser.setUsername(editUsername.getText().toString());
                    newUser.setPassword(editPassword.getText().toString());
                    newUser.setPhoneNumber(user.getPhoneNumber());
                    newUser.setFirstName(editFirstName.getText().toString());
                    newUser.setLastName(editLastName.getText().toString());
                    newUser.setMail(editMail.getText().toString());
                    newUser.setAvatarLink(user.getAvatarLink());
                    checkDuplicateMail(newUser);
                } else {
                    checkDuplicatePhone();
                }
            }
        });
    }

    private void checkDuplicateMail(User user) {
        APIUserCaller.findUserByMail(editMail.getText().toString(),
                getApplication(), new APIListener() {
            @Override
            public void onUserFound(User user, String message) {
                errorMessage = StringUtils.MES_ERROR_DUPLICATE_MAIL;
                displayErrorMessage();
            }
            @Override
            public void onFailedAPICall(int errorCode) {
                switch (errorCode) {
                    case IntegerUtils.ERROR_API:
                    case IntegerUtils.ERROR_PARSING_JSON: {
                        errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                        displayErrorMessage();
                        break;
                    }
                    case IntegerUtils.ERROR_NO_USER: {
                        registerUser(user);
                        break;
                    }
                }
            }
        });
    }

    private void checkDuplicatePhone() {
        phone = MethodUtils.formatPhoneWithCountryCode(editPhoneNumber.getText().toString());
        APIUserCaller.findUserByPhoneNumber(phone, getApplication(), new APIListener() {
            @Override
            public void onUserFound(User user, String message) {
                errorMessage = StringUtils.MES_ERROR_DUPLICATE_NUMBER;
                displayErrorMessage();
            }
            @Override
            public void onFailedAPICall(int errorCode) {
                switch (errorCode) {
                    case IntegerUtils.ERROR_API:
                    case IntegerUtils.ERROR_PARSING_JSON: {
                        errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                        displayErrorMessage();
                        break;
                    }
                    case IntegerUtils.ERROR_NO_USER: {
                        errorMessage = StringUtils.MES_ALERT_OTP_REQUIRE;
                        dialogBoxAlert = new DialogBoxAlert(activity,
                                IntegerUtils.CONFIRM_ACTION_CODE_ALERT,
                                errorMessage,"") {
                            @Override
                            public void onClickAction() {
                                user.setPhoneNumber(phone);
                                sendOTP();
                            }
                        };
                        dialogBoxAlert.show();
                        break;
                    }
                }
            }
        });
    }

    private void sendOTP() {
        DialogBoxOTP dialogBoxOTP = new DialogBoxOTP(activity,
                getApplicationContext(), user.getPhoneNumber()) {
            @Override
            public void onVerificationSuccessful() {
                DialogBoxAlert dialogBox = new DialogBoxAlert(activity,
                        IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS,
                        StringUtils.MES_SUCCESSFUL_OTP,"") {
                    @Override
                    public void onClickAction() {
                        updateUser();
                    }
                };
                dialogBox.show();
                dismiss();
            }

            @Override
            public void onVerificationFailed() {
                errorMessage = StringUtils.MES_ERROR_INVALID_OTP;
                displayErrorMessage();
            }

            @Override
            public void onClosingDialogBox() {
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
            }

            @Override
            public void onSendOTPFailed() {
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                MethodUtils.displayErrorAPIMessage(CompleteAccountActivity.this);
            }
        };
        dialogBoxOTP.getWindow()
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxOTP.show();
    }

    private void updateUser() {
        user.setFirstName(editFirstName.getText().toString());
        user.setLastName(editLastName.getText().toString());
        user.setMail(editMail.getText().toString());
        APIUserCaller.updateUserProfile(user, getApplication(), new APIListener() {
            @Override
            public void onUpdateSuccessful() {
                displaySuccessfulMessage();
            }

            @Override
            public void onFailedAPICall(int code) {
                errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                displayErrorMessage();
            }
        });
    }

    private void registerUser(User newUser) {
        APIUserCaller.registerNewUser(newUser, getApplication(), new APIListener() {
            @Override
            public void onUpdateSuccessful() {
                APIUserCaller.logInWithUsernameAndPassword(newUser.getUsername(),
                        newUser.getPassword(), getApplication(), new APIListener() {
                            @Override
                            public void onUserFound(User registeredUser, String message) {
                                user = registeredUser;
                                displaySuccessfulMessage();
                            }

                            @Override
                            public void onFailedAPICall(int errorCode) {
                                errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                                displayErrorMessage();
                            }
                        });
            }

            @Override
            public void onFailedAPICall(int errorCode) {
                if (errorCode == IntegerUtils.ERROR_PARSING_JSON) {
                    errorMessage = StringUtils.MES_ERROR_DUPLICATE_USERNAME;
                } else {
                    errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                }
                displayErrorMessage();
            }
        });
    }

    private void displaySuccessfulMessage() {
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        dialogBoxAlert = new DialogBoxAlert(activity, IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS,
                StringUtils.MES_SUCCESSFUL_REGISTRATION,"") {
            @Override
            public void onClickAction() {
                activity.getIntent().putExtra("USER", user);
                activity.setResult(Activity.RESULT_OK, activity.getIntent());
                activity.finish();
            }
        };
        dialogBoxAlert.getWindow()
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();
    }

    private void displayErrorMessage() {
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        dialogBoxAlert = new DialogBoxAlert(activity,
                IntegerUtils.CONFIRM_ACTION_CODE_FAILED, errorMessage,"");
        dialogBoxAlert.getWindow()
                      .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();
    }

    private void enableButtonConfirm() {
        if(checkEmptyFields()) {
            btnConfirm.setEnabled(false);
            btnConfirm.getBackground().setTint(getResources().getColor(R.color.gray_light));
        } else {
            btnConfirm.setEnabled(true);
            btnConfirm.getBackground().setTint(getResources().getColor(R.color.blue_main));
        }
    }

    private boolean checkEmptyFields() {
        if (googleId == null) {
            if(editUsername.getText().toString().isEmpty()) {
                return true;
            }
            if (editPassword.getText().toString().isEmpty()) {
                return true;
            }
            if (editPasswordConfirm.getText().toString().isEmpty()) {
                return true;
            }
        }
        if (editFirstName.getText().toString().isEmpty()) {
            return true;
        }
        if (editLastName.getText().toString().isEmpty()) {
            return true;
        }
        phone = editPhoneNumber.getText().toString();
        if (phone.equals(StringUtils.VIETNAM_COUNTRY_CODE)) {
            return true;
        }
        return false;
    }

    private String checkValidInput() {
        if (googleId == null) {
            if (!editUsername.getText().toString().matches(StringUtils.USERNAME_REGEX)) {
                return StringUtils.MES_ERROR_INVALID_USERNAME;
            }
            if (!editPassword.getText().toString().matches(StringUtils.PASSWORD_REGEX)) {
                return StringUtils.MES_ERROR_INVALID_PASSWORD;
            }
            if (!editPassword.getText().toString().equals(editPasswordConfirm.getText().toString())) {
                return StringUtils.MES_ERROR_INCORRECT_CONFIRM_PASSWORD;
            }
        }
        phone = MethodUtils.formatPhoneWithCountryCode(editPhoneNumber.getText().toString());
        if (!phone.matches(StringUtils.PHONE_REGEX)) {
            return StringUtils.MES_ERROR_INVALID_NUMBER;
        }
        if(!editMail.getText().toString().isEmpty()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(editMail.getText().toString()).matches()) {
                return StringUtils.MES_ERROR_INVALID_MAIL;
            }
        }
        return null;
    }
    @Override
    public void onBackPressed() {
        imgBackFromAccountDetailRegister.performClick();
    }
}