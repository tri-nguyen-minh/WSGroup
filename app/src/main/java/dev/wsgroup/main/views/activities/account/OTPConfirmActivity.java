package dev.wsgroup.main.views.activities.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.utils.IntegerUtils;

public class OTPConfirmActivity extends AppCompatActivity {

    private EditText editOTP;
    private TextView txtPhoneNumber, txtResendOTP,
            txtOTPHeader, lblResentOTP;
    private Button btnConfirmOTP;

    private String OTP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_confirm);
        this.getSupportActionBar().hide();

        editOTP = findViewById(R.id.editOTP);
        txtPhoneNumber = findViewById(R.id.txtPhoneNumber);
        txtResendOTP = findViewById(R.id.txtResendOTP);
        txtOTPHeader = findViewById(R.id.txtOTPHeader);
        lblResentOTP = findViewById(R.id.lblResentOTP);
        btnConfirmOTP = findViewById(R.id.btnConfirmOTP);
        CardView cardViewBackFromOTP = findViewById(R.id.cardViewBackFromOTP);

        String phoneNumber = getIntent().getStringExtra("PHONE");

        generateOTP();
//        String ID = Settings.Secure.getString(getContentResolver(),
//                Settings.Secure.ANDROID_ID);

        txtPhoneNumber.setText(phoneNumber
                      .replaceFirst("(\\d{3})(\\d{3})(\\d+)", "$1 $2 $3"));
        cardViewBackFromOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED, getIntent());
                finish();
            }
        });

        editOTP.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() == 6) {
                    btnConfirmOTP.setEnabled(true);
                } else {
                    btnConfirmOTP.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        btnConfirmOTP.setEnabled(false);
        btnConfirmOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String OTPInput = editOTP.getText().toString();
                if(OTP.equals(OTPInput)) {
                    int requestCode= getIntent().getIntExtra("REQUEST_CODE",
                                                                IntegerUtils.REQUEST_REGISTER);
                    Intent nextIntent = null;
                    if (requestCode == 1) {
                        nextIntent = new Intent(getApplicationContext(), CompleteAccountActivity.class);
                    } else {
                        nextIntent = new Intent(getApplicationContext(), PasswordChangeActivity.class);
                    }
                    nextIntent.putExtra("PHONE", phoneNumber);
                    startActivityForResult(nextIntent, requestCode);
                } else {
                    generateOTP();
//                    txtOTPError.setVisibility(View.VISIBLE);
                    txtOTPHeader.setText("A new OTP has been sent to");
                }
            }
        });

        txtResendOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                txtOTPError.setVisibility(View.INVISIBLE);
                txtOTPHeader.setText("Sending a new OTP to");

                generateOTP();
//                txtOTPError.setVisibility(View.INVISIBLE);
                txtOTPHeader.setText("A new OTP has been sent to");
            }
        });

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

    private String getOTPString() {
        return editOTP.getText().toString();
    }

    private void generateOTP() {

        OTP = "111111";

//Work with getting OTP

//        Random rnd = new Random();
//        int number = 0;
//        while (number < 100000) {
//            number = rnd.nextInt(999999);
//        }
//        OTP = String.format("%06d", number);
    }

    private void setupResentOTPCountdown() {

    }
}