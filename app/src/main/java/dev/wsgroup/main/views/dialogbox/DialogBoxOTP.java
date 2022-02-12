package dev.wsgroup.main.views.dialogbox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.services.FirebasePhoneAuthService;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.account.AccountDetailActivity;
import dev.wsgroup.main.views.activities.account.PasswordChangeActivity;

public class DialogBoxOTP extends Dialog {

    private TextView txtOTPHeader, txtPhoneNumber, txtResendOTP, txtCountdownClock;
    private EditText editOTP;
    private Button btnConfirmOTP;
    private ProgressBar progressBarLoadOTP;
    private CardView cardViewParent;
    private LinearLayout layoutResendOTP, layoutOTPCountdown;

    private Context context;
    private Activity activity;
    private String phoneNumber, otp;
    private int requestCode;
    private FirebasePhoneAuthService service;

    public DialogBoxOTP(Activity activity, Context context, String phoneNumber, int requestCode) {
        super(activity);
        this.context = context;
        this.activity = activity;
        this.phoneNumber = phoneNumber;
        this.requestCode = requestCode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_otp);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        txtOTPHeader = findViewById(R.id.txtOTPHeader);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtResendOTP = findViewById(R.id.txtResendOTP);
        txtCountdownClock = findViewById(R.id.txtCountdownClock);
        editOTP = findViewById(R.id.editOTP);
        btnConfirmOTP = findViewById(R.id.btnConfirmOTP);
        progressBarLoadOTP = findViewById(R.id.progressBarLoadOTP);
        cardViewParent = findViewById(R.id.cardViewParent);
        layoutResendOTP = findViewById(R.id.layoutResendOTP);
        layoutOTPCountdown = findViewById(R.id.layoutOTPCountdown);

        txtPhoneNumber.setText(MethodUtils.formatPhoneNumberWithCountryCode(MethodUtils.formatPhoneNumber(phoneNumber)));
        btnConfirmOTP.setEnabled(false);
        btnConfirmOTP.setBackground(context.getResources().getDrawable(R.color.gray_light));

        setLabel(false);
        layoutOTPCountdown.setVisibility(View.INVISIBLE);

        service = new FirebasePhoneAuthService(activity, MethodUtils.formatPhoneNumberWithCountryCode(phoneNumber)) {
            @Override
            public void onOTPSent() {
                super.onOTPSent();
                setLabel(true);
                layoutOTPCountdown.setVisibility(View.VISIBLE);
                resetCountDown();
            }

            @Override
            public void onVerificationComplete(boolean result) {
                super.onVerificationComplete(result);
                if(result) {
                    DialogBoxAlert dialogBox =
                            new DialogBoxAlert(activity,
                                    IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS, StringUtils.MES_SUCCESSFUL_OTP,"") {
                                @Override
                                public void onClickAction() {
                                    super.onClickAction();
                                    Intent nextIntent;
                                    if (requestCode == IntegerUtils.REQUEST_REGISTER) {
                                        nextIntent = new Intent(context, AccountDetailActivity.class);
                                    } else {
                                        nextIntent = new Intent(context, PasswordChangeActivity.class);
                                    }
                                    nextIntent.putExtra("PHONE", phoneNumber);
                                    activity.startActivityForResult(nextIntent, requestCode);
                                }
                            };
                    dialogBox.show();
                    dismiss();
                } else {
                    displayError(StringUtils.MES_ERROR_INVALID_OTP);
                }
            }
        };

//        dialogBoxLoading = new DialogBoxLoading(activity);
//        dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialogBoxLoading.show();
//        APIUserCaller.findUserByPhoneNumber(phoneNumber, activity.getApplication(), new APIListener() {
//            @Override
//            public void onUserFound(User user) {
//                super.onUserFound(user);
//                dialogBoxLoading.dismiss();
//                if(requestCode != IntegerUtils.REQUEST_REGISTER) {
//                    cardViewParent.setVisibility(View.VISIBLE);
//                } else {
//                    displayError(StringUtils.MES_ERROR_DUPLICATE_NUMBER);
//                    dismiss();
//                }
//            }
//            @Override
//            public void onFailedAPICall(int errorCode) {
//                super.onFailedAPICall(errorCode);
//                dialogBoxLoading.dismiss();
//                switch (errorCode) {
//                    case IntegerUtils.ERROR_API:
//                    case IntegerUtils.ERROR_PARSING_JSON: {
//                        displayError(StringUtils.MES_ERROR_FAILED_API_CALL);
//                        dismiss();
//                        break;
//                    }
//                    case IntegerUtils.ERROR_NO_USER: {
//                        if(requestCode != IntegerUtils.REQUEST_REGISTER) {
//                            displayError(StringUtils.MES_ERROR_NO_NUMBER_FOUND);
//                            dismiss();
//                        } else {
//                            cardViewParent.setVisibility(View.VISIBLE);
//                        }
//                        break;
//                    }
//                }
//            }
//        });

        txtResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateOTP();
            }
        });

        cardViewParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editOTP.clearFocus();
            }
        });

        editOTP.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        editOTP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (getOTPString().length() == 6) {
                    btnConfirmOTP.setEnabled(true);
                    btnConfirmOTP.setBackground(context.getResources().getDrawable(R.color.blue_main));
                } else {
                    btnConfirmOTP.setEnabled(false);
                    btnConfirmOTP.setBackground(context.getResources().getDrawable(R.color.gray_light));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnConfirmOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLabel(true);
                layoutOTPCountdown.setVisibility(View.VISIBLE);
                String OTPInput = getOTPString();
                service.signInWithPhoneAuthCredential(OTPInput);
            }
        });
        generateOTP();
    }

    private void displayError(String errorMessage) {
        DialogBoxAlert dialogBox =
                new DialogBoxAlert(activity,
                        IntegerUtils.CONFIRM_ACTION_CODE_FAILED, errorMessage,"");
        dialogBox.show();
    }

    private String getOTPString() {
        return editOTP.getText().toString();
    }

    public void generateOTP() {
        setLabel(false);
        layoutOTPCountdown.setVisibility(View.GONE);
        service.sendOTP();
    }

    public void setLabel(boolean OTPSent) {
        if (OTPSent) {
            txtOTPHeader.setVisibility(View.VISIBLE);
            progressBarLoadOTP.setVisibility(View.INVISIBLE);
        } else {
            txtOTPHeader.setVisibility(View.INVISIBLE);
            progressBarLoadOTP.setVisibility(View.VISIBLE);
        }
    }


    public void resetCountDown() {
        layoutOTPCountdown.setVisibility(View.VISIBLE);
        layoutResendOTP.setVisibility(View.GONE);
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                int timer = (int) (l / 1000);
                String timerString = (timer > 9) ? "" + timer : "0" + timer;
                txtCountdownClock.setText(timerString);
            }

            @Override
            public void onFinish() {
                layoutOTPCountdown.setVisibility(View.GONE);
                layoutResendOTP.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
