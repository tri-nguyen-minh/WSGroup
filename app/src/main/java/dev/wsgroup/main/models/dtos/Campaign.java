package dev.wsgroup.main.models.dtos;

import org.json.JSONObject;

import java.io.Serializable;

public class Campaign implements Serializable {
    private String id, supplierId, productId, startDate, endDate, code, status;
    private int quantity, orderCount, quantityCount;
    private double price;

    public Campaign() {
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public int getQuantityCount() {
        return quantityCount;
    }

    public void setQuantityCount(int quantityCount) {
        this.quantityCount = quantityCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static Campaign getCampaignFromJSON(JSONObject data) throws Exception {
        Campaign campaign = new Campaign();
        campaign.setId(data.getString("id"));
        campaign.setProductId(data.getString("productid"));
        campaign.setSupplierId(data.getString("supplierid"));
        campaign.setCode(data.getString("code"));
        campaign.setStartDate(data.getString("fromdate"));
        campaign.setEndDate(data.getString("todate"));
        campaign.setQuantity(data.getInt("quantity"));
        campaign.setPrice(data.getDouble("price"));
        campaign.setQuantityCount(data.getInt("quantityorderwaiting"));
        campaign.setOrderCount(data.getInt("numorderwaiting"));
        campaign.setStatus(data.getString("status"));
        return campaign;
    }

    @Override
    public String toString() {
        return "Campaign{" +
                "startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", code='" + code + '\'' +
                ", orderCount=" + orderCount +
                ", quantityCount=" + quantityCount +
                '}';
    }
}
