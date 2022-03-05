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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIAddressCaller;
import dev.wsgroup.main.models.dtos.Address;

public class DialogBoxAddress extends Dialog {

    private CardView cardViewParent;
    private ImageView imgCloseDialogBox;
    private EditText editStreet, editProvince;
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_address);
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
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        editProvince.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
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
                    APIAddressCaller.UpdateAddress(token, address, activity.getApplication(), new APIListener() {
                        @Override
                        public void onUpdateAddressSuccessful(Address address) {
                            dismiss();
                            onAddressUpdate(address);
                        }
                    });
                }
            });
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    APIAddressCaller.DeleteAddress(token, address, activity.getApplication(), new APIListener() {
                        @Override
                        public void onUpdateAddressSuccessful(Address address) {
                            dismiss();
                            onAddressDelete();
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
                    APIAddressCaller.AddAddress(token, address, activity.getApplication(), new APIListener() {
                        @Override
                        public void onUpdateAddressSuccessful(Address address) {
                            dismiss();
                            onAddressAdd(address);
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

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onAddressAdd(Address address) {}

    public void onAddressUpdate(Address address) {}

    public void onAddressDelete() {}
}
