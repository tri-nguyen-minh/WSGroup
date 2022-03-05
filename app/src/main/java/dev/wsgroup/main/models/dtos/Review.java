package dev.wsgroup.main.models.dtos;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class Review implements Serializable {
    private String id, orderId, productId, description;
    private double rating;
    private User user;
    private String createDate, updateDate;

    public Review() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public static Review getReviewFromJSON(JSONObject jsonObject) throws Exception {
        Review review = new Review();
        review.setId(jsonObject.getString("id"));
        review.setProductId(jsonObject.getString("productid"));
        review.setOrderId(jsonObject.getString("oderdetailid"));
        review.setCreateDate(jsonObject.getString("createdat"));
        review.setUpdateDate(jsonObject.getString("updatedat"));
        review.setRating(jsonObject.getDouble("rating"));
        review.setDescription(jsonObject.getString("comment"));
        User user = new User();
        user.setUserId(jsonObject.getString("customerid"));
        user.setAvatarLink(jsonObject.getString("avt"));
        user.setFirstName(jsonObject.getString("firstname"));
        user.setLastName(jsonObject.getString("lastname"));
        review.setUser(user);
        return review;
    }
}
