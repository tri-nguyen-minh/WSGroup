package dev.wsgroup.main.models.dtos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class Supplier implements Serializable {

    private String id, accountId, name, address, mail, avatarLink;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
            System.out.println(avatarLink);
            System.out.println(avatarLink.equals("null"));
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
        supplier.setAddress(data.getString("address"));
        supplier.setAvatarLink(data.getString("avt"));
        supplier.setStatus(data.getBoolean("isdeleted"));
        return supplier;
    }

    public static Supplier getObjectFromDiscountJSON(JSONObject data) throws Exception{
        Supplier supplier = new Supplier();
        supplier.setId(data.getString("supplierid"));
        supplier.setAccountId(data.getString("accountid"));
        supplier.setName(data.getString("suppliername"));
        supplier.setMail(data.getString("supplieremai"));
        supplier.setAddress(data.getString("supplieraddress"));
        supplier.setAvatarLink(data.getString("supplieravt"));
        return supplier;
    }
}
