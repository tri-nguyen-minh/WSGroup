package dev.wsgroup.main.views.activities.account;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.callers.APIUserCaller;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class AccountDetailActivity extends AppCompatActivity {

    private EditText editUsername, editFirstName, editLastName,
            editPassword, editPasswordConfirm, editPhoneNumber, editMail;
    private TextView btnConfirm;
    private ImageView imgDisplayPassword, imgDisplayPasswordConfirm,
            imgBackFromAccountDetailRegister, imgAccountInfoHome;
    private ConstraintLayout layoutParent;

    private boolean passwordDisplayed, passwordConfirmedDisplayed;
    private String errorMessage;
    private Activity activity;
    private DialogBoxLoading dialogBoxLoading;
    private DialogBoxAlert dialogBoxAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_detail);
        this.getSupportActionBar().hide();

        btnConfirm = findViewById(R.id.btnConfirm);
        editUsername = findViewById(R.id.editUsername);
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        editPassword = findViewById(R.id.editLoginPassword);
        editPasswordConfirm = findViewById(R.id.editPasswordConfirm);
        editMail = findViewById(R.id.editMail);
        imgDisplayPassword = findViewById(R.id.imgDisplayPassword);
        imgDisplayPasswordConfirm = findViewById(R.id.imgDisplayPasswordConfirm);
        imgBackFromAccountDetailRegister = findViewById(R.id.imgBackFromAccountDetailRegister);
        imgAccountInfoHome = findViewById(R.id.imgAccountInfoHome);
        layoutParent = findViewById(R.id.layoutParent);

        activity = this;
        passwordDisplayed = false;
        passwordConfirmedDisplayed = false;
        String phoneNumber = getIntent().getStringExtra("PHONE");
        if(phoneNumber != null) {
            String phoneDisplay = MethodUtils.formatPhoneNumber(phoneNumber);
            editPhoneNumber.setText(MethodUtils.formatPhoneNumberWithCountryCode(phoneDisplay));
            editPhoneNumber.setEnabled(false);
        }

        editPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editPasswordConfirm.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        btnConfirm.setEnabled(false);

        editPassword.setText("Password1!");
        editPasswordConfirm.setText("Password1!");
        editFirstName.setText("first");
        editLastName.setText("last");
        editMail.setText("mail@gmail.com");

        layoutParent.setOnClickListener(new View.OnClickListener() {
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
        });
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
        editPasswordConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        editPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        editFirstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        editLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        editMail.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

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

        editUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableButtonConfirm();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        editFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableButtonConfirm();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        editLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableButtonConfirm();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        editPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableButtonConfirm();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableButtonConfirm();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        editPasswordConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableButtonConfirm();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBoxLoading = new DialogBoxLoading(AccountDetailActivity.this);
                dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                errorMessage = checkValidInput();
                if(errorMessage != null) {
                    dialogBoxLoading.dismiss();
                    dialogBoxAlert = new DialogBoxAlert(activity,
                                    IntegerUtils.CONFIRM_ACTION_CODE_FAILED, errorMessage,"");
                    dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogBoxAlert.show();
                } else {
                    User user = new User();
                    user.setUsername(editUsername.getText().toString());
                    user.setPassword(editPassword.getText().toString());
                    user.setPhoneNumber(editPhoneNumber.getText().toString());
                    user.setFirstName(editFirstName.getText().toString());
                    user.setLastName(editLastName.getText().toString());
                    user.setMail(editMail.getText().toString());
                    APIUserCaller.registerNewUser(user, getApplication(), new APIListener() {
                        @Override
                        public void onCompletingRegistrationRequest() {
                            APIUserCaller.findUserByUsernameAndPassword(user.getUsername(), user.getPassword(),
                                    getApplication(), new APIListener() {
                                @Override
                                public void onUserFound(User user) {
                                    dialogBoxLoading.dismiss();
                                    activity.getIntent().putExtra("USER", user);
                                    try {
                                        dialogBoxAlert = new DialogBoxAlert(activity,
                                                        IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS,
                                                StringUtils.MES_SUCCESSFUL_REGISTRATION,"") {
                                                    @Override
                                                    public void onClickAction() {
                                                        super.onClickAction();
                                                        activity.setResult(Activity.RESULT_OK, activity.getIntent());
                                                        activity.finish();
                                                    }
                                                };
                                        dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        dialogBoxAlert.show();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailedAPICall(int errorCode) {
                                    super.onFailedAPICall(errorCode);
                                    dialogBoxLoading.dismiss();
                                    errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                                    dialogBoxAlert = new DialogBoxAlert(activity,
                                                    IntegerUtils.CONFIRM_ACTION_CODE_FAILED, errorMessage,"");
                                    dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    dialogBoxAlert.show();
                                }
                            });
                        }

                        @Override
                        public void onFailedAPICall(int errorCode) {
                            super.onFailedAPICall(errorCode);
                            dialogBoxLoading.dismiss();
                            errorMessage = StringUtils.MES_ERROR_DUPLICATE_USERNAME;
                            dialogBoxAlert = new DialogBoxAlert(activity,
                                            IntegerUtils.CONFIRM_ACTION_CODE_FAILED, errorMessage,"");
                            dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialogBoxAlert.show();
                        }
                    });
                }
            }
        });
    }

    private void enableButtonConfirm() {
        if(checkEmptyFields()) {
            btnConfirm.setEnabled(false);
            btnConfirm.setBackground(getResources().getDrawable(R.color.gray_light));
        } else {
            btnConfirm.setEnabled(true);
            btnConfirm.setBackground(getResources().getDrawable(R.color.blue_main));
        }
    }

    private boolean checkEmptyFields() {
        if(editUsername.getText().toString().isEmpty()) {
            return true;
        }
        if (editFirstName.getText().toString().isEmpty()) {
            return true;
        }
        if (editLastName.getText().toString().isEmpty()) {
            return true;
        }
        if (editPhoneNumber.getText().toString().isEmpty()) {
            return true;
        }
        if (editPassword.getText().toString().isEmpty()) {
            return true;
        }
        if (editPasswordConfirm.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    private String checkValidInput() {
        if (!editUsername.getText().toString().matches(StringUtils.USERNAME_REGEX)) {
            return StringUtils.MES_ERROR_INVALID_USERNAME;
        }
        if (!MethodUtils.revertPhoneNumber(editPhoneNumber.getText().toString()).matches(StringUtils.PHONE_REGEX)) {
            return StringUtils.MES_ERROR_INVALID_NUMBER;
        }
        if(!editMail.getText().toString().isEmpty()) {
            if (!editMail.getText().toString().matches(StringUtils.MAIL_REGEX)) {
                return StringUtils.MES_ERROR_INVALID_MAIL;
            }
        }
        if (!editPassword.getText().toString().matches(StringUtils.PASSWORD_REGEX)) {
            return StringUtils.MES_ERROR_INVALID_PASSWORD;
        }
        if (!editPassword.getText().toString().equals(editPasswordConfirm.getText().toString())) {
            return StringUtils.MES_ERROR_INCORRECT_CONFIRM_PASSWORD;
        }
        return null;
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}