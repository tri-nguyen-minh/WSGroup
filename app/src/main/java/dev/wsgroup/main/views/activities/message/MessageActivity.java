package dev.wsgroup.main.views.activities.message;

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
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIChatCaller;
import dev.wsgroup.main.models.apis.callers.APISupplierCaller;
import dev.wsgroup.main.models.dtos.Message;
import dev.wsgroup.main.models.dtos.MessageFirebase;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewMessageAdapter;
import dev.wsgroup.main.models.services.FirebaseReferences;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxSelectImage;

public class MessageActivity extends AppCompatActivity {

    private ConstraintLayout layoutParent;
    private ImageView imgBackFromMessage, imgMessageHome, imgMessageImage;
    private RelativeLayout layoutLoading;
    private LinearLayout layoutNoMessage, layoutFailedGettingMessage;
    private RecyclerView recViewMessage;
    private EditText editWriteMessage;
    private TextView txtSend, lblRetryGetMessage, txtNewMessageSupplierName;
    private FrameLayout layoutBody;

    private SharedPreferences sharedPreferences;
    private FirebaseReferences references;
    private List<Message> messageList;
    private String token, userAccountId, supplierAccountId;
    private Supplier supplier;
    private Message newMessage;
    private boolean messageLoading, profileLoading, supplierCheck, firstLoadCheck;
    private RecViewMessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        this.getSupportActionBar().hide();

        layoutParent = findViewById(R.id.layoutParent);
        imgBackFromMessage = findViewById(R.id.imgBackFromMessage);
        imgMessageHome = findViewById(R.id.imgMessageHome);
        imgMessageImage = findViewById(R.id.imgMessageImage);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutNoMessage = findViewById(R.id.layoutNoMessage);
        layoutFailedGettingMessage = findViewById(R.id.layoutFailedGettingMessage);
        recViewMessage = findViewById(R.id.recViewMessage);
        editWriteMessage = findViewById(R.id.editWriteMessage);
        txtSend = findViewById(R.id.txtSend);
        lblRetryGetMessage = findViewById(R.id.lblRetryGetMessage);
        txtNewMessageSupplierName = findViewById(R.id.txtNewMessageSupplierName);
        layoutBody = findViewById(R.id.layoutBody);

        recViewMessage.setItemViewCacheSize(10);

        references = new FirebaseReferences();
        getConversation();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editWriteMessage.hasFocus()) {
                    editWriteMessage.clearFocus();
                }
            }
        };

        imgBackFromMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });
        imgMessageHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        lblRetryGetMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getConversation();
            }
        });
        editWriteMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    editWriteMessage.setMaxLines(4);
                    editWriteMessage.setEllipsize(null);
                } else {
                    MethodUtils.hideKeyboard(view, getApplicationContext());
                    editWriteMessage.setMaxLines(1);
                    editWriteMessage.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
        });
        imgMessageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editWriteMessage.hasFocus()) {
                    editWriteMessage.clearFocus();
                }
                DialogBoxSelectImage dialogBox
                        = new DialogBoxSelectImage(MessageActivity.this) {
                    @RequiresApi(api = Build.VERSION_CODES.M)
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
        layoutParent.setOnClickListener(listener);
        recViewMessage.setOnClickListener(listener);
        layoutBody.setOnClickListener(listener);
        txtSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editWriteMessage.hasFocus()) {
                    editWriteMessage.clearFocus();
                }
                sendNewMessage(editWriteMessage.getText().toString(), null);
            }
        });
    }

    private void getConversation() {
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
        userAccountId = getIntent().getStringExtra("USER_ID");
        supplierAccountId = getIntent().getStringExtra("SUPPLIER_ID");
        supplierCheck = getIntent().getBooleanExtra("RECIPIENT_CHECK", true);
        messageList = new ArrayList<>();
        messageLoading = false; profileLoading = false; firstLoadCheck = true;
        startFirebase();
        getProfile();
    }

    private void startFirebase() {
        references.getConversation(userAccountId, supplierAccountId)
                  .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                MessageFirebase messageFirebase = null;
                if (snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        messageFirebase = dataSnapshot.getValue(MessageFirebase.class);

                    }
                }
                if (firstLoadCheck || messageFirebase.getFrom().equals(supplierAccountId)) {
                    messageLoading = true; firstLoadCheck = false;
                    getMessage();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
            }
        });
    }

    private void getMessage() {
        setLoadingState();
        APIChatCaller.getConversation(token, userAccountId, supplierAccountId,
                getApplication(), new APIListener() {
            @Override
            public void onMessageListFound(List<Message> list) {
                messageList = list;
                messageLoading = false;
                setConversation();
            }

            @Override
            public void onFailedAPICall(int code) {
                if (code == IntegerUtils.ERROR_NO_USER) {
                    MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                            MessageActivity.this);
                } else {
                    setFailedState();
                }
            }
        });
    }

    private void getProfile() {
        if (!supplierCheck) {
            profileLoading = false;
            supplier = new Supplier();
            supplier.setAccountId(supplierAccountId);
            supplier.setName("Customer Service");
            setConversation();
        } else {
            profileLoading = true;
            Set<String> idSet = new LinkedHashSet<>();
            idSet.add(supplierAccountId);
            APISupplierCaller.getSupplierListByAccountId(idSet, null,
                    getApplication(), new APIListener() {
                @Override
                public void onSupplierListFound(List<Supplier> supplierList) {
                    if (supplierList.size() > 0) {
                        supplier = supplierList.get(0);
                        profileLoading = false;
                        setConversation();
                    } else {
                        setFailedState();
                    }
                }

                @Override
                public void onFailedAPICall(int code) {
                    setFailedState();
                }
            });
        }
    }

    private void setConversation() {
        if (!messageLoading && !profileLoading) {
            txtNewMessageSupplierName.setText(supplier.getName());
            adapter = new RecViewMessageAdapter(getApplicationContext(),
                    MessageActivity.this, userAccountId) {
                @Override
                public void onClick() {
                    if (editWriteMessage.hasFocus()) {
                        editWriteMessage.clearFocus();
                    }
                }
            };
            recViewMessage.setAdapter(adapter);
            recViewMessage.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                    LinearLayoutManager.VERTICAL, true));
            if (messageList.isEmpty()) {
                setNoMessageState();
            } else {
                adapter.setMessageList(messageList);
                setListFoundState();
            }
        }
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

    private void sendNewMessage(String message, String image) {
        imgMessageImage.setEnabled(false);
        editWriteMessage.setEnabled(false);
        txtSend.setEnabled(false);
        newMessage = new Message();
        newMessage.setFromId(userAccountId);
        newMessage.setToId(supplierAccountId);
        if (message != null) {
            newMessage.setMessage(editWriteMessage.getText().toString());
        }
        if (image != null) {
            newMessage.setLink(image);
        }
        references.addMessage(newMessage).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                editWriteMessage.setText("");
                imgMessageImage.setEnabled(true);
                editWriteMessage.setEnabled(true);
                txtSend.setEnabled(true);
                adapter.updateMessage(newMessage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void uploadImageToFirebase(Uri uri) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously().addOnSuccessListener(this, new  OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference();
                StorageReference ref = storageReference.child("images/messages/" + UUID.randomUUID());
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
                                    MethodUtils.displayErrorAPIMessage(MessageActivity.this);
                                    throw task.getException();
                                }
                                return ref.getDownloadUrl();
                            }
                        });
                        urlTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    sendNewMessage(null, task.getResult().toString());
                                } else {
                                    MethodUtils.displayErrorAPIMessage(MessageActivity.this);
                                }
                            }
                        });
                    }
                });
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        e.printStackTrace();
                        MethodUtils.displayErrorAPIMessage(MessageActivity.this);
                    }
                });

            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                MethodUtils.displayErrorAPIMessage(MessageActivity.this);
            }
        });
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoMessage.setVisibility(View.GONE);
        layoutFailedGettingMessage.setVisibility(View.GONE);
        recViewMessage.setVisibility(View.GONE);
    }

    private void setNoMessageState() {
        layoutLoading.setVisibility(View.GONE);
        layoutNoMessage.setVisibility(View.VISIBLE);
        layoutFailedGettingMessage.setVisibility(View.GONE);
        recViewMessage.setVisibility(View.GONE);
    }

    private void setFailedState() {
        layoutLoading.setVisibility(View.GONE);
        layoutNoMessage.setVisibility(View.GONE);
        layoutFailedGettingMessage.setVisibility(View.VISIBLE);
        recViewMessage.setVisibility(View.GONE);
    }

    private void setListFoundState() {
        layoutLoading.setVisibility(View.GONE);
        layoutNoMessage.setVisibility(View.GONE);
        layoutFailedGettingMessage.setVisibility(View.GONE);
        recViewMessage.setVisibility(View.VISIBLE);
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
        imgBackFromMessage.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri uri = null;
            if (requestCode == IntegerUtils.REQUEST_TAKE_IMAGE) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                uri = MethodUtils.getImageUri(getApplicationContext(), image);
            } else if (requestCode == IntegerUtils.REQUEST_SELECT_IMAGE) {
                uri = data.getData();
            }
            if (uri != null) {
                uploadImageToFirebase(uri);
            } else {
                MethodUtils.displayErrorAPIMessage(MessageActivity.this);
            }

        }
    }
}