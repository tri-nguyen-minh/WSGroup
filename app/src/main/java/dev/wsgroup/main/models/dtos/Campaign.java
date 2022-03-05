package dev.wsgroup.main.models.dtos;

import org.json.JSONObject;

import java.io.Serializable;

public class Campaign implements Serializable {
    private String id, supplierId, productId, startDate, endDate, code, status, description;
    private int minQuantity, maxQuantity, orderCount, quantityCount;
    private double savingPrice;
    private boolean shareFlag;

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

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public int getOrderCount() {
        return orderCount;
    }

    public double getSavingPrice() {
        return savingPrice;
    }

    public void setSavingPrice(double savingPrice) {
        this.savingPrice = savingPrice;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getShareFlag() {
        return shareFlag;
    }

    public void setShareFlag(boolean shareFlag) {
        this.shareFlag = shareFlag;
    }

    public static Campaign getObjectFromJSON(JSONObject data) throws Exception {
        Campaign campaign = new Campaign();
        campaign.setId(data.getString("id"));
        campaign.setProductId(data.getString("productid"));
        campaign.setSupplierId(data.getString("supplierid"));
        campaign.setCode(data.getString("code"));
        campaign.setStartDate(data.getString("fromdate"));
        campaign.setEndDate(data.getString("todate"));
        campaign.setMinQuantity(data.getInt("quantity"));
        campaign.setMaxQuantity(data.getInt("maxquantity"));
        campaign.setSavingPrice(data.getDouble("price"));
        campaign.setStatus(data.getString("status"));
        campaign.setDescription(data.getString("description"));
        campaign.setQuantityCount(data.getInt("quantityorderwaiting"));
        campaign.setOrderCount(data.getInt("numorderwaiting"));
        campaign.setShareFlag(data.getBoolean("isshare"));
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
