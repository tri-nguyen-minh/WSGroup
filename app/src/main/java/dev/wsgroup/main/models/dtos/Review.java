package dev.wsgroup.main.models.dtos;

import org.json.JSONObject;

import java.io.Serializable;

import dev.wsgroup.main.models.utils.StringUtils;

public class Review implements Serializable {
    private String orderId, productId, review, date;
    private double rating;
    private User user;
    private boolean isRemoved;

    public Review() {
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

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setRemoved(boolean removed) {
        isRemoved = removed;
    }

    public static Review getObjectFromJSON(JSONObject data) throws Exception {
        Review review;
        if (data.getString("rating").equals("null") || data.getDouble("rating") == 0) {
            return null;
        } else {
            review = new Review();
            review.setRemoved(false);
            if (data.getString("comment").equals("removed")) {
                review.setReview(StringUtils.MES_ALERT_REVIEW_REMOVED);
                review.setRating(0);
                review.setRemoved(true);
            } else {
                review.setReview(data.getString("comment"));
                review.setRating(data.getDouble("rating"));
            }
            review.setDate(data.getString("updatedat"));
            User user = new User();
            user.setAvatarLink(data.getString("avt"));
            user.setFirstName(data.getString("firstName"));
            user.setLastName(data.getString("lastName"));
            review.setUser(user);
        }
        return review;
    }
}
