package dev.wsgroup.main.views.activities.account;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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

import java.util.UUID;

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

public class AccountDetailActivity extends AppCompatActivity {

    private EditText editUsername, editFirstName, editLastName,
            editPassword, editPasswordConfirm, editPhoneNumber, editMail;
    private CardView cardViewEditProfileAvatar;
    private TextView btnConfirm;
    private ImageView imgDisplayPassword, imgDisplayPasswordConfirm,
            imgBackFromAccountDetailRegister, imgAccountInfoHome, imgAccountInfoAvatar;
    private ConstraintLayout layoutParent, layoutUsername;
    private LinearLayout layoutPassword;

    private boolean passwordDisplayed, passwordConfirmedDisplayed;
    private User user;
    private String googleId, errorMessage, avatarLink;
    private Activity activity;
    private Uri uri;
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
        cardViewEditProfileAvatar = findViewById(R.id.cardViewEditProfileAvatar);
        imgDisplayPassword = findViewById(R.id.imgDisplayPassword);
        imgDisplayPasswordConfirm = findViewById(R.id.imgDisplayPasswordConfirm);
        imgBackFromAccountDetailRegister = findViewById(R.id.imgBackFromAccountDetailRegister);
        imgAccountInfoHome = findViewById(R.id.imgAccountInfoHome);
        imgAccountInfoAvatar = findViewById(R.id.imgAccountInfoAvatar);
        layoutParent = findViewById(R.id.layoutParent);
        layoutUsername = findViewById(R.id.layoutUsername);
        layoutPassword = findViewById(R.id.layoutPassword);

        activity = this;
        passwordDisplayed = false;
        passwordConfirmedDisplayed = false;
        user = (User)getIntent().getSerializableExtra("USER");
        if(user.getGoogleId() == null || user.getGoogleId().equals("null")) {
            editPhoneNumber.setText(user.getPhoneNumber());
            editPhoneNumber.setEnabled(false);
            layoutUsername.setVisibility(View.VISIBLE);
            layoutPassword.setVisibility(View.VISIBLE);
            imgAccountInfoAvatar.setImageResource(R.drawable.ic_profile_circle);
            avatarLink = "";
        } else {
            googleId = user.getGoogleId();
            layoutUsername.setVisibility(View.GONE);
            layoutPassword.setVisibility(View.GONE);
            editFirstName.setText(user.getFirstName());
            editLastName.setText(user.getLastName());
            editMail.setText(user.getMail());
            editMail.setEnabled(false);
            avatarLink = user.getAvatarLink();
            Glide.with(getApplicationContext()).load(avatarLink).into(imgAccountInfoAvatar);
        }
        enableButtonConfirm();

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
        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    MethodUtils.hideKeyboard(view, getApplicationContext());
                }
            }
        };
        editUsername.setOnFocusChangeListener(focusChangeListener);
        editPassword.setOnFocusChangeListener(focusChangeListener);
        editPasswordConfirm.setOnFocusChangeListener(focusChangeListener);
        editPhoneNumber.setOnFocusChangeListener(focusChangeListener);
        editFirstName.setOnFocusChangeListener(focusChangeListener);
        editLastName.setOnFocusChangeListener(focusChangeListener);
        editMail.setOnFocusChangeListener(focusChangeListener);

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
                        editPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                                | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        imgDisplayPassword.setImageResource(R.drawable.ic_visibility_off);
                        passwordDisplayed = false;
                    } else {
                        editPassword.setInputType(InputType.TYPE_CLASS_TEXT
                                                | InputType.TYPE_TEXT_VARIATION_NORMAL);
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
                        editPasswordConfirm.setInputType(InputType.TYPE_CLASS_TEXT
                                                        | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        imgDisplayPasswordConfirm.setImageResource(R.drawable.ic_visibility_off);
                        passwordConfirmedDisplayed = false;
                    } else {
                        editPasswordConfirm.setInputType(InputType.TYPE_CLASS_TEXT
                                                        | InputType.TYPE_TEXT_VARIATION_NORMAL);
                        imgDisplayPasswordConfirm.setImageResource(R.drawable.ic_visibility_on);
                        passwordConfirmedDisplayed = true;
                    }
                }
            }
        });

        cardViewEditProfileAvatar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                DialogBoxSelectImage dialogBox
                        = new DialogBoxSelectImage(activity) {
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

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                enableButtonConfirm();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };

        editUsername.addTextChangedListener(textWatcher);
        editFirstName.addTextChangedListener(textWatcher);
        editLastName.addTextChangedListener(textWatcher);
        editPhoneNumber.addTextChangedListener(textWatcher);
        editPassword.addTextChangedListener(textWatcher);
        editPasswordConfirm.addTextChangedListener(textWatcher);

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBoxLoading = new DialogBoxLoading(activity);
                dialogBoxLoading.getWindow()
                                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                errorMessage = checkValidInput();
                if(errorMessage != null) {
                    if (dialogBoxLoading.isShowing()) {
                        dialogBoxLoading.dismiss();
                    }
                    displayErrorMessage();
                } else if (googleId == null) {
                    User newUser = new User();
                    newUser.setUsername(editUsername.getText().toString());
                    newUser.setPassword(editPassword.getText().toString());
                    String dataString = editPhoneNumber.getText().toString();
                    newUser.setPhoneNumber(dataString);
                    newUser.setFirstName(editFirstName.getText().toString());
                    newUser.setLastName(editLastName.getText().toString());
                    newUser.setMail(editMail.getText().toString());
                    newUser.setAvatarLink(user.getAvatarLink());
                    if (!avatarLink.equals("")) {
                        uploadImageToFirebase(newUser);
                    } else {
                        registerUser(newUser);
                    }
                } else {
                    checkDuplicatePhone();
                }
            }
        });
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

    private void uploadImageToFirebase(User newUser) {
        avatarLink = "images/" + UUID.randomUUID();
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
                                    if (dialogBoxLoading.isShowing()) {
                                        dialogBoxLoading.dismiss();
                                    }
                                    MethodUtils.displayErrorAPIMessage(activity);
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
                                    newUser.setAvatarLink(avatarLink);
                                    if (googleId != null) {
                                        updateUser();
                                    } else {
                                        registerUser(newUser);
                                    }
                                } else {
                                    if (dialogBoxLoading.isShowing()) {
                                        dialogBoxLoading.dismiss();
                                    }
                                    MethodUtils.displayErrorAPIMessage(activity);
                                }
                            }
                        });
                    }
                });
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                        if (dialogBoxLoading.isShowing()) {
                            dialogBoxLoading.dismiss();
                        }
                        MethodUtils.displayErrorAPIMessage(activity);
                    }
                });

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                MethodUtils.displayErrorAPIMessage(activity);
            }
        });
    }

    private void checkDuplicatePhone() {
        APIUserCaller.findUserByPhoneNumber(editPhoneNumber.getText().toString(),
                getApplication(), new APIListener() {
            @Override
            public void onUserFound(User user, String message) {
                errorMessage = StringUtils.MES_ERROR_DUPLICATE_NUMBER;
                displayErrorMessage();
            }
            @Override
            public void onFailedAPICall(int errorCode) {
                switch (errorCode) {
                    case IntegerUtils.ERROR_API:
                    case IntegerUtils.ERROR_PARSING_JSON: {
                        errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                        displayErrorMessage();
                        break;
                    }
                    case IntegerUtils.ERROR_NO_USER: {
                        errorMessage = StringUtils.MES_ALERT_OTP_REQUIRE;
                        dialogBoxAlert = new DialogBoxAlert(activity,
                                IntegerUtils.CONFIRM_ACTION_CODE_ALERT,
                                errorMessage,"") {
                            @Override
                            public void onClickAction() {
                                user.setPhoneNumber(editPhoneNumber.getText()
                                        .toString());
                                sendOTP();
                            }
                        };
                        dialogBoxAlert.show();
                        break;
                    }
                }
            }
        });
    }

    private void sendOTP() {
        DialogBoxOTP dialogBoxOTP = new DialogBoxOTP(activity,
                getApplicationContext(), user.getPhoneNumber()) {
            @Override
            public void onVerificationSuccessful() {
                DialogBoxAlert dialogBox = new DialogBoxAlert(activity,
                        IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS,
                        StringUtils.MES_SUCCESSFUL_OTP,"") {
                    @Override
                    public void onClickAction() {
                        if (!avatarLink.equals(user.getAvatarLink())) {
                            uploadImageToFirebase(user);
                        } else {
                            updateUser();
                        }
                    }
                };
                dialogBox.show();
                dismiss();
            }

            @Override
            public void onVerificationFailed() {
                errorMessage = StringUtils.MES_ERROR_INVALID_OTP;
                displayErrorMessage();
            }
        };
        dialogBoxOTP.getWindow()
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxOTP.show();
    }

    private void updateUser() {
        user.setFirstName(editFirstName.getText().toString());
        user.setLastName(editLastName.getText().toString());
        user.setMail(editMail.getText().toString());
        user.setAvatarLink(avatarLink);
        APIUserCaller.updateUserProfile(user, getApplication(), new APIListener() {
            @Override
            public void onUpdateSuccessful() {
                displaySuccessfulMessage();
            }

            @Override
            public void onFailedAPICall(int code) {
                errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                displayErrorMessage();
            }
        });
    }

    private void registerUser(User newUser) {
        APIUserCaller.registerNewUser(newUser, getApplication(), new APIListener() {
            @Override
            public void onCompletingRegistrationRequest() {
                APIUserCaller.logInWithUsernameAndPassword(newUser.getUsername(),
                        newUser.getPassword(), getApplication(), new APIListener() {
                            @Override
                            public void onUserFound(User registeredUser, String message) {
                                user = registeredUser;
                                displaySuccessfulMessage();
                            }

                            @Override
                            public void onFailedAPICall(int errorCode) {
                                errorMessage = StringUtils.MES_ERROR_FAILED_API_CALL;
                                displayErrorMessage();
                            }
                        });
            }

            @Override
            public void onFailedAPICall(int errorCode) {
                errorMessage = StringUtils.MES_ERROR_DUPLICATE_USERNAME;
                displayErrorMessage();
            }
        });
    }

    private void displaySuccessfulMessage() {
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        dialogBoxAlert = new DialogBoxAlert(activity, IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS,
                StringUtils.MES_SUCCESSFUL_REGISTRATION,"") {
            @Override
            public void onClickAction() {
                activity.getIntent().putExtra("USER", user);
                activity.setResult(Activity.RESULT_OK, activity.getIntent());
                activity.finish();
            }
        };
        dialogBoxAlert.getWindow()
                      .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();
    }

    private void displayErrorMessage() {
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        dialogBoxAlert = new DialogBoxAlert(activity,
                IntegerUtils.CONFIRM_ACTION_CODE_FAILED, errorMessage,"");
        dialogBoxAlert.getWindow()
                      .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();

    }

    private void enableButtonConfirm() {
        if(checkEmptyFields()) {
            btnConfirm.setEnabled(false);
            btnConfirm.getBackground().setTint(getResources().getColor(R.color.gray_light));
        } else {
            btnConfirm.setEnabled(true);
            btnConfirm.getBackground().setTint(getResources().getColor(R.color.blue_main));
        }
    }

    private boolean checkEmptyFields() {
        if (googleId == null) {
            if(editUsername.getText().toString().isEmpty()) {
                return true;
            }
            if (editPassword.getText().toString().isEmpty()) {
                return true;
            }
            if (editPasswordConfirm.getText().toString().isEmpty()) {
                return true;
            }
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
        return false;
    }

    private String checkValidInput() {
        if (googleId == null) {
            if (!editUsername.getText().toString().matches(StringUtils.USERNAME_REGEX)) {
                return StringUtils.MES_ERROR_INVALID_USERNAME;
            }
            if (!editPassword.getText().toString().matches(StringUtils.PASSWORD_REGEX)) {
                return StringUtils.MES_ERROR_INVALID_PASSWORD;
            }
            if (!editPassword.getText().toString().equals(editPasswordConfirm.getText().toString())) {
                return StringUtils.MES_ERROR_INCORRECT_CONFIRM_PASSWORD;
            }
        }
        if (!editPhoneNumber.getText().toString().matches(StringUtils.PHONE_REGEX)) {
            return StringUtils.MES_ERROR_INVALID_NUMBER;
        }
        if(!editMail.getText().toString().isEmpty()) {
            if (!editMail.getText().toString().matches(StringUtils.MAIL_REGEX)) {
                return StringUtils.MES_ERROR_INVALID_MAIL;
            }
        }
        return null;
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
        imgBackFromAccountDetailRegister.performClick();
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
        }
    }
}