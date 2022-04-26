package dev.wsgroup.main.views.activities.account;

import android.content.Context;
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

import java.util.ArrayList;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIAddressCaller;
import dev.wsgroup.main.models.dtos.Address;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewAddressListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.views.dialogbox.DialogBoxAddress;

public class DeliveryAddressSelectActivity extends AppCompatActivity {

    private ImageView imgBackFromDeliveryAddress, checkboxDefaultAddress;
    private RelativeLayout layoutLoading;
    private ConstraintLayout layoutNoDefaultAddress, layoutDefaultAddress, layoutAddAddress;
    private LinearLayout layoutOtherAddress, layoutScreen, layoutFailed;
    private TextView txtAddressStreet, txtAddressProvince, lblRetry;
    private RecyclerView recViewAddress;

    private SharedPreferences sharedPreferences;
    private String token;
    private Address currentAddress, defaultAddress;
    private List<Address> customerAddressList;
    private DialogBoxAddress dialogBoxAddress;
    private RecViewAddressListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_address_select);
        this.getSupportActionBar().hide();

        imgBackFromDeliveryAddress = findViewById(R.id.imgBackFromDeliveryAddress);
        checkboxDefaultAddress = findViewById(R.id.checkboxDefaultAddress);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutNoDefaultAddress = findViewById(R.id.layoutNoDefaultAddress);
        layoutDefaultAddress = findViewById(R.id.layoutDefaultAddress);
        layoutAddAddress = findViewById(R.id.layoutAddAddress);
        layoutOtherAddress = findViewById(R.id.LayoutOtherAddress);
        layoutScreen = findViewById(R.id.layoutScreen);
        layoutFailed = findViewById(R.id.layoutFailed);
        txtAddressStreet = findViewById(R.id.txtAddressStreet);
        txtAddressProvince = findViewById(R.id.txtAddressProvince);
        lblRetry = findViewById(R.id.lblRetry);
        recViewAddress = findViewById(R.id.recViewAddress);

        getAddress();

        imgBackFromDeliveryAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getIntent().putExtra("ADDRESS", currentAddress);
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });

        layoutNoDefaultAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBoxAddress = new DialogBoxAddress(DeliveryAddressSelectActivity.this,
                        getApplicationContext(), null, true) {
                    @Override
                    public void onAddressAdd(Address address) {
                        super.onAddressAdd(address);
                        currentAddress = address;
                        setupLayout();
                        setupDefaultAddress(address);
                        defaultAddress.setSelectedFlag(true);
                        setupCheckBox(checkboxDefaultAddress, true);
                    }

                    @Override
                    public void onUpdateFailed() {
                        MethodUtils.displayErrorAPIMessage(DeliveryAddressSelectActivity.this);
                    }
                };
                dialogBoxAddress.getWindow()
                                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxAddress.show();
            }
        });
        layoutDefaultAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultAddress.setSelectedFlag(true);
                currentAddress = defaultAddress;
                setupCheckBox(checkboxDefaultAddress, true);
                clearAddressListSelection();
                setupAddressList();
            }
        });

        layoutDefaultAddress.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dialogBoxAddress = new DialogBoxAddress(DeliveryAddressSelectActivity.this,
                        getApplicationContext(), defaultAddress, true) {
                    @Override
                    public void onAddressUpdate(Address address) {
                        super.onAddressUpdate(address);
                        currentAddress = address;
                        setupDefaultAddress(address);
                    }

                    @Override
                    public void onUpdateFailed() {
                        MethodUtils.displayErrorAPIMessage(DeliveryAddressSelectActivity.this);
                    }
                };
                dialogBoxAddress.getWindow()
                                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxAddress.show();
                return true;
            }
        });

        layoutAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogBoxAddress = new DialogBoxAddress(DeliveryAddressSelectActivity.this,
                        getApplicationContext(), null, false) {
                    @Override
                    public void onAddressAdd(Address address) {
                        super.onAddressAdd(address);
                        customerAddressList.add(address);
                        setupAddressList();
                    }

                    @Override
                    public void onUpdateFailed() {
                        MethodUtils.displayErrorAPIMessage(DeliveryAddressSelectActivity.this);
                    }
                };
                dialogBoxAddress.getWindow()
                                .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxAddress.show();
            }
        });

        lblRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAddress();
            }
        });
    }

    private void getAddress() {
        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
        currentAddress = (Address) getIntent().getSerializableExtra("ADDRESS");
        setLoadingState();
        setupLayout();
        if (currentAddress != null) {
            APIAddressCaller.getAllAddress(token, null, getApplication(), new APIListener() {
                @Override
                public void onAddressListFound(List<Address> addressList) {
                    super.onAddressListFound(addressList);
                    customerAddressList = addressList;
                    for (Address address : addressList) {
                        if (address.getDefaultFlag()) {
                            setupDefaultAddress(address);
                        }
                    }
                    defaultAddress.setSelectedFlag(true);
                    setupCheckBox(checkboxDefaultAddress, true);
                    currentAddress = defaultAddress;
                    customerAddressList.remove(defaultAddress);
                    setupAddressList();
                    setViewState();
                }

                @Override
                public void onFailedAPICall(int code) {
                    if (code == IntegerUtils.ERROR_NO_USER) {
                        MethodUtils.displayErrorAccountMessage(getApplicationContext(),
                                DeliveryAddressSelectActivity.this);
                    } else {
                        setFailedState();
                    }
                }
            });
        } else {
            customerAddressList = new ArrayList<>();
            setupAddressList();
            setViewState();
        }
    }

    private void setupCheckBox(ImageView checkbox, boolean selected) {
        if (!selected) {
            checkbox.setImageResource(R.drawable.ic_checkbox_unchecked);
            checkbox.setColorFilter(getApplicationContext().getResources()
                                                           .getColor(R.color.gray));
        } else {
            checkbox.setImageResource(R.drawable.ic_checkbox_checked);
            checkbox.setColorFilter(getApplicationContext().getResources()
                                                           .getColor(R.color.blue_main));
        }
    }
    private void clearAddressListSelection() {
        for (Address address : customerAddressList) {
            address.setSelectedFlag(false);
        }
    }

    private void setupAddressList() {
        recViewAddress.setAdapter(null);
        adapter = new RecViewAddressListAdapter(getApplicationContext(),
                DeliveryAddressSelectActivity.this, IntegerUtils.REQUEST_SELECT_ADDRESS) {
            @Override
            public void onCheckboxSelected(ImageView view, int position) {
                super.onCheckboxSelected(view, position);
                clearAddressListSelection();
                customerAddressList.get(position).setSelectedFlag(true);
                currentAddress = customerAddressList.get(position);
                setupAddressList();
                setupCheckBox(checkboxDefaultAddress,false);
            }

            @Override
            public void onAddressChange(Address address, int position, int action) {
                super.onAddressChange(address, position, action);
                switch (action) {
                    case IntegerUtils
                            .ADDRESS_ACTION_UPDATE: {
                        if (currentAddress.equals(customerAddressList.get(position))) {
                            currentAddress = address;
                        }
                        customerAddressList.set(position, address);
                        break;
                    }
                    case IntegerUtils
                            .ADDRESS_ACTION_DELETE: {
                        customerAddressList.remove(position);
                        if (currentAddress.equals(address)) {
                            currentAddress = defaultAddress;
                            defaultAddress.setSelectedFlag(true);
                            setupCheckBox(checkboxDefaultAddress, true);
                        }
                        break;
                    }
                }
            }
        };
        adapter.setAddressList(customerAddressList);
        recViewAddress.setAdapter(adapter);
        recViewAddress.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));
    }

    private void setupLayout() {
        if (currentAddress == null) {
            layoutNoDefaultAddress.setVisibility(View.VISIBLE);
            layoutDefaultAddress.setVisibility(View.GONE);
            layoutOtherAddress.setVisibility(View.GONE);
        } else {
            layoutNoDefaultAddress.setVisibility(View.GONE);
            layoutDefaultAddress.setVisibility(View.VISIBLE);
            layoutOtherAddress.setVisibility(View.VISIBLE);
        }
    }

    private void setupDefaultAddress(Address address) {
        defaultAddress = address;
        txtAddressStreet.setText(defaultAddress.getStreet());
        txtAddressProvince.setText(defaultAddress.getProvince());
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutScreen.setVisibility(View.GONE);
        layoutFailed.setVisibility(View.GONE);
    }

    private void setViewState() {
        layoutLoading.setVisibility(View.GONE);
        layoutScreen.setVisibility(View.VISIBLE);
        layoutFailed.setVisibility(View.GONE);
    }

    private void setFailedState() {
        layoutLoading.setVisibility(View.GONE);
        layoutScreen.setVisibility(View.GONE);
        layoutFailed.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        imgBackFromDeliveryAddress.performClick();
    }
}