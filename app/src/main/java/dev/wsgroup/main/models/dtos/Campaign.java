package dev.wsgroup.main.models.dtos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Campaign implements Serializable {
    private String id, supplierId, startDate, endDate, code, status, description;
    private int minQuantity, maxQuantity, orderCount, quantityCount, advancePercentage;
    private double price;
    private boolean shareFlag;
    private Product product;
    private List<CampaignMilestone> milestoneList;

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

    public int getAdvancePercentage() {
        return advancePercentage;
    }

    public void setAdvancePercentage(int advancePercentage) {
        this.advancePercentage = advancePercentage;
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

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public List<CampaignMilestone> getMilestoneList() {
        return milestoneList;
    }

    public void setMilestoneList(List<CampaignMilestone> milestoneList) {
        this.milestoneList = milestoneList;
    }

    public static Campaign getObjectFromJSON(JSONObject data) throws Exception {
        Campaign campaign = new Campaign();
        campaign.setId(data.getString("id"));
        Product product = new Product();
        product.setProductId(data.getString("productid"));
        campaign.setProduct(product);
        campaign.setCode(data.getString("code"));
        campaign.setStartDate(data.getString("fromdate"));
        campaign.setEndDate(data.getString("todate"));
        campaign.setMinQuantity(data.getInt("quantity"));
        campaign.setMaxQuantity(data.getInt("maxquantity"));
        campaign.setPrice(data.getDouble("price"));
        campaign.setStatus(data.getString("status"));
        campaign.setDescription(data.getString("description"));
        campaign.setQuantityCount(data.getInt("quantityorderwaiting"));
        campaign.setOrderCount(data.getInt("numorderwaiting"));
        campaign.setShareFlag(data.getBoolean("isshare"));
        campaign.setAdvancePercentage(data.getInt("advancefee"));
        if (campaign.getShareFlag()) {
            List<CampaignMilestone> milestoneList = new ArrayList<>();
            JSONArray rangeArray = new JSONArray(data.getString("range"));
            if (rangeArray.length() > 0) {
                CampaignMilestone milestone;
                for (int i = 0; i < rangeArray.length(); i++) {
                    milestone = CampaignMilestone.getObjectFromJSON(rangeArray.getJSONObject(i));
                    milestoneList.add(milestone);
                }
                Collections.sort(milestoneList, new Comparator<CampaignMilestone>() {
                    @Override
                    public int compare(CampaignMilestone milestone1, CampaignMilestone milestone2) {
                        return milestone1.getQuantity() - milestone2.getQuantity();
                    }
                });
            }
            campaign.setMilestoneList(milestoneList);
        }
        return campaign;
    }

    public static Campaign getSearchedObjectFromJSON(JSONObject data) throws Exception {
        Campaign campaign = new Campaign();
        campaign.setId(data.getString("id"));
        Product product = new Product();
        product.setProductId(data.getString("productid"));
        product.setName(data.getString("productname"));
        product.setRetailPrice(data.getDouble("productretailprice"));
        product.setImageLink(data.getString("productimage"));
        Supplier supplier = new Supplier();
        supplier.setId(data.getString("supplierId"));
        supplier.setId(data.getString("name"));
        product.setSupplier(supplier);
        campaign.setProduct(product);
        campaign.setCode(data.getString("code"));
        campaign.setStartDate(data.getString("fromdate"));
        campaign.setEndDate(data.getString("todate"));
        campaign.setMinQuantity(data.getInt("quantity"));
        campaign.setMaxQuantity(data.getInt("maxquantity"));
        campaign.setPrice(data.getDouble("price"));
        campaign.setStatus(data.getString("status"));
        campaign.setDescription(data.getString("description"));
        campaign.setQuantityCount(data.getInt("quantityorderwaiting"));
        campaign.setOrderCount(data.getInt("numorderwaiting"));
        campaign.setShareFlag(data.getBoolean("isshare"));
        campaign.setAdvancePercentage(data.getInt("advancefee"));
        if (campaign.getShareFlag()) {
            List<CampaignMilestone> milestoneList = new ArrayList<>();
            JSONArray rangeArray = new JSONArray(data.getString("range"));
            if (rangeArray.length() > 0) {
                CampaignMilestone milestone;
                for (int i = 0; i < rangeArray.length(); i++) {
                    milestone = CampaignMilestone.getObjectFromJSON(rangeArray.getJSONObject(i));
                    milestoneList.add(milestone);
                }
                Collections.sort(milestoneList, new Comparator<CampaignMilestone>() {
                    @Override
                    public int compare(CampaignMilestone milestone1, CampaignMilestone milestone2) {
                        return milestone1.getQuantity() - milestone2.getQuantity();
                    }
                });
            }
            campaign.setMilestoneList(milestoneList);
        }
        return campaign;
    }
}
