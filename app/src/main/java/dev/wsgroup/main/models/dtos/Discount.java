package dev.wsgroup.main.models.dtos;

import org.json.JSONObject;

import java.io.Serializable;

public class Discount implements Serializable {

    private String id, supplierId, code, description, starDate, endDate, productId;
    private boolean status;
    private double discountPrice, minPrice;
    private int quantity;

    public Discount() {
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStarDate() {
        return starDate;
    }

    public void setStarDate(String starDate) {
        this.starDate = starDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(double minPrice) {
        this.minPrice = minPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setStatusByString(String status) {
        if(status.equals("ready")) {
            this.status = true;
        } else {
            this.status = false;
        }
    }

    public static Discount getObjectFromJSON(JSONObject data) throws Exception {
        Discount discount = new Discount();
        discount.setId(data.getString("discountCodeId"));
        discount.setSupplierId(data.getString("supplierid"));
        discount.setCode(data.getString("code"));
        discount.setDescription(data.getString("description"));
        discount.setStarDate(data.getString("startdate"));
        discount.setEndDate(data.getString("enddate"));
        discount.setProductId(data.getString("productid"));
        discount.setStatusByString(data.getString("status"));
        discount.setDiscountPrice(data.getDouble("discountprice"));
        discount.setMinPrice(data.getDouble("minimunpricecondition"));
        discount.setQuantity(data.getInt("quantity"));
        return discount;
    }
}
