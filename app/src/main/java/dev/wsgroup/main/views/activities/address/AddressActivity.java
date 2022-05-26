package dev.wsgroup.main.views.activities.address;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIAddressCaller;
import dev.wsgroup.main.models.dtos.Address;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class AddressActivity extends AppCompatActivity {

    private ImageView imgBack;
    private Spinner spinnerProvince, spinnerDistrict, spinnerWard;
    private RelativeLayout layoutLoading;
    private LinearLayout layoutFailed, layoutMain;
    private TextView lblRetry;
    private Button btnSave, btnDelete;
    private TextInputEditText editAddress;

    private SharedPreferences sharedPreferences;
    private String token;
    private DialogBoxLoading dialogBoxLoading;
    private List<Address> provinceList, districtList, wardList;
    private List<String> provinceNameList, districtNameList, wardNameList;
    private ArrayAdapter<String> provinceAdapter, districtAdapter, wardAdapter;
    private Address selectedAddress, currentAddress;
    private boolean loadingProvince, loadingDistrict, loadingWard;
    private int provinceIndex, districtIndex, wardIndex,
            provinceSelectedCheck, districtSelectedCheck, wardSelectedCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        this.getSupportActionBar().hide();

        imgBack = findViewById(R.id.imgBack);
        spinnerProvince = findViewById(R.id.spinnerProvince);
        spinnerDistrict = findViewById(R.id.spinnerDistrict);
        spinnerWard = findViewById(R.id.spinnerWard);
        layoutLoading = findViewById(R.id.layoutLoading);
        layoutFailed = findViewById(R.id.layoutFailed);
        layoutMain = findViewById(R.id.layoutMain);
        lblRetry = findViewById(R.id.lblRetry);
        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);
        editAddress = findViewById(R.id.editAddress);

        sharedPreferences = getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
        dialogBoxLoading = new DialogBoxLoading(AddressActivity.this);
        dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        currentAddress = (Address) getIntent().getSerializableExtra("ADDRESS");
        if (currentAddress == null) {
            currentAddress = new Address();
            currentAddress.setProvinceId("202");
            currentAddress.setDistrictId("1453");
            btnDelete.setVisibility(View.GONE);
            btnSave.setEnabled(false);
            btnSave.getBackground().setTint(getApplicationContext().getResources()
                   .getColor(R.color.gray_light));
        } else {
            editAddress.setText(currentAddress.getStreet());
            btnSave.setEnabled(true);
            btnSave.getBackground().setTint(getApplicationContext().getResources()
                   .getColor(R.color.blue_main));
            if (currentAddress.getDefaultFlag()) {
                btnDelete.setVisibility(View.GONE);
            }
        }
        provinceSelectedCheck = 0; districtSelectedCheck = 0; wardSelectedCheck = 0;
        performInitialLoad();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        editAddress.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    MethodUtils.hideKeyboard(view, getApplicationContext());
                }
            }
        });
        editAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                currentAddress.setStreet(editAddress.getText().toString());
                if (editAddress.getText().toString().isEmpty()) {
                    btnSave.setEnabled(false);
                    btnSave.getBackground().setTint(getApplicationContext().getResources()
                           .getColor(R.color.gray_light));
                } else {
                    btnSave.setEnabled(true);
                    btnSave.getBackground().setTint(getApplicationContext().getResources()
                           .getColor(R.color.blue_main));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        layoutMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editAddress.hasFocus()) {
                    editAddress.clearFocus();
                }
            }
        });

        lblRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                performInitialLoad();
            }
        });

        spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int positionInt, long positionLong) {
                if (++provinceSelectedCheck > 1) {
                    selectProvince(positionInt);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int positionInt, long positionLong) {
                if (++districtSelectedCheck > 1) {
                    selectDistrict(positionInt);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerWard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int positionInt, long positionLong) {
                if (++wardSelectedCheck > 1) {
                    selectWard(positionInt);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAddress.getId() == null) {
                    APIAddressCaller.addAddress(token, currentAddress,
                            getApplication(), new APIListener() {
                        public void onAddressAPICompleted(Address address) {
                            onAddedSuccessful();
                        }

                        @Override
                        public void onFailedAPICall(int code) {
                            onUpdateFailed(code);
                        }
                    });
                } else {
                    APIAddressCaller.updateAddress(token, currentAddress, getApplication(), new APIListener() {
                        public void onAddressAPICompleted(Address address) {
                            onUpdatedSuccessful();
                        }

                        @Override
                        public void onFailedAPICall(int code) {
                            onUpdateFailed(code);
                        }
                    });
                }
            }
        });
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                APIAddressCaller.deleteAddress(token, currentAddress,
                        getApplication(), new APIListener() {
                    public void onAddressAPICompleted(Address address) {
                        onDeletedSuccessful();
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        onUpdateFailed(code);
                    }
                });
            }
        });
    }

    private void performInitialLoad() {
        setLoadingState();
        loadingProvince = true; loadingDistrict = true; loadingWard = true;
        APIAddressCaller.getProvinceList(getApplication(), new APIListener() {
            @Override
            public void onAddressListFound(List<Address> addressList) {
                if (!addressList.isEmpty()) {
                    provinceList = addressList;
                    provinceNameList = new ArrayList<>();
                    for (Address address : provinceList) {
                        provinceNameList.add(address.getProvince());
                    }
                    loadingProvince = false;
                    performPostInitialLoad();
                } else {
                    setFailedState();
                }
            }

            @Override
            public void onNoJSONFound() {
                setFailedState();
            }

            @Override
            public void onFailedAPICall(int code) {
                setFailedState();
            }
        });
        APIAddressCaller.getDistrictList(currentAddress.getProvinceId(),
                getApplication(), new APIListener() {
            @Override
            public void onAddressListFound(List<Address> addressList) {
                if (!addressList.isEmpty()) {
                    districtList = addressList;
                } else {
                    districtList = new ArrayList<>();
                    selectedAddress = new Address();
                    selectedAddress.setProvinceId(currentAddress.getProvinceId());
                    selectedAddress.setDistrictId("0");
                    selectedAddress.setDistrict("No available district");
                }
                districtNameList = new ArrayList<>();
                for (Address address : districtList) {
                    districtNameList.add(address.getDistrict());
                }
                loadingDistrict = false;
                performPostInitialLoad();
            }

            @Override
            public void onNoJSONFound() {
                setFailedState();
            }

            @Override
            public void onFailedAPICall(int code) {
                setFailedState();
            }
        });
        APIAddressCaller.getWardList(currentAddress.getDistrictId(),
                getApplication(), new APIListener() {
            @Override
            public void onAddressListFound(List<Address> addressList) {
                if (!addressList.isEmpty()) {
                    wardList = addressList;
                } else {
                    wardList = new ArrayList<>();
                    selectedAddress = new Address();
                    selectedAddress.setDistrictId(currentAddress.getDistrictId());
                    selectedAddress.setWardId("0");
                    selectedAddress.setWard("No available ward");
                }
                wardNameList = new ArrayList<>();
                for (Address address : wardList) {
                    wardNameList.add(address.getWard());
                }
                loadingWard = false;
                performPostInitialLoad();
            }

            @Override
            public void onNoJSONFound() {
                setFailedState();
            }

            @Override
            public void onFailedAPICall(int code) {
                setFailedState();
            }
        });
    }

    private void performPostInitialLoad() {
        if (!loadingProvince && !loadingDistrict && !loadingWard) {
            provinceIndex = 0; districtIndex = 0; wardIndex = 0;
            getAddressIndexes();
            currentAddress.setProvinceId(provinceList.get(provinceIndex).getProvinceId());
            currentAddress.setProvince(provinceList.get(provinceIndex).getProvince());
            currentAddress.setDistrictId(districtList.get(districtIndex).getDistrictId());
            currentAddress.setDistrict(districtList.get(districtIndex).getDistrict());
            currentAddress.setWardId(wardList.get(wardIndex).getWardId());
            currentAddress.setWard(wardList.get(wardIndex).getWard());
            provinceAdapter = new ArrayAdapter<>(AddressActivity.this,
                    R.layout.spinner_selected_item, provinceNameList);
            provinceAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerProvince.setAdapter(provinceAdapter);
            districtAdapter = new ArrayAdapter<>(AddressActivity.this,
                    R.layout.spinner_selected_item, districtNameList);
            districtAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerDistrict.setAdapter(districtAdapter);
            wardAdapter = new ArrayAdapter<>(AddressActivity.this,
                    R.layout.spinner_selected_item, wardNameList);
            wardAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerWard.setAdapter(wardAdapter);
            spinnerProvince.setSelection(provinceIndex);
            spinnerDistrict.setSelection(districtIndex);
            spinnerWard.setSelection(wardIndex);
            setViewState();
        }
    }

    private void getAddressIndexes() {
        for (int i = 0; i < provinceList.size(); i++) {
            if (provinceList.get(i).getProvinceId().equals(currentAddress.getProvinceId())) {
                provinceIndex = i;
                break;
            }
        }
        for (int i = 0; i < districtList.size(); i++) {
            if (districtList.get(i).getDistrictId().equals(currentAddress.getDistrictId())) {
                districtIndex = i;
                break;
            }
        }
        for (int i = 0; i < wardList.size(); i++) {
            if (wardList.get(i).getWardId().equals(currentAddress.getWardId())) {
                wardIndex = i;
                break;
            }
        }
    }

    private void getDistrictList() {
        System.out.println("get d list");
        APIAddressCaller.getDistrictList(currentAddress.getProvinceId(),
                getApplication(), new APIListener() {
            @Override
            public void onAddressListFound(List<Address> addressList) {
                if (addressList.isEmpty()) {
                    selectedAddress = new Address();
                    selectedAddress.setDistrictId("0");
                    selectedAddress.setProvinceId(currentAddress.getProvinceId());
                    selectedAddress.setDistrict("No available district");
                    addressList.add(selectedAddress);
                }
                districtList = addressList;
                districtNameList = new ArrayList<>();
                for (Address address : districtList) {
                    districtNameList.add(address.getDistrict());
                }
                districtAdapter = new ArrayAdapter<>(AddressActivity.this,
                        R.layout.spinner_selected_item, districtNameList);
                districtAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinnerDistrict.setAdapter(districtAdapter);
                selectDistrict(0);
            }

            @Override
            public void onNoJSONFound() {
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                setFailedState();
            }

            @Override
            public void onFailedAPICall(int code) {
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                setFailedState();
            }
        });
    }

    private void getWardList() {
        if (!currentAddress.getDistrictId().equals("0")) {
            APIAddressCaller.getWardList(currentAddress.getDistrictId(),
                    getApplication(), new APIListener() {
                @Override
                public void onAddressListFound(List<Address> addressList) {
                    if (addressList.isEmpty()) {
                        selectedAddress = new Address();
                        selectedAddress.setWardId("0");
                        selectedAddress.setDistrictId(currentAddress.getDistrictId());
                        selectedAddress.setWard("No available ward");
                        addressList.add(selectedAddress);
                    }
                    wardList = addressList;
                    wardNameList = new ArrayList<>();
                    for (Address address : wardList) {
                        wardNameList.add(address.getWard());
                    }
                    wardAdapter = new ArrayAdapter<>(AddressActivity.this,
                            R.layout.spinner_selected_item, wardNameList);
                    wardAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    spinnerWard.setAdapter(wardAdapter);
                    selectWard(0);
                    if (dialogBoxLoading.isShowing()) {
                        dialogBoxLoading.dismiss();
                    }
                    spinnerDistrict.setEnabled(true);
                    spinnerWard.setEnabled(true);
                }

                @Override
                public void onNoJSONFound() {
                    if (dialogBoxLoading.isShowing()) {
                        dialogBoxLoading.dismiss();
                    }
                    setFailedState();
                }

                @Override
                public void onFailedAPICall(int code) {
                    if (dialogBoxLoading.isShowing()) {
                        dialogBoxLoading.dismiss();
                    }
                    setFailedState();
                }
            });
        } else {
            wardList = new ArrayList<>();
            selectedAddress = new Address();
            selectedAddress.setWardId("0");
            selectedAddress.setDistrictId(currentAddress.getDistrictId());
            selectedAddress.setWard("No available ward");
            wardList.add(selectedAddress);
            wardNameList = new ArrayList<>();
            for (Address address : wardList) {
                wardNameList.add(address.getWard());
            }
            wardAdapter = new ArrayAdapter<>(AddressActivity.this,
                    R.layout.spinner_selected_item, wardNameList);
            wardAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
            spinnerWard.setAdapter(wardAdapter);
            selectWard(0);
            if (dialogBoxLoading.isShowing()) {
                dialogBoxLoading.dismiss();
            }
            spinnerDistrict.setEnabled(true);
            spinnerWard.setEnabled(true);
        }
    }

    private void selectProvince(int position) {
        System.out.println("selected p");
        selectedAddress = provinceList.get(position);
        currentAddress.setProvinceId(selectedAddress.getProvinceId());
        currentAddress.setProvince(selectedAddress.getProvince());
        spinnerDistrict.setEnabled(false);
        if (!dialogBoxLoading.isShowing()) {
            dialogBoxLoading.show();
        }
        getDistrictList();
    }

    private void selectDistrict(int position) {
        System.out.println("selected d");
        selectedAddress = districtList.get(position);
        currentAddress.setDistrictId(selectedAddress.getDistrictId());
        currentAddress.setDistrict(selectedAddress.getDistrict());
        spinnerWard.setEnabled(false);
        if (!dialogBoxLoading.isShowing()) {
            dialogBoxLoading.show();
        }
        getWardList();
    }

    private void selectWard(int position) {
        System.out.println("selected w");
        selectedAddress = wardList.get(position);
        currentAddress.setWardId(selectedAddress.getWardId());
        currentAddress.setWard(selectedAddress.getWard());
    }

    private void onAddedSuccessful() {
        displaySuccessfulMessage(StringUtils.MES_SUCCESSFUL_ADD_ADDRESS);
    }

    private void onUpdatedSuccessful() {
        displaySuccessfulMessage(StringUtils.MES_SUCCESSFUL_UPDATE_ADDRESS);
    }

    private void onDeletedSuccessful() {
        displaySuccessfulMessage(StringUtils.MES_SUCCESSFUL_DELETE_ADDRESS);
    }

    private void displaySuccessfulMessage(String message) {
        DialogBoxAlert dialogBoxAlert = new DialogBoxAlert(AddressActivity.this,
                IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS, message, "") {
            @Override
            public void onClickAction() {
                getIntent().putExtra("ADDRESS", currentAddress);
                setResult(RESULT_OK, getIntent());
                finish();
            }
        };
        dialogBoxAlert.getWindow()
                      .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();
    }

    private void onUpdateFailed(int code) {
        if (code == IntegerUtils.ERROR_NO_USER) {
            MethodUtils.displayErrorAccountMessage(getApplicationContext(), AddressActivity.this);
        } else {
            MethodUtils.displayErrorAPIMessage(AddressActivity.this);
        }
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutMain.setVisibility(View.GONE);
        layoutFailed.setVisibility(View.GONE);
    }

    private void setViewState() {
        layoutLoading.setVisibility(View.GONE);
        layoutMain.setVisibility(View.VISIBLE);
        layoutFailed.setVisibility(View.GONE);
    }

    private void setFailedState() {
        layoutLoading.setVisibility(View.GONE);
        layoutMain.setVisibility(View.GONE);
        layoutFailed.setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        imgBack.performClick();
    }
}