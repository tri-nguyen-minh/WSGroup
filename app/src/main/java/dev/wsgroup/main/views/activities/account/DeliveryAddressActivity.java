package dev.wsgroup.main.views.activities.account;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIAddressCaller;
import dev.wsgroup.main.models.dtos.Address;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewAddressListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.views.activities.MainActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAddress;

public class DeliveryAddressActivity extends AppCompatActivity {

    private ImageView imgBackFromDeliveryAddress, imgDeliveryAddressHome;
    private RelativeLayout layoutLoading;
    private ConstraintLayout layoutNoDefaultAddress, layoutDefaultAddress, layoutAddAddress;
    private LinearLayout LayoutOtherAddress, layoutScreen;
    private TextView txtAddressStreet, txtAddressProvince;
    private RecyclerView recViewAddress;

    private SharedPreferences sharedPreferences;
    private String token;
    private DialogBoxAddress dialogBoxAddress;
    private RecViewAddressListAdapter adapter;
    private Address defaultAddress;
    private List<Address> customerAddressList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_address);
        this.getSupportActionBar().hide();

        imgBackFromDeliveryAddress = findViewById(R.id.imgBackFromDeliveryAddress);
        imgDeliveryAddressHome = findViewById(R.id.imgDeliveryAddressHome);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutNoDefaultAddress = findViewById(R.id.layoutNoDefaultAddress);
        layoutDefaultAddress = findViewById(R.id.layoutDefaultAddress);
        layoutAddAddress = findViewById(R.id.layoutAddAddress);
        LayoutOtherAddress = findViewById(R.id.LayoutOtherAddress);
        layoutScreen = findViewById(R.id.layoutScreen);
        txtAddressStreet = findViewById(R.id.txtAddressStreet);
        txtAddressProvince = findViewById(R.id.txtAddressProvince);
        recViewAddress = findViewById(R.id.recViewAddress);

        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");

        layoutLoading.setVisibility(View.VISIBLE);
        layoutScreen.setVisibility(View.INVISIBLE);

        APIAddressCaller.getAllAddress(token, null,
                getApplication(), new APIListener() {
            @Override
            public void onAddressListFound(List<Address> addressList) {
                super.onAddressListFound(addressList);
                customerAddressList = addressList;
                layoutLoading.setVisibility(View.INVISIBLE);
                layoutScreen.setVisibility(View.VISIBLE);
                if (customerAddressList.size() == 0) {
                    onNoAddress(true);
                } else {
                    onNoAddress(false);
                    for (Address address : addressList) {
                        if (address.getDefaultFlag()) {
                            setupDefaultAddress(address);
                        }
                    }
                    customerAddressList.remove(defaultAddress);
                    setupAddressList();
                }
            }
        });

        imgBackFromDeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });

        imgDeliveryAddressHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        layoutNoDefaultAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBoxAddress
                        = new DialogBoxAddress(DeliveryAddressActivity.this, getApplicationContext(),
                        null, true) {
                    @Override
                    public void onAddressAdd(Address address) {
                        super.onAddressAdd(address);
//                        currentAddress = address;
//                        setupLayout();
                        onNoAddress(false);
                        setupDefaultAddress(address);
//                        defaultAddress.setSelectedFlag(true);
//                        setupCheckBox(checkboxDefaultAddress, true);
                    }
                };
                dialogBoxAddress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxAddress.show();
            }
        });

        layoutDefaultAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBoxAddress = new DialogBoxAddress(DeliveryAddressActivity.this, getApplicationContext(),
                        defaultAddress, true) {
                    @Override
                    public void onAddressUpdate(Address address) {
                        super.onAddressUpdate(address);
                        setupDefaultAddress(address);
                    }
                };
                dialogBoxAddress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxAddress.show();
            }
        });

        layoutAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBoxAddress = new DialogBoxAddress(DeliveryAddressActivity.this, getApplicationContext(),
                        null, false) {
                    @Override
                    public void onAddressAdd(Address address) {
                        super.onAddressAdd(address);
                        customerAddressList.add(address);
                        setupAddressList();
                    }
                };
                dialogBoxAddress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxAddress.show();
            }
        });
    }

    private void onNoAddress(boolean check) {
        if (check) {
            layoutNoDefaultAddress.setVisibility(View.VISIBLE);
            layoutDefaultAddress.setVisibility(View.GONE);
            LayoutOtherAddress.setVisibility(View.GONE);
        } else {
            layoutNoDefaultAddress.setVisibility(View.GONE);
            layoutDefaultAddress.setVisibility(View.VISIBLE);
            LayoutOtherAddress.setVisibility(View.VISIBLE);
        }
    }

    private void setupDefaultAddress(Address address) {
        defaultAddress = address;
        txtAddressStreet.setText(defaultAddress.getStreet());
        txtAddressProvince.setText(defaultAddress.getProvince());
    }

    private void setupAddressList() {
        recViewAddress.setAdapter(null);
        adapter = new RecViewAddressListAdapter(getApplicationContext(),
                DeliveryAddressActivity.this, IntegerUtils.REQUEST_COMMON) {
//            @Override
//            public void onCheckboxSelected(ImageView view, int position) {
//                super.onCheckboxSelected(view, position);
////                clearAddressListSelection();
////                customerAddressList.get(position).setSelectedFlag(true);
////                currentAddress = customerAddressList.get(position);
//                setupAddressList();
////                setupCheckBox(checkboxDefaultAddress,false);
//            }

            @Override
            public void onAddressChange(Address address, int position, int action) {
                super.onAddressChange(address, position, action);
                switch (action) {
                    case IntegerUtils
                            .ADDRESS_ACTION_UPDATE: {
//                        if (currentAddress.equals(customerAddressList.get(position))) {
//                            currentAddress = address;
//                        }
                        customerAddressList.set(position, address);
                        break;
                    }
                    case IntegerUtils
                            .ADDRESS_ACTION_DELETE: {
                        customerAddressList.remove(position);
//                        if (currentAddress.equals(address)) {
//                            currentAddress = defaultAddress;
//                            defaultAddress.setSelectedFlag(true);
//                            setupCheckBox(checkboxDefaultAddress, true);
//                        }
                        break;
                    }
                }
//                setupAddressList();
            }
        };
        adapter.setAddressList(customerAddressList);
        recViewAddress.setAdapter(adapter);
        recViewAddress.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));
    }
}