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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;
import dev.wsgroup.main.views.dialogbox.DialogBoxOTP;
import dev.wsgroup.main.views.dialogbox.DialogBoxSelectImage;

public class AccountInformationActivity extends AppCompatActivity {

    private ImageView imgBackFromAccountInformation, imgAccountInfoHome, imgAccountInfoAvatar;
    private CardView cardViewEditProfileAvatar;
    private EditText editAccountInfoUsername, editAccountInfoFirstName, editAccountInfoLastName,
                     editAccountInfoPhone, editAccountInfoMail;
    private Button btnSaveEdit;
    private ConstraintLayout layoutParent, layoutProfileAvatar;
    private ProgressBar progressBarLoading;

    private SharedPreferences sharedPreferences;
    private String token, username, phone, avatarLink;
    private User user;
    private DialogBoxLoading dialogBoxLoading;
    private DialogBoxAlert dialogBoxAlert;
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
        btnSaveEdit = findViewById(R.id.btnSaveEdit);
        layoutParent = findViewById(R.id.layoutParent);
        layoutProfileAvatar = findViewById(R.id.layoutProfileAvatar);
        progressBarLoading = findViewById(R.id.progressBarLoading);

        dataLoaded = false;
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
        username = sharedPreferences.getString("USERNAME", "");
        phone = sharedPreferences.getString("PHONE", "");

        layoutProfileAvatar.setVisibility(View.INVISIBLE);
        progressBarLoading.setVisibility(View.VISIBLE);

        APIUserCaller.findUserByToken(token, getApplication(), new APIListener() {
            @Override
            public void onUserFound(User foundUser, String message) {
                user = foundUser;
                user.setToken(token);
                user.setUsername(username);
                user.setPhoneNumber(phone);
                editAccountInfoUsername.setText(username);
                editAccountInfoFirstName.setText(user.getFirstName());
                editAccountInfoLastName.setText(user.getLastName());
                editAccountInfoPhone.setText(user.getPhoneNumber());
                editAccountInfoMail.setText(user.getMail());
                avatarLink = user.getAvatarLink();
                Glide.with(getApplicationContext()).load(avatarLink).into(imgAccountInfoAvatar);
                layoutProfileAvatar.setVisibility(View.VISIBLE);
                progressBarLoading.setVisibility(View.INVISIBLE);
                dataLoaded = true;
                enablingSaveButton();
            }
        });

        editAccountInfoUsername.setEnabled(false);
        editAccountInfoUsername.setTextColor(getResources().getColor(R.color.gray));

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
                            requestPermissions(new String[] {Manifest.permission.CAMERA}, 100);
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
        editAccountInfoFirstName.addTextChangedListener(textWatcher);
        editAccountInfoLastName.addTextChangedListener(textWatcher);
        editAccountInfoMail.addTextChangedListener(textWatcher);
        editAccountInfoPhone.addTextChangedListener(textWatcher);

        layoutParent.setOnClickListener(new View.OnClickListener() {
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
            }
        });

        btnSaveEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBoxLoading = new DialogBoxLoading(AccountInformationActivity.this);
                dialogBoxLoading.getWindow()
                        .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                if (!editAccountInfoPhone.getText().toString().equals(user.getPhoneNumber())) {
                    phone = editAccountInfoPhone.getText().toString();
                    APIUserCaller.findUserByPhoneNumber(phone,
                            getApplication(), new APIListener() {
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
                                            errorMessage = StringUtils.MES_ALERT_OTP_REQUIRE;
                                            dialogBoxAlert = new DialogBoxAlert(AccountInformationActivity.this,
                                                    IntegerUtils.CONFIRM_ACTION_CODE_ALERT, errorMessage,"") {
                                                @Override
                                                public void onClickAction() {
                                                    sendOTP();
                                                }
                                            };
                                            dialogBoxAlert.show();
                                            break;
                                        }
                                    }
                                }
                            });
                } else {
                    if (!checkAvatarChanged()) {
                        updateUser();
                    } else {
                        uploadImageToFirebase();
                    }
                }
            }
        });
    }

    private void enablingSaveButton() {
        if (dataLoaded) {
            if (checkEmptyFields() || !checkInfoChanged() || checkInvalidInput()) {
                btnSaveEdit.setEnabled(false);
                btnSaveEdit.getBackground()
                           .setTint(getApplicationContext().getResources()
                                    .getColor(R.color.gray_light));
            } else {
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
        if (editAccountInfoPhone.getText().toString().isEmpty()) {
            return true;
        }
        return false;
    }

    private boolean checkInvalidInput() {
        if(!editAccountInfoMail.getText().toString().isEmpty()) {
            if (!editAccountInfoMail.getText().toString().matches(StringUtils.MAIL_REGEX)) {
                return true;
            }
        }
        if (!editAccountInfoPhone.getText().toString().isEmpty()) {
            if (!editAccountInfoPhone.getText().toString().matches(StringUtils.PHONE_REGEX)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkInfoChanged() {
        String firstName = editAccountInfoFirstName.getText().toString();
        String lastname = editAccountInfoLastName.getText().toString();
        String mail = editAccountInfoMail.getText().toString();
        String phone = editAccountInfoPhone.getText().toString();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takeImage();
            }
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager
                = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
                        Task<Uri> urlTask
                                = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
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
                                    updateUser();
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

    private void updateUser() {
        User updatedUser = new User();
        updatedUser.setToken(token);
        updatedUser.setFirstName(editAccountInfoFirstName.getText().toString());
        updatedUser.setLastName(editAccountInfoLastName.getText().toString());
        updatedUser.setMail(editAccountInfoMail.getText().toString());
        updatedUser.setPhoneNumber(phone);
        updatedUser.setAvatarLink(avatarLink);
        APIUserCaller.updateUserProfile(updatedUser, getApplication(), new APIListener() {
            @Override
            public void onUpdateProfileSuccessful() {
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
                        StringUtils.MES_SUCCESSFUL_UPDATE_PROFILE,"");
                dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBox.show();
            }

            @Override
            public void onFailedAPICall(int code) {
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                DialogBoxAlert dialogBox = new DialogBoxAlert(AccountInformationActivity.this,
                        IntegerUtils.CONFIRM_ACTION_CODE_FAILED,
                        StringUtils.MES_ERROR_FAILED_API_CALL,"") {};
                dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBox.show();
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
                        updateUser();
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