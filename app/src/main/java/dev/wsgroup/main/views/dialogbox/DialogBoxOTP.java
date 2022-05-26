package dev.wsgroup.main.views.dialogbox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.services.FirebasePhoneAuthService;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;

public class DialogBoxOTP extends Dialog {

    private TextView txtOTPHeader, txtPhoneNumber,
            txtResendOTP, txtCountdownClock;
    private EditText editOTP;
    private Button btnConfirmOTP;
    private ProgressBar progressBarLoadOTP;
    private CardView cardViewParent;
    private LinearLayout layoutOTPCountdown;
    private ImageView imgCloseDialogBox;

    private Context context;
    private Activity activity;
    private String phoneNumber;
    private FirebasePhoneAuthService service;

    public DialogBoxOTP(Activity activity, Context context, String phoneNumber) {
        super(activity);
        this.context = context;
        this.activity = activity;
        this.phoneNumber = phoneNumber;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_otp);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        setCancelable(false);

        txtOTPHeader = findViewById(R.id.txtOTPHeader);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtResendOTP = findViewById(R.id.txtResendOTP);
        txtCountdownClock = findViewById(R.id.txtCountdownClock);
        editOTP = findViewById(R.id.editOTP);
        btnConfirmOTP = findViewById(R.id.btnConfirmOTP);
        progressBarLoadOTP = findViewById(R.id.progressBarLoadOTP);
        cardViewParent = findViewById(R.id.cardViewParent);
        layoutOTPCountdown = findViewById(R.id.layoutOTPCountdown);
        imgCloseDialogBox = findViewById(R.id.imgCloseDialogBox);

        txtPhoneNumber.setText(MethodUtils.formatPhoneNumber(phoneNumber));
        btnConfirmOTP.setEnabled(false);
        btnConfirmOTP.getBackground().setTint(context.getResources().getColor(R.color.gray_light));

        service = new FirebasePhoneAuthService(activity, phoneNumber) {
            @Override
            public void onOTPSent() {
                super.onOTPSent();
                setLabel(true);
                resetCountDown();
            }

            @Override
            public void onOTPSentFailed(int errorCode) {
                dismiss();
                onSendOTPFailed();
            }

            @Override
            public void onVerificationComplete(boolean result) {
                super.onVerificationComplete(result);
                if(result) {
                    onVerificationSuccessful();
                } else {
                    onVerificationFailed();
                }
            }
        };

        txtResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateOTP();
            }
        });

        cardViewParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editOTP.hasFocus()) {
                    editOTP.clearFocus();
                }
            }
        });

        imgCloseDialogBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                onClosingDialogBox();
            }
        });

        editOTP.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    MethodUtils.hideKeyboard(view, context);
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
                    btnConfirmOTP.getBackground()
                                 .setTint(context.getResources().getColor(R.color.blue_main));
                } else {
                    btnConfirmOTP.setEnabled(false);
                    btnConfirmOTP.getBackground()
                                 .setTint(context.getResources().getColor(R.color.gray_light));
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
                String OTPInput = getOTPString();
                service.signInWithPhoneAuthCredential(OTPInput);
            }
        });
        generateOTP();
    }

    public void onVerificationFailed() {
        DialogBoxAlert dialogBoxAlert = new DialogBoxAlert(activity,
                IntegerUtils.CONFIRM_ACTION_CODE_FAILED, StringUtils.MES_ERROR_INVALID_OTP);
        dialogBoxAlert.show();
    }

    public void onVerificationSuccessful() {
    }
    public void onClosingDialogBox() {}

    public void onSendOTPFailed() {
    }

    private String getOTPString() {
        return editOTP.getText().toString();
    }

    public void generateOTP() {
        setLabel(false);
        txtResendOTP.setVisibility(View.INVISIBLE);
        layoutOTPCountdown.setVisibility(View.INVISIBLE);
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
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                int timer = (int) (l / 1000);
                String timerString = (timer > 9) ? "" + timer : "0" + timer;
                txtCountdownClock.setText(timerString);
            }

            @Override
            public void onFinish() {
                layoutOTPCountdown.setVisibility(View.INVISIBLE);
                txtResendOTP.setVisibility(View.VISIBLE);
            }
        }.start();
        layoutOTPCountdown.setVisibility(View.VISIBLE);
        txtResendOTP.setVisibility(View.INVISIBLE);
    }
}
