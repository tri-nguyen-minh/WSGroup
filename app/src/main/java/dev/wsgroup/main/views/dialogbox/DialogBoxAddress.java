package dev.wsgroup.main.views.dialogbox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIAddressCaller;
import dev.wsgroup.main.models.dtos.Address;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;

public class DialogBoxAddress extends Dialog {

    private CardView cardViewParent;
    private ImageView imgCloseDialogBox;
    private TextInputEditText editStreet, editProvince;
    private Button btnSave, btnDelete;

    private SharedPreferences sharedPreferences;
    private Address address;
    private Activity activity;
    private Context context;
    private boolean defaultFlag;
    private String token;

    public DialogBoxAddress(Activity activity, Context context, Address address, boolean defaultFlag) {
        super(activity);
        this.activity = activity;
        this.context = context;
        this.address = address;
        this.defaultFlag = defaultFlag;
        sharedPreferences = activity.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_address);
        setCancelable(false);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        cardViewParent = findViewById(R.id.cardViewParent);
        imgCloseDialogBox = findViewById(R.id.imgCloseDialogBox);
        editStreet = findViewById(R.id.editStreet);
        editProvince = findViewById(R.id.editProvince);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        if (defaultFlag || (address == null)) {
            btnDelete.setVisibility(View.GONE);
        }
        setButton();

        imgCloseDialogBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

        cardViewParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editStreet.hasFocus()) {
                    editStreet.clearFocus();
                }
                if (editProvince.hasFocus()) {
                    editProvince.clearFocus();
                }
            }
        });
        editStreet.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    MethodUtils.hideKeyboard(view, context);
                }
            }
        });
        editProvince.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    MethodUtils.hideKeyboard(view, context);
                }
            }
        });

        editStreet.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        editProvince.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setButton();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (address != null) {
            editStreet.setText(address.getStreet());
            editProvince.setText(address.getProvince());
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    address.setStreet(editStreet.getText().toString());
                    address.setProvince(editProvince.getText().toString());
                    APIAddressCaller.updateAddress(token, address,
                            activity.getApplication(), new APIListener() {
                        @Override
                        public void onUpdateAddressSuccessful(Address address) {
                            dismiss();
                            onAddressUpdate(address);
                        }

                        @Override
                        public void onFailedAPICall(int code) {
                            if (code == IntegerUtils.ERROR_NO_USER) {
                                MethodUtils.displayErrorAccountMessage(context, activity);
                            } else {
                                onUpdateFailed();
                            }
                        }
                    });
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    APIAddressCaller.deleteAddress(token, address,
                            activity.getApplication(), new APIListener() {
                        @Override
                        public void onUpdateAddressSuccessful(Address address) {
                            dismiss();
                            onAddressDelete();
                        }

                        @Override
                        public void onFailedAPICall(int code) {
                            if (code == IntegerUtils.ERROR_NO_USER) {
                                MethodUtils.displayErrorAccountMessage(context, activity);
                            } else {
                                onUpdateFailed();
                            }
                        }
                    });
                }
            });
        } else {
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    address = new Address();
                    address.setStreet(editStreet.getText().toString());
                    address.setProvince(editProvince.getText().toString());
                    address.setAddressStringAuto();
                    APIAddressCaller.addAddress(token, address,
                            activity.getApplication(), new APIListener() {
                        @Override
                        public void onUpdateAddressSuccessful(Address address) {
                            dismiss();
                            onAddressAdd(address);
                        }

                        @Override
                        public void onFailedAPICall(int code) {
                            dismiss();
                            if (code == IntegerUtils.ERROR_NO_USER) {
                                MethodUtils.displayErrorAccountMessage(context, activity);
                            } else {
                                onUpdateFailed();
                            }
                        }
                    });
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setButton() {
        if (editStreet.getText().toString().isEmpty() || editProvince.getText().toString().isEmpty()) {
            btnSave.setEnabled(false);
            btnSave.getBackground().setTint(context.getResources().getColor(R.color.gray_light));
        } else {
            btnSave.setEnabled(true);
            btnSave.getBackground().setTint(context.getResources().getColor(R.color.blue_main));
        }
    }

    public void onAddressAdd(Address address) {}

    public void onAddressUpdate(Address address) {}

    public void onAddressDelete() {}

    public void onUpdateFailed() {}
}
