package dev.wsgroup.main.models.dtos;

import org.json.JSONObject;

import java.io.Serializable;

public class Address implements Serializable {
    private String id, customerId, province, street, addressString;
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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
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

    public void setAddressStringAuto() {
        addressString = street + " " + province;
    }

    public static Address getObjectFromJSON(JSONObject jsonObject) throws Exception {
        Address address = new Address();
        address.setId(jsonObject.getString("id"));
        address.setCustomerId(jsonObject.getString("customerid"));
        address.setProvince(jsonObject.getString("province"));
        address.setStreet(jsonObject.getString("street"));
        address.setAddressString(address.getStreet() + " " + address.getProvince());
        address.setDefaultFlag(jsonObject.getBoolean("isdefault"));
        address.setSelectedFlag(false);
        return address;
    }
}
