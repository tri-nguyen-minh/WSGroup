package dev.wsgroup.main.models.dtos;

import org.json.JSONObject;

import java.io.Serializable;

public class CustomerDiscount implements Serializable {

    private String id, customerId, status;
    private Discount discount;

    public CustomerDiscount() { }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Discount getDiscount() {
        return discount;
    }

    public void setDiscount(Discount discount) {
        this.discount = discount;
    }

    public static CustomerDiscount getObjectFromJSON(JSONObject data) throws Exception {
        CustomerDiscount customerDiscount = new CustomerDiscount();
        customerDiscount.setId(data.getString("id"));
        customerDiscount.setStatus(data.getString("customerDiscountCodeStatus"));
        Discount discount = Discount.getObjectFromJSON(data);
        customerDiscount.setDiscount(discount);
        return customerDiscount;
    }
}
