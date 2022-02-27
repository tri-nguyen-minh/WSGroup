package dev.wsgroup.main.views.activities.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIUserCaller;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class PasswordChangeActivity extends AppCompatActivity {

    private ImageView imgBackFromChangePassword, imgChangePasswordHome;
    private EditText editOldPassword, editNewPassword, editConfirmPassword;
    private Button btnSaveEdit;
    private ConstraintLayout layoutParent;

    private SharedPreferences sharedPreferences;
    private String username, token, errorMessage;
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

        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        username = sharedPreferences.getString("USERNAME", "");
        token = sharedPreferences.getString("TOKEN", "");

        enableButtonUpdate();
        View.OnFocusChangeListener listener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(view);
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
                dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                errorMessage = checkValidInput();
                if(errorMessage != null) {
                    dialogBoxLoading.dismiss();
                    alertError(errorMessage);
                } else {
                    String oldPassword = editOldPassword.getText().toString();
                    String newPassword = editNewPassword.getText().toString();
                    APIUserCaller.findUserByUsernameAndPassword(username, oldPassword, getApplication(), new APIListener() {
                        @Override
                        public void onUserFound(User user) {
                            super.onUserFound(user);
                            if (oldPassword.equals(newPassword)) {
                                errorMessage = StringUtils.MES_ERROR_DUPLICATE_OLD_PASSWORD;
                                dialogBoxLoading.dismiss();
                                alertError(errorMessage);
                            } else {
                                APIUserCaller.updatePassword(token, newPassword, getApplication(), new APIListener() {
                                    @Override
                                    public void onUpdateProfileSuccessful() {
                                        super.onUpdateProfileSuccessful();
                                        dialogBoxLoading.dismiss();
                                        DialogBoxAlert dialogBox = new DialogBoxAlert(PasswordChangeActivity.this,
                                                IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS,StringUtils.MES_SUCCESSFUL_UPDATE_PASSWORD,"") {
                                            @Override
                                            public void onClickAction() {
                                                super.onClickAction();
                                                imgBackFromChangePassword.performClick();
                                            }
                                        };
                                        dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        dialogBox.show();
                                    }

                                    @Override
                                    public void onFailedAPICall(int code) {
                                        super.onFailedAPICall(code);
                                        errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                                        dialogBoxLoading.dismiss();
                                        alertError(errorMessage);
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailedAPICall(int code) {
                            super.onFailedAPICall(code);
                            if (code == IntegerUtils.ERROR_API) {
                                errorMessage = StringUtils.MES_ERROR_WRONG_OLD_PASSWORD;
                            } else {
                                errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                            }
                            dialogBoxLoading.dismiss();
                            alertError(errorMessage);
                        }
                    });
                }
            }
        });
    }

    private void alertError(String error) {
        DialogBoxAlert dialogBoxAlert = new DialogBoxAlert(PasswordChangeActivity.this,
                IntegerUtils.CONFIRM_ACTION_CODE_FAILED, error,"");
        dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();
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
            btnSaveEdit.getBackground().setTint(getResources().getColor(R.color.gray_light));
        } else {
            btnSaveEdit.setEnabled(true);
            btnSaveEdit.getBackground().setTint(getResources().getColor(R.color.blue_main));
        }
    }

    private String checkValidInput() {
        if (!editNewPassword.getText().toString().matches(StringUtils.PASSWORD_REGEX)) {
            return StringUtils.MES_ERROR_INVALID_PASSWORD;
        }
        return null;
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}