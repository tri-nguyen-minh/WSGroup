package dev.wsgroup.main.views.activities.order;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIOrderCaller;
import dev.wsgroup.main.models.apis.callers.APIUserCaller;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderHistory;
import dev.wsgroup.main.models.dtos.User;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewImageAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.activities.account.AccountInformationActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;
import dev.wsgroup.main.views.dialogbox.DialogBoxSelectImage;

public class RequestActivity extends AppCompatActivity {

    private ImageView imgBackFromReturnRequest, imgReturnRequestHome;
    private EditText editReason;
    private LinearLayout layoutFailedGettingRequest, layoutRequest;
    private ConstraintLayout layoutParent;
    private RelativeLayout layoutLoading;
    private TextView txtOrderCode, txtLetterCount, lblLetterSeparator, lblLetterCount,
            lblRetry, txtSupplier, txtPrice, txtRecipient;
    private RecyclerView recViewImage;
    private Button btnSubmitRequest;

    private SharedPreferences sharedPreferences;
    private Intent intent;
    private int letterCount, imageCount, requestCount;
    private String token, orderCode, reasonString, description, uuid, root;
    private List<String> imageList, imageLinkList;
    private Order order;
    private List<OrderHistory> orderHistoryList;
    private User user;
    private Uri uri;
    private RecViewImageAdapter adapter;
    private DialogBoxLoading dialogBoxLoading;
    private DialogBoxConfirm dialogBoxConfirm;
    private DialogBoxAlert dialogBoxAlert;
    private UploadTask uploadTask;
    private StorageReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);
        this.getSupportActionBar().hide();

        imgBackFromReturnRequest = findViewById(R.id.imgBackFromReturnRequest);
        imgReturnRequestHome = findViewById(R.id.imgReturnRequestHome);
        editReason = findViewById(R.id.editReason);
        layoutFailedGettingRequest = findViewById(R.id.layoutFailedGettingRequest);
        layoutRequest = findViewById(R.id.layoutRequest);
        layoutParent = findViewById(R.id.layoutParent);
        layoutLoading = findViewById(R.id.layoutLoading);
        txtOrderCode = findViewById(R.id.txtOrderCode);
        txtLetterCount = findViewById(R.id.txtLetterCount);
        lblLetterSeparator = findViewById(R.id.lblLetterSeparator);
        lblLetterCount = findViewById(R.id.lblLetterCount);
        lblRetry = findViewById(R.id.lblRetry);
        txtSupplier = findViewById(R.id.txtSupplier);
        txtPrice = findViewById(R.id.txtPrice);
        txtRecipient = findViewById(R.id.txtRecipient);
        recViewImage = findViewById(R.id.recViewImage);
        btnSubmitRequest = findViewById(R.id.btnSubmitRequest);

        imageList = new ArrayList<>();
        imageList.add(null);
        setupRequest();

        adapter = new RecViewImageAdapter(getApplicationContext(),
                RequestActivity.this, R.layout.recycle_view_image_large) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void selectImage() {
                DialogBoxSelectImage dialogBox
                        = new DialogBoxSelectImage(RequestActivity.this) {
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
                        RequestActivity.this.selectImage();
                    }
                };
                dialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBox.show();
            }
        };
        adapter.setImageList(imageList);
        recViewImage.setAdapter(adapter);
        recViewImage.setLayoutManager(new GridLayoutManager(getApplicationContext(), 3));

        imgBackFromReturnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
        imgReturnRequestHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editReason.hasFocus()) {
                    editReason.clearFocus();
                }
            }
        });
        editReason.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    MethodUtils.hideKeyboard(view, getApplicationContext());
                }
            }
        });

        editReason.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                reasonString = editReason.getText().toString();
                letterCount = charSequence.length();
                checkLetterCount();
                enablingButton();

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        lblRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupRequest();
            }
        });

        btnSubmitRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBoxConfirm = new DialogBoxConfirm(RequestActivity.this,
                        StringUtils.MES_CONFIRM_SUBMIT_REQUEST) {
                    @Override
                    public void onYesClicked() {
                        dialogBoxLoading = new DialogBoxLoading(RequestActivity.this);
                        dialogBoxLoading.getWindow()
                                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialogBoxLoading.show();
                        imageLinkList = new ArrayList<>();
                        if (imageList == null || imageList.size() < 2) {
                            sendRequest();
                        } else {
                            if (imageList.get(imageList.size() - 1) == null) {
                                imageList.remove(imageList.size() - 1);
                            }
                            uploadImageList();
                        }
                    }
                };
                if (requestCount == 1) {
                    description = "This is your final request.";
                    dialogBoxConfirm.setDescription(description);
                }
                dialogBoxConfirm.getWindow()
                                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxConfirm.show();
            }
        });
    }

    private void setupRequest() {
        setLoadingState();
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
        orderCode = getIntent().getStringExtra("ORDER_CODE");
        requestCount = 0;
        APIOrderCaller.getOrderByOrderCode(orderCode, getApplication(), new APIListener() {
            @Override
            public void onOrderFound(List<Order> orderList) {
                setReadyState();
                if (orderList.size() > 0) {
                    order = orderList.get(0);
                    APIOrderCaller.getOrderHistoryByOrderCode(order.getCode(),
                            getApplication(), new APIListener() {
                        @Override
                        public void onOrderHistoryFound(List<OrderHistory> historyList) {
                            orderHistoryList = historyList;
                            setupView();
                        }

                        @Override
                        public void onFailedAPICall(int code) {
                            setFailedState();
                        }
                    });
                } else {
                    setFailedState();
                }
            }

            @Override
            public void onFailedAPICall(int code) {
                setFailedState();
            }
        });
        APIUserCaller.findUserByToken(token, getApplication(), new APIListener() {
            @Override
            public void onUserFound(User foundUser, String message) {
                user = foundUser;
                setupView();
            }

            @Override
            public void onFailedAPICall(int code) {
                if (code == IntegerUtils.ERROR_NO_USER) {
                    MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                            RequestActivity.this);
                } else {
                    setFailedState();
                }
            }
        });
    }


    private boolean checkValidRequest() {
        if (!editReason.getText().toString().isEmpty() && (imageList.size() > 0)) {
            return true;
        }
        return false;
    }

    private void enablingButton() {
        if (checkValidRequest()) {
            btnSubmitRequest.setEnabled(true);
            btnSubmitRequest.getBackground().setTint(getResources().getColor(R.color.blue_main));
        } else {
            btnSubmitRequest.setEnabled(false);
            btnSubmitRequest.getBackground().setTint(getResources().getColor(R.color.gray_light));
        }
    }

    private void checkLetterCount() {
        txtLetterCount.setText(letterCount + "");
        if (letterCount == 200) {
            txtLetterCount.setTextColor(getResources().getColor(R.color.red));
            lblLetterSeparator.setTextColor(getResources().getColor(R.color.red));
            lblLetterCount.setTextColor(getResources().getColor(R.color.red));
        } else {
            txtLetterCount.setTextColor(getResources().getColor(R.color.gray));
            lblLetterSeparator.setTextColor(getResources().getColor(R.color.gray));
            lblLetterCount.setTextColor(getResources().getColor(R.color.gray));
        }
    }

    private void findReturnRequestCount() {
        for (OrderHistory history : orderHistoryList) {
            if (history.getStatus().equals("returning")) {
                requestCount++;
            }
        }
    }

    private void getDescription() {
        description = "is requested for return by Customer " + user.getDisplayName();
        description += " for: " + reasonString;
    }

    private void uploadImageList() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageMetadata metadata = new StorageMetadata.Builder()
                        .setContentType("image/jpeg")
                        .build();
                imageCount = imageList.size();
                for (String string : imageList) {
                    uuid = UUID.randomUUID().toString();
                    imageLinkList.add(MethodUtils.getFirebaseImageProofLink(uuid));
                    root = "images/proof/" + uuid;
                    reference = storage.getReference().child(root);
                    uri = Uri.parse(string);
                    uploadTask = reference.putFile(uri, metadata);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> urlTask
                                    = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,
                                    Task<Uri>>() {
                                @Override
                                public Task<Uri> then(Task<UploadTask.TaskSnapshot> task)
                                        throws Exception {
                                    if (!task.isSuccessful()) {
                                        if (dialogBoxLoading.isShowing()) {
                                            dialogBoxLoading.dismiss();
                                        }
                                        MethodUtils.displayErrorAPIMessage(RequestActivity.this);
                                        throw task.getException();
                                    }
                                    return reference.getDownloadUrl();
                                }
                            });
                            urlTask.addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        imageCount--;
                                        if (imageCount == 0) {
                                            sendRequest();
                                        }
                                    } else {
                                        if (dialogBoxLoading.isShowing()) {
                                            dialogBoxLoading.dismiss();
                                        }
                                        MethodUtils.displayErrorAPIMessage(RequestActivity.this);
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
                            MethodUtils.displayErrorAPIMessage(RequestActivity.this);
                        }
                    });
                }
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                MethodUtils.displayErrorAPIMessage(RequestActivity.this);
            }
        });
    }

    private void takeImage() {
        intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, IntegerUtils.REQUEST_TAKE_IMAGE);
    }


    private void selectImage() {
        intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(Intent.createChooser(intent, "Select Images"),
                                    IntegerUtils.REQUEST_SELECT_IMAGE);
    }

    private void setupView() {
        if (order != null && user != null && orderHistoryList != null) {
            txtOrderCode.setText(order.getCode());
            double price = order.getTotalPrice() - order.getDiscountPrice() - order.getAdvanceFee();
            txtSupplier.setText(order.getSupplier().getName());
            txtPrice.setText(MethodUtils.formatPriceString(price));
            findReturnRequestCount();
            if (requestCount < 1) {
                txtRecipient.setText("Supplier");
            } else {
                txtRecipient.setText("Customer Service");
            }
            enablingButton();
            setReadyState();
        }
    }

    private void sendRequest() {
        getDescription();
        APIOrderCaller.updateOrderStatus(token, order, description, imageLinkList,
                "returning", getApplication(), new APIListener() {
                    @Override
                    public void onUpdateSuccessful() {
                        displayFinalMessage(StringUtils.MES_SUCCESSFUL_UPDATE_ORDER);
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        if (dialogBoxLoading.isShowing()) {
                            dialogBoxLoading.dismiss();
                        }if (code == IntegerUtils.ERROR_NO_USER) {
                            MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                                    RequestActivity.this);
                        } else {
                            MethodUtils.displayErrorAPIMessage(RequestActivity.this);
                        }
                    }
                });
    }

    private void displayFinalMessage(String message) {
        if (dialogBoxLoading.isShowing()) {
            dialogBoxLoading.dismiss();
        }
        dialogBoxAlert = new DialogBoxAlert(RequestActivity.this,
                IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS, message, "") {
            @Override
            public void onClickAction() {
                setResult(RESULT_OK);
                finish();
            }
        };
        dialogBoxAlert.getWindow()
                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutFailedGettingRequest.setVisibility(View.GONE);
        layoutRequest.setVisibility(View.GONE);
    }

    private void setFailedState() {
        layoutLoading.setVisibility(View.GONE);
        layoutFailedGettingRequest.setVisibility(View.VISIBLE);
        layoutRequest.setVisibility(View.GONE);
    }

    private void setReadyState() {
        layoutLoading.setVisibility(View.GONE);
        layoutFailedGettingRequest.setVisibility(View.GONE);
        layoutRequest.setVisibility(View.VISIBLE);
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
        imgBackFromReturnRequest.performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uri = null;
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == IntegerUtils.REQUEST_SELECT_IMAGE) {
                imageList = new ArrayList<>();
                if (data.getData() != null) {
                    uri = data.getData();
                    imageList.add(uri.toString());
                } else if (data.getClipData() != null) {
                    for (int i = 0; i < 6 && i < data.getClipData().getItemCount(); i++) {
                        uri = data.getClipData().getItemAt(i).getUri();
                        imageList.add(uri.toString());
                    }
                }
                if (imageList.size() < 6) {
                    imageList.add(null);
                }
            } else if (requestCode == IntegerUtils.REQUEST_TAKE_IMAGE) {
                if (imageList == null || imageList.size() == 0) {
                    imageList = new ArrayList<>();
                    imageList.add(null);
                }
                Bitmap image = (Bitmap) data.getExtras().get("data");
                uri = MethodUtils.getImageUri(getApplicationContext(), image);
                imageList.remove(imageList.size() - 1);
                imageList.add(uri.toString());
                if (imageList.size() < 6) {
                    imageList.add(null);
                }
            }
            if (adapter != null) {
                adapter.updateList(imageList);
            }
        }
    }
}