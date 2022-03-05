package dev.wsgroup.main.models.dtos;

import java.io.Serializable;

public class LoyaltyStatus implements Serializable {
    private String id, supplierId;
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

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
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
}
