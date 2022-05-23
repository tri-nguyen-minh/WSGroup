package dev.wsgroup.main.models.dtos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Supplier implements Serializable {

    private String id, accountId, name, mail, avatarLink;
    private Address address;
    private boolean status;
    private LoyaltyStatus loyaltyStatus;

    public Supplier() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
        try {
            if (!avatarLink.equals("null")) {
                JSONArray jsonArray = new JSONArray(avatarLink);
                if (jsonArray.length() > 0) {
                    this.avatarLink = jsonArray.getJSONObject(0).getString("url");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAddressString(String addressString) {
        try {
            address = new Address();
            JSONObject jsonObject = new JSONObject(addressString);
            address.setProvinceId(jsonObject.getJSONObject("province").getString("provinceId"));
            address.setProvince(jsonObject.getJSONObject("province").getString("provinceName"));
            address.setDistrictId(jsonObject.getJSONObject("district").getString("districtId"));
            address.setDistrict(jsonObject.getJSONObject("district").getString("districtName"));
            address.setWardId(jsonObject.getJSONObject("ward").getString("wardId"));
            address.setWard(jsonObject.getJSONObject("ward").getString("wardName"));
            address.setStreet(jsonObject.getJSONObject("street").getString("streetName"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Address getAddress() {
        return address;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public LoyaltyStatus getLoyaltyStatus() {
        return loyaltyStatus;
    }

    public void setLoyaltyStatus(LoyaltyStatus loyaltyStatus) {
        this.loyaltyStatus = loyaltyStatus;
    }

    public static Supplier getObjectFromJSON(JSONObject data) throws Exception{
        Supplier supplier = new Supplier();
        supplier.setId(data.getString("id"));
        supplier.setAccountId(data.getString("accountid"));
        supplier.setName(data.getString("name"));
        supplier.setMail(data.getString("email"));
        supplier.setAddressString(data.getString("address"));
        supplier.setAvatarLink(data.getString("avt"));
        supplier.setStatus(data.getBoolean("isdeleted"));
        return supplier;
    }

    public static Supplier getObjectFromDiscountJSON(JSONObject data) throws Exception{
        Supplier supplier = new Supplier();
        supplier.setId(data.getString("supplierid"));
        supplier.setAccountId(data.getString("accountid"));
        supplier.setName(data.getString("suppliername"));
        supplier.setMail(data.getString("supplieremail"));
        supplier.setAddressString(data.getString("supplieraddress"));
        supplier.setAvatarLink(data.getString("supplieravt"));
        return supplier;
    }
}
