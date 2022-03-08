package dev.wsgroup.main.models.dtos;

import org.json.JSONObject;

import java.io.Serializable;

public class LoyaltyStatus implements Serializable {
    private String id;
    private Supplier supplier;
    private int discountPercent;
    private double discountPrice;

    public LoyaltyStatus() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public void setDiscountPriceFromTotal(double totalPrice) {
        this.discountPrice = (totalPrice * discountPercent) / 100;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public static LoyaltyStatus getObjectFromJSON(JSONObject data) throws Exception {
        LoyaltyStatus status = new LoyaltyStatus();
        status.setId(data.getString("id"));
        Supplier supplier = new Supplier();
        supplier.setId(data.getString("supplierid"));
        status.setSupplier(supplier);
        status.setDiscountPercent(data.getInt("discountpercent"));
        return status;
    }
}
