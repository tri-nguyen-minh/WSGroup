package dev.wsgroup.main.models.dtos;

import org.json.JSONObject;

import java.io.Serializable;

public class Category implements Serializable {
    private String categoryId, supplierId, name;
    private boolean isDeleted;

    public Category() { }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public static Category getCategoryFromJSON(JSONObject jsonObject) throws Exception {
        Category category = new Category();
        category.setCategoryId(jsonObject.getString("id"));
        category.setSupplierId(jsonObject.getString("supplierid"));
        category.setName(jsonObject.getString("categoryname"));
        category.setDeleted(jsonObject.getBoolean("isdeleted"));
        return category;
    }
}
