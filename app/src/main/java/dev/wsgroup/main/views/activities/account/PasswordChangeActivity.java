package dev.wsgroup.main.views.activities.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.RelativeLayout;

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

public class PasswordChangeActivity extends AppCompatActivity {

    private ImageView imgBackFromChangePassword, imgChangePasswordHome;
    private TextInputEditText editOldPassword, editNewPassword, editConfirmPassword;
    private Button btnSaveEdit;
    private ConstraintLayout layoutParent;
    private LinearLayout layoutOldPassword;

    private SharedPreferences sharedPreferences;
    private int requestCode;
    private String username, accountId, errorMessage;
    private DialogBoxLoading dialogBoxLoading;

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
        layoutOldPassword = findViewById(R.id.layoutOldPassword);

        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        requestCode = getIntent().getIntExtra("REQUEST_CODE", IntegerUtils.REQUEST_COMMON);
        username = sharedPreferences.getString("USERNAME", "");
        accountId = getIntent().getStringExtra("ACCOUNT_ID");

        if (requestCode == IntegerUtils.REQUEST_PASSWORD_UPDATE) {
            layoutOldPassword.setVisibility(View.VISIBLE);
        } else {
            layoutOldPassword.setVisibility(View.GONE);
        }
        enableButtonUpdate();

        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    MethodUtils.hideKeyboard(view, getApplicationContext());
                }
            }
        };
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                enableButtonUpdate();
            }

            @Override
            public void afterTextChanged(Editable editable) {

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
        editOldPassword.addTextChangedListener(textWatcher);
        editNewPassword.addTextChangedListener(textWatcher);
        editConfirmPassword.addTextChangedListener(textWatcher);

        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBoxLoading = new DialogBoxLoading(PasswordChangeActivity.this);
                dialogBoxLoading.getWindow()
                                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                errorMessage = checkValidInput();
                if(errorMessage != null) {
                    displayErrorMessage();
                } else {
                    String newPassword = editNewPassword.getText().toString();
                    if (requestCode == IntegerUtils.REQUEST_PASSWORD_UPDATE) {
                        String oldPassword = editOldPassword.getText().toString();
                        APIUserCaller.logInWithUsernameAndPassword(username, oldPassword,
                                                getApplication(), new APIListener() {
                            @Override
                            public void onUserFound(User user, String message) {
                                if (oldPassword.equals(newPassword)) {
                                    errorMessage = StringUtils.MES_ERROR_DUPLICATE_OLD_PASSWORD;
                                    displayErrorMessage();
                                } else {
                                    APIUserCaller.updatePassword(user.getAccountId(), newPassword,
                                            getApplication(), new APIListener() {
                                        @Override
                                        public void onUpdateSuccessful() {
                                            if (dialogBoxLoading.isShowing()) {
                                                dialogBoxLoading.dismiss();
                                            }
                                            displayFinalMessage();
                                        }

                                        @Override
                                        public void onFailedAPICall(int code) {
                                            errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                                            displayErrorMessage();
                                        }
                                    });
                                }
                            }
                            @Override
                            public void onFailedAPICall(int code) {
                                if (code == IntegerUtils.ERROR_API) {
                                    errorMessage = StringUtils.MES_ERROR_WRONG_OLD_PASSWORD;
                                } else {
                                    errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                                }
                                displayErrorMessage();
                            }
                        });
                    } else {
                        APIUserCaller.updatePassword(accountId, newPassword,
                                getApplication(), new APIListener() {
                            @Override
                            public void onUpdateSuccessful() {
                                if (dialogBoxLoading.isShowing()) {
                                    dialogBoxLoading.dismiss();
                                }
                                displayFinalMessage();
                            }

                            @Override
                            public void onFailedAPICall(int code) {
                                errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                                displayErrorMessage();
                            }
                        });
                    }
                }
            }
        });
    }

    private void displayFinalMessage() {
        DialogBoxAlert dialogBox
                = new DialogBoxAlert(PasswordChangeActivity.this,
                IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS,
                StringUtils.MES_SUCCESSFUL_UPDATE_PASSWORD,"") {
            @Override
            public void onClickAction() {
                setResult(RESULT_OK);
                finish();
            }
        };
        dialogBox.getWindow()
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBox.show();
    }

    private void displayErrorMessage() {
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        DialogBoxAlert dialogBoxAlert = new DialogBoxAlert(PasswordChangeActivity.this,
                IntegerUtils.CONFIRM_ACTION_CODE_FAILED, errorMessage,"");
        dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();
    }

    private boolean checkEmptyFields() {
        if(editOldPassword.getText().toString().isEmpty()
                            && requestCode == IntegerUtils.REQUEST_PASSWORD_UPDATE) {
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
            btnSaveEdit.getBackground()
                       .setTint(getResources()
                       .getColor(R.color.gray_light));
        } else {
            btnSaveEdit.setEnabled(true);
            btnSaveEdit.getBackground()
                       .setTint(getResources()
                       .getColor(R.color.blue_main));
        }
    }

    private String checkValidInput() {
        if (!editNewPassword.getText().toString().matches(StringUtils.PASSWORD_REGEX)) {
            return StringUtils.MES_ERROR_INVALID_PASSWORD;
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        imgBackFromChangePassword.performClick();
    }
}