package dev.wsgroup.main.models.dtos;

import org.json.JSONObject;

import java.io.Serializable;

public class Address implements Serializable {
    private String id, customerId, provinceId, province, districtId, district,
            wardId, ward, street, addressString;
    private boolean defaultFlag, selectedFlag;

    public Address() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrictId() {
        return districtId;
    }

    public void setDistrictId(String districtId) {
        this.districtId = districtId;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWardId() {
        return wardId;
    }

    public void setWardId(String wardId) {
        this.wardId = wardId;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public boolean getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(boolean defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    public boolean getSelectedFlag() {
        return selectedFlag;
    }

    public void setSelectedFlag(boolean selectedFlag) {
        this.selectedFlag = selectedFlag;
    }

    public String getAddressString() {
        return addressString;
    }

    public void setAddressString(String addressString) {
        this.addressString = addressString;
    }

    public String getDistrictString() {
        return ward + ", " + district;
    }

    public String getProvinceString() {
        return ward + ", " + district + ", " + province;
    }

    public String getFullAddressString() {
        return street + ", " + ward + ", " + district + ", " + province;
    }

    public static Address getObjectFromJSON(JSONObject jsonObject) throws Exception {
        Address address = new Address();
        address.setId(jsonObject.getString("id"));
        address.setCustomerId(jsonObject.getString("customerId"));
        address.setProvinceId(jsonObject.getString("provinceId"));
        address.setProvince(jsonObject.getString("province"));
        address.setDistrictId(jsonObject.getString("districtId"));
        address.setDistrict(jsonObject.getString("district"));
        address.setWardId(jsonObject.getString("wardId"));
        address.setWard(jsonObject.getString("ward"));
        address.setStreet(jsonObject.getString("street"));
        address.setDefaultFlag(jsonObject.getBoolean("isDefault"));
        address.setSelectedFlag(false);
        return address;
    }

    public static Address getProvinceFromAPI(JSONObject jsonObject) throws Exception {
        Address address = new Address();
        address.setProvinceId(jsonObject.getString("ProvinceID"));
        address.setProvince(jsonObject.getString("ProvinceName"));
        return address;
    }

    public static Address getDistrictFromAPI(JSONObject jsonObject) throws Exception {
        Address address = new Address();
        address.setDistrictId(jsonObject.getString("DistrictID"));
        address.setProvinceId(jsonObject.getString("ProvinceID"));
        address.setDistrict(jsonObject.getString("DistrictName"));
        return address;
    }

    public static Address getWardFromAPI(JSONObject jsonObject) throws Exception {
        Address address = new Address();
        address.setWardId(jsonObject.getString("WardCode"));
        address.setDistrictId(jsonObject.getString("DistrictID"));
        address.setWard(jsonObject.getString("WardName"));
        return address;
    }
}
