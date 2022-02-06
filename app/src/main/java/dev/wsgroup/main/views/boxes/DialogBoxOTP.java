package dev.wsgroup.main.views.boxes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.views.activities.account.AccountDetailActivity;
import dev.wsgroup.main.views.activities.account.PasswordChangeActivity;
import dev.wsgroup.main.views.activities.account.PhoneInputActivity;

public class DialogBoxOTP extends Dialog {

    private TextView txtOTPHeader, txtPhoneNumber, txtResendOTP;
    private EditText editOTP;
    private Button btnConfirmOTP;
    private ProgressBar progressBarLoadOTP;
    private CardView cardViewParent;

    private Context context;
    private Activity activity;
    private String phoneNumber, otp;
    private int requestCode;

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
        editOTP = findViewById(R.id.editOTP);
        btnConfirmOTP = findViewById(R.id.btnConfirmOTP);
        progressBarLoadOTP = findViewById(R.id.progressBarLoadOTP);
        cardViewParent = findViewById(R.id.cardViewParent);

        txtPhoneNumber.setText(MethodUtils.formatPhoneNumber(phoneNumber));
        btnConfirmOTP.setEnabled(false);
        btnConfirmOTP.setBackground(context.getResources().getDrawable(R.color.gray_light));

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
                txtOTPHeader.setVisibility(View.INVISIBLE);
                progressBarLoadOTP.setVisibility(View.VISIBLE);
                String OTPInput = getOTPString();
                if(otp.equals(OTPInput)) {
                    Intent nextIntent;
                    if (requestCode == IntegerUtils.REQUEST_REGISTER) {
                        nextIntent = new Intent(context, AccountDetailActivity.class);
                    } else {
                        nextIntent = new Intent(context, PasswordChangeActivity.class);
                    }
                    nextIntent.putExtra("PHONE", phoneNumber);
                    activity.startActivityForResult(nextIntent, requestCode);
                } else {
                    generateOTP();
                    editOTP.setText("");
                }
            }
        });
        generateOTP();
    }

    private String getOTPString() {
        return editOTP.getText().toString();
    }

    private void generateOTP() {
        txtOTPHeader.setVisibility(View.INVISIBLE);
        progressBarLoadOTP.setVisibility(View.VISIBLE);
        otp = "111111";
//Work with getting OTP

//        Random rnd = new Random();
//        int number = 0;
//        while (number < 100000) {
//            number = rnd.nextInt(999999);
//        }
//        OTP = String.format("%06d", number);

        txtOTPHeader.setVisibility(View.VISIBLE);
        progressBarLoadOTP.setVisibility(View.INVISIBLE);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
