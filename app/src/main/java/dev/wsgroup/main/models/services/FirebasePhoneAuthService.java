package dev.wsgroup.main.models.services;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import dev.wsgroup.main.models.utils.IntegerUtils;

public class FirebasePhoneAuthService {

    private Activity activity;

    private boolean validOTP;
    private String phone, verificationId;
    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.ForceResendingToken resentToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private PhoneAuthCredential credential;

    public FirebasePhoneAuthService(Activity activity, String phone) {
        this.activity = activity;
        this.phone = phone;
        firebaseAuth = FirebaseAuth.getInstance();
    }
    private void createCallback() {

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                e.printStackTrace();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    onOTPSentFailed(IntegerUtils.ERROR_OTP_REQUEST_INVALID);
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    onOTPSentFailed(IntegerUtils.ERROR_OTP_REQUEST_FULL);
                }
            }

            @Override
            public void onCodeSent(String id, PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(id, token);
                verificationId = id;
                resentToken = token;
                onOTPSent();
            }
        };
    }

    public void sendOTP() {
        if (callbacks == null) {
            createCallback();
        }
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setForceResendingToken(resentToken)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthCredential verifyPhoneNumberWithCode(String code) {
        PhoneAuthCredential credential = null;
        try {
            credential = PhoneAuthProvider.getCredential(verificationId, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return credential;
    }

    public void signInWithPhoneAuthCredential(String code) {
        validOTP = false;
        PhoneAuthCredential credential = verifyPhoneNumberWithCode(code);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            validOTP = true;
                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                task.getException().printStackTrace();
                            }
                        }
                        onVerificationComplete(validOTP);
                    }
                });
    }

    public void onOTPSentFailed(int errorCode) {

    }

    public void onOTPSent() {}

    public void onVerificationComplete(boolean result) {}
}
