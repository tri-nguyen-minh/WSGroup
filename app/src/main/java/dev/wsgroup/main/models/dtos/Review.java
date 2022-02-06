package dev.wsgroup.main.models.dtos;

import java.io.Serializable;
import java.util.List;

public class Review implements Serializable {
    private String reviewId, userid, productId, description;
    private List<String> imageList;
    private boolean deletedStatus;

    public Review(String reviewId, String userid, String productId, String description, List<String> imageList, boolean deletedStatus) {
        this.reviewId = reviewId;
        this.userid = userid;
        this.productId = productId;
        this.description = description;
        this.imageList = imageList;
        this.deletedStatus = deletedStatus;
    }

    public Review() {
    }

    public String getReviewId() {
        return reviewId;
    }

    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
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

    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public boolean isDeletedStatus() {
        return deletedStatus;
    }

    public void setDeletedStatus(boolean deletedStatus) {
        this.deletedStatus = deletedStatus;
    }
}
