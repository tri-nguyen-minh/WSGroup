package dev.wsgroup.main.views.activities.account;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIUserCaller;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;
import dev.wsgroup.main.views.dialogbox.DialogBoxOTP;
import dev.wsgroup.main.views.dialogbox.DialogBoxSelectImage;

public class AccountInformationActivity extends AppCompatActivity {

    private ImageView imgBackFromAccountInformation, imgAccountInfoHome, imgAccountInfoAvatar;
    private CardView cardViewEditProfileAvatar;
    private TextInputEditText editAccountInfoUsername, editAccountInfoFirstName,
            editAccountInfoLastName, editAccountInfoPhone, editAccountInfoMail,
            editAccountInfoWalletCode, editAccountInfoWalletSecret;
    private Button btnSaveEdit;
    private ConstraintLayout layoutParent, layoutAvatar, layoutUsername;
    private LinearLayout layoutFailed, layoutMain;
    private TextView lblRetry, txtLoginMethod;
    private RelativeLayout layoutLoading;
    private ScrollView scrollViewMain;

    private SharedPreferences sharedPreferences;
    private String token, username, phone, avatarLink, googleId;
    private User user;
    private DialogBoxLoading dialogBoxLoading;
    private DialogBoxAlert dialogBoxAlert;
    private DialogBoxConfirm dialogBoxConfirm;
    private Uri uri;
    private String errorMessage;
    private boolean dataLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_information);
        this.getSupportActionBar().hide();

        imgBackFromAccountInformation = findViewById(R.id.imgBackFromAccountInformation);
        imgAccountInfoHome = findViewById(R.id.imgAccountInfoHome);
        imgAccountInfoAvatar = findViewById(R.id.imgAccountInfoAvatar);
        cardViewEditProfileAvatar = findViewById(R.id.cardViewEditProfileAvatar);
        editAccountInfoUsername = findViewById(R.id.editAccountInfoUsername);
        editAccountInfoFirstName = findViewById(R.id.editAccountInfoFirstName);
        editAccountInfoLastName = findViewById(R.id.editAccountInfoLastName);
        editAccountInfoPhone = findViewById(R.id.editAccountInfoPhone);
        editAccountInfoMail = findViewById(R.id.editAccountInfoMail);
        editAccountInfoWalletCode = findViewById(R.id.editAccountInfoWalletCode);
        editAccountInfoWalletSecret = findViewById(R.id.editAccountInfoWalletSecret);
        btnSaveEdit = findViewById(R.id.btnSaveEdit);
        layoutParent = findViewById(R.id.layoutParent);
        layoutAvatar = findViewById(R.id.layoutAvatar);
        layoutUsername = findViewById(R.id.layoutUsername);
        layoutFailed = findViewById(R.id.layoutFailed);
        layoutMain = findViewById(R.id.layoutMain);
        lblRetry = findViewById(R.id.lblRetry);
        txtLoginMethod = findViewById(R.id.txtLoginMethod);
        layoutLoading = findViewById(R.id.layoutLoading);
        scrollViewMain = findViewById(R.id.scrollViewMain);

        dataLoaded = false;

        getUserProfile();

        editAccountInfoUsername.setEnabled(false);
        editAccountInfoUsername.setTextColor(getResources().getColor(R.color.gray));

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
                enablingSaveButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };

        imgBackFromAccountInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });

        imgAccountInfoHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        cardViewEditProfileAvatar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                DialogBoxSelectImage dialogBox
                        = new DialogBoxSelectImage(AccountInformationActivity.this) {
                    @Override
                    public void executeTakePhoto() {
                        if (checkSelfPermission(Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[] {Manifest.permission.CAMERA}, 101);
                        } else {
                            takeImage();
                        }
                    }

                    @Override
                    public void executeSelectPhoto() {
                        selectImage();
                    }
                };
                dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBox.show();
            }
        });

        editAccountInfoFirstName.setOnFocusChangeListener(listener);
        editAccountInfoLastName.setOnFocusChangeListener(listener);
        editAccountInfoMail.setOnFocusChangeListener(listener);
        editAccountInfoPhone.setOnFocusChangeListener(listener);
        editAccountInfoWalletCode.setOnFocusChangeListener(listener);
        editAccountInfoWalletSecret.setOnFocusChangeListener(listener);
        editAccountInfoFirstName.addTextChangedListener(textWatcher);
        editAccountInfoLastName.addTextChangedListener(textWatcher);
        editAccountInfoMail.addTextChangedListener(textWatcher);
        editAccountInfoWalletCode.addTextChangedListener(textWatcher);
        editAccountInfoWalletSecret.addTextChangedListener(textWatcher);
        editAccountInfoPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phone = editAccountInfoPhone.getText().toString();
                if (phone.length() < 4 || !phone.startsWith(StringUtils.VIETNAM_COUNTRY_CODE)) {
                    editAccountInfoPhone.setText(StringUtils.VIETNAM_COUNTRY_CODE);
                    editAccountInfoPhone.setSelection(4);
                }
                enablingSaveButton();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        View.OnClickListener clearFocusListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editAccountInfoFirstName.hasFocus()) {
                    editAccountInfoFirstName.clearFocus();
                }
                if (editAccountInfoLastName.hasFocus()) {
                    editAccountInfoLastName.clearFocus();
                }
                if (editAccountInfoMail.hasFocus()) {
                    editAccountInfoMail.clearFocus();
                }
                if (editAccountInfoPhone.hasFocus()) {
                    editAccountInfoPhone.clearFocus();
                }
                if (editAccountInfoWalletCode.hasFocus()) {
                    editAccountInfoWalletCode.clearFocus();
                }
                if (editAccountInfoWalletSecret.hasFocus()) {
                    editAccountInfoWalletSecret.clearFocus();
                }
            }
        };

        layoutParent.setOnClickListener(clearFocusListener);
        layoutAvatar.setOnClickListener(clearFocusListener);
        layoutMain.setOnClickListener(clearFocusListener);
        txtLoginMethod.setOnClickListener(clearFocusListener);

        lblRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserProfile();
            }
        });

        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBoxLoading = new DialogBoxLoading(AccountInformationActivity.this);
                dialogBoxLoading.getWindow()
                        .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                updateUser();
            }
        });
    }

    private void getUserProfile() {
        setLoadingState();
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
        APIUserCaller.findUserByToken(token, getApplication(), new APIListener() {
            @Override
            public void onUserFound(User foundUser, String message) {
                user = foundUser;
                user.setToken(token);
                username = user.getUsername();
                phone = user.getPhoneNumber();
                googleId = user.getGoogleId();
                if (googleId.isEmpty() || googleId.equals("null") ) {
                    txtLoginMethod.setText("WSGroup's Account");
                    editAccountInfoUsername.setText(username);
                } else {
                    txtLoginMethod.setText("Linked Google's Account");
                    layoutUsername.setVisibility(View.GONE);
                }
                editAccountInfoFirstName.setText(user.getFirstName());
                editAccountInfoLastName.setText(user.getLastName());
                phone = user.getPhoneNumber();
                editAccountInfoPhone.setText(MethodUtils.formatPhoneNumberWithCountryCode(phone));
                editAccountInfoMail.setText(user.getMail());
                editAccountInfoWalletCode.setText(user.getWalletCode().equals("null")  ?
                        "" : user.getWalletCode());
                editAccountInfoWalletSecret.setText(user.getWalletSecret().equals("null") ?
                        "" : user.getWalletSecret());
                avatarLink = user.getAvatarLink();
                if (!avatarLink.equals("null")) {
                    Glide.with(getApplicationContext())
                            .load(avatarLink)
                            .into(imgAccountInfoAvatar);
                }
                dataLoaded = true;
                setReadyState();
                enablingSaveButton();
            }

            @Override
            public void onFailedAPICall(int code) {
                if (code == IntegerUtils.ERROR_NO_USER) {
                    MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                            AccountInformationActivity.this);
                } else {
                    setFailedState();
                }
            }
        });
    }

    private void updateUser() {
        phone = MethodUtils.revertPhoneNumber(editAccountInfoPhone.getText().toString());
        if (!phone.equals(user.getPhoneNumber())) {
            APIUserCaller.findUserByPhoneNumber(phone, getApplication(), new APIListener() {
                @Override
                public void onUserFound(User user, String message) {
                    errorMessage = StringUtils.MES_ERROR_DUPLICATE_NUMBER;
                    displayError();
                }
                @Override
                public void onFailedAPICall(int errorCode) {
                    super.onFailedAPICall(errorCode);
                    switch (errorCode) {
                        case IntegerUtils.ERROR_API:
                        case IntegerUtils.ERROR_PARSING_JSON: {
                            errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                            displayError();
                            break;
                        }
                        case IntegerUtils.ERROR_NO_USER: {
                            sendOTP();
                            break;
                        }
                    }
                }
            });
        } else {
            if (!checkAvatarChanged()) {
                checkEmptyWallet();
            } else {
                uploadImageToFirebase();
            }
        }
    }

    private void enablingSaveButton() {
        if (dataLoaded) {
            if (checkEmptyFields() || !checkInfoChanged() || checkInvalidInput()) {
                System.out.println("in false");
                btnSaveEdit.setEnabled(false);
                btnSaveEdit.getBackground()
                           .setTint(getApplicationContext().getResources()
                                    .getColor(R.color.gray_light));
            } else {
                System.out.println("in true");
                btnSaveEdit.setEnabled(true);
                btnSaveEdit.getBackground()
                           .setTint(getApplicationContext().getResources()
                                    .getColor(R.color.blue_main));
            }
        }
    }

    private boolean checkEmptyFields() {
        if(editAccountInfoFirstName.getText().toString().isEmpty()) {
            return true;
        }
        if (editAccountInfoLastName.getText().toString().isEmpty()) {
            return true;
        }
        if (editAccountInfoPhone.getText().toString().equals(StringUtils.VIETNAM_COUNTRY_CODE)) {
            return true;
        }
        return false;
    }

    private boolean checkInvalidInput() {
        if(!editAccountInfoMail.getText().toString().isEmpty()) {
            if (!Patterns.EMAIL_ADDRESS.matcher(editAccountInfoMail.getText().toString()).matches()) {
                return true;
            }
        }
        phone = MethodUtils.revertPhoneNumber(editAccountInfoPhone.getText().toString());
        System.out.println(phone);
        if (phone.isEmpty() || !phone.matches(StringUtils.PHONE_REGEX)) {
            return true;
        }
        return false;
    }

    private boolean checkInfoChanged() {
        String firstName = editAccountInfoFirstName.getText().toString();
        String lastname = editAccountInfoLastName.getText().toString();
        String mail = editAccountInfoMail.getText().toString();
        String phone = MethodUtils.revertPhoneNumber(editAccountInfoPhone.getText().toString());
        if (!firstName.equals(user.getFirstName())) {
            return true;
        }
        if (!lastname.equals(user.getLastName())) {
            return true;
        }
        if (!mail.equals(user.getMail())) {
            return true;
        }
        if (!phone.equals(user.getPhoneNumber())) {
            return true;
        }
        return checkAvatarChanged();
    }

    private boolean checkAvatarChanged() {
        return !avatarLink.equals(user.getAvatarLink());
    }

    public void takeImage() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, IntegerUtils.REQUEST_TAKE_IMAGE);
    }

    public void selectImage() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IntegerUtils.REQUEST_SELECT_IMAGE);
    }

    private void uploadImageToFirebase() {
        avatarLink = "images/" + username + "_avatar";
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference();
                StorageReference ref = storageReference.child(avatarLink);
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("image/jpeg")
                        .build();
                UploadTask uploadTask = ref.putFile(uri, metadata);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,
                                Task<Uri>>() {
                            @Override
                            public Task<Uri> then(Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                                    displayError();
                                    throw task.getException();
                                }
                                return ref.getDownloadUrl();
                            }
                        });
                        urlTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    avatarLink = task.getResult().toString();
                                    checkEmptyWallet();
                                } else {
                                    errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                                    displayError();
                                }
                            }
                        });
                    }
                });
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                        errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                        displayError();
                    }
                });

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                displayError();
            }
        });
    }

    private void checkEmptyWallet() {
        String walletCode = editAccountInfoWalletCode.getText().toString();
        String walletSecret = editAccountInfoWalletSecret.getText().toString();
        if (walletCode.isEmpty() || walletSecret.isEmpty()) {
            dialogBoxConfirm = new DialogBoxConfirm(AccountInformationActivity.this,
                    StringUtils.MES_CONFIRM_NO_WALLET) {
                @Override
                public void onYesClicked() {
                    performUpdate();
                }

                @Override
                public void onNoClicked() {
                    if (dialogBoxLoading.isShowing()) {
                        dialogBoxLoading.dismiss();
                    }
                }
            };
            dialogBoxConfirm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogBoxConfirm.setDescription(StringUtils.MES_ALERT_INCOMPLETE_WALLET);
            dialogBoxConfirm.show();
        } else {
            performUpdate();
        }
    }

    private void performUpdate() {
        User updatedUser = new User();
        updatedUser.setToken(token);
        updatedUser.setFirstName(editAccountInfoFirstName.getText().toString());
        updatedUser.setLastName(editAccountInfoLastName.getText().toString());
        updatedUser.setMail(editAccountInfoMail.getText().toString());
        updatedUser.setWalletCode(editAccountInfoWalletCode.getText().toString());
        updatedUser.setWalletSecret(editAccountInfoWalletSecret.getText().toString());
        updatedUser.setPhoneNumber(phone);
        updatedUser.setAvatarLink(avatarLink);
        APIUserCaller.updateUserProfile(updatedUser, getApplication(), new APIListener() {
            @Override
            public void onUpdateSuccessful() {
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                user.setFirstName(updatedUser.getFirstName());
                user.setLastName(updatedUser.getLastName());
                user.setMail(updatedUser.getMail());
                user.setAvatarLink(avatarLink);
                if (!phone.equals(user.getPhoneNumber())) {
                    sharedPreferences.edit().putString("PHONE", phone);
                    user.setPhoneNumber(phone);
                }
                DialogBoxAlert dialogBox = new DialogBoxAlert(AccountInformationActivity.this,
                        IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS,
                        StringUtils.MES_SUCCESSFUL_UPDATE_PROFILE,"") {
                    @Override
                    public void onClickAction() {
                        getUserProfile();
                    }
                };
                dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBox.show();
            }

            @Override
            public void onFailedAPICall(int code) {
                errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                displayError();
            }
        });
    }

    private void sendOTP() {
        DialogBoxOTP dialogBoxOTP = new DialogBoxOTP(AccountInformationActivity.this,
                getApplicationContext(), phone) {
            @Override
            public void onVerificationSuccessful() {
                DialogBoxAlert dialogBox = new DialogBoxAlert(AccountInformationActivity.this,
                        IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS,
                        StringUtils.MES_SUCCESSFUL_OTP,"") {
                    @Override
                    public void onClickAction() {
                        if (!checkAvatarChanged()) {
                            checkEmptyWallet();
                        } else {
                            uploadImageToFirebase();
                        }
                    }
                };
                dialogBox.show();
                dismiss();
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
        dialogBoxAlert = new DialogBoxAlert(AccountInformationActivity.this,
                        IntegerUtils.CONFIRM_ACTION_CODE_FAILED, errorMessage,"");
        dialogBoxAlert.show();
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutFailed.setVisibility(View.GONE);
        scrollViewMain.setVisibility(View.GONE);
    }

    private void setFailedState() {
        layoutLoading.setVisibility(View.GONE);
        layoutFailed.setVisibility(View.VISIBLE);
        scrollViewMain.setVisibility(View.GONE);
    }

    private void setReadyState() {
        layoutLoading.setVisibility(View.GONE);
        layoutFailed.setVisibility(View.GONE);
        scrollViewMain.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101
                && grantResults.length != 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            takeImage();
        }
    }

    @Override
    public void onBackPressed() {
        imgBackFromAccountInformation.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uri = null;
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == IntegerUtils.REQUEST_TAKE_IMAGE) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                uri = MethodUtils.getImageUri(getApplicationContext(), image);
            } else if (requestCode == IntegerUtils.REQUEST_SELECT_IMAGE) {
                uri = data.getData();
            }
            avatarLink = "temp";
            imgAccountInfoAvatar.setImageURI(uri);
            enablingSaveButton();
        }
    }
}