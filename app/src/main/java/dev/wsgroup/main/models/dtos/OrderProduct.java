package dev.wsgroup.main.models.dtos;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderProduct implements Serializable {
    private String id, note, orderCode;
    private int quantity;
    private double price, totalPrice;
    private Product product;
    private Campaign campaign;
    private CartProduct cartProduct;
    private List<String> typeList;
    private boolean inCampaign;
    private Review review;

    public OrderProduct() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public CartProduct getCartProduct() {
        return cartProduct;
    }

    public void setCartProduct(CartProduct cartProduct) {
        this.cartProduct = cartProduct;
    }

    public List<String> getTypeList() {
        return typeList;
    }

    public void setTypeList(List<String> typeList) {
        this.typeList = typeList;
    }

    public void setInCampaign(boolean inCampaign) {
        this.inCampaign = inCampaign;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public static OrderProduct getObjectFromJSON(JSONObject jsonObject) throws Exception {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(jsonObject.getString("id"));
        try {
            orderProduct.setNote(jsonObject.getString("notes"));
        } catch (Exception e) {
            orderProduct.setNote(jsonObject.getString("note"));
        }
        orderProduct.setPrice(jsonObject.getDouble("price"));
        orderProduct.setTotalPrice(jsonObject.getDouble("totalPrice"));
        orderProduct.setQuantity(jsonObject.getInt("quantity"));
        orderProduct.setOrderCode(jsonObject.getString("orderCode"));
        Product product = new Product();
        product.setProductId(jsonObject.getString("productId"));
        product.setImageLink(jsonObject.getString("image"));
        product.setName(jsonObject.getString("productName"));
        orderProduct.setProduct(product);
        return orderProduct;
    }
}
