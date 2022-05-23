package dev.wsgroup.main.models.dtos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Product implements Serializable {

    private String productId, name, description, categoryId, status, imageLink;
    private Supplier supplier;
    private double retailPrice, weight, rating;
    private int quantity, orderCount, reviewCount;
    private List<String> imageList;
    private Campaign campaign;
    private List<Campaign> campaignList;
    private List<Review> reviewList;

    public Product() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
        List<String> imageList = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(imageLink);
            if(jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    imageList.add(jsonArray.getJSONObject(i).getString("url"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            imageList = new ArrayList<>();
        }
        setImageList(imageList);
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
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

    public void setOrderCount(int orderCount) {
        this.orderCount = orderCount;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public List<Campaign> getCampaignList() {
        return campaignList;
    }

    public void setCampaignList(List<Campaign> campaignList) {
        this.campaignList = campaignList;
    }

    public List<Review> getReviewList() {
        return reviewList;
    }

    public void setReviewList(List<Review> reviewList) {
        this.reviewList = reviewList;
    }

    public static Product getObjectFromJSON(JSONObject jsonObject, boolean searchForm)
                                                                            throws Exception {
        Product product = new Product();
        product.setProductId(jsonObject.getString("id"));
        product.setName(jsonObject.getString("name"));
        product.setCategoryId(jsonObject.getString("categoryid"));
        product.setDescription(jsonObject.getString("description"));
        product.setQuantity(jsonObject.getInt("quantity"));
        product.setRetailPrice(jsonObject.getDouble("retailprice"));
        product.setStatus(jsonObject.getString("status"));
        Supplier supplier = new Supplier();
        supplier.setId(jsonObject.getString("supplierid"));
        if (!searchForm) {
            supplier.setName(jsonObject.getString("suppliername"));
            supplier.setAddressString(jsonObject.getString("supplieraddress"));
        }
        product.setSupplier(supplier);
        product.setImageLink(jsonObject.getString("image"));
        product.setWeight(jsonObject.getDouble("weight"));
        product.setOrderCount(0);
        product.setReviewCount(0);
        product.setRating(0);
        return product;
    }
}
