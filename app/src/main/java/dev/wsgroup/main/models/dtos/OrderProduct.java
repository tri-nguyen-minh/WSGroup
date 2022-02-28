package dev.wsgroup.main.models.dtos;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderProduct implements Serializable {
    private String id, productType, note, orderCode;
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

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
        List<String> productTypeList = new ArrayList<>();
        if (!productType.isEmpty()) {
            productTypeList.add(productType);
        }
        productTypeList.add("test");
        this.typeList = productTypeList;
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

    public boolean getInCampaign() {
        return inCampaign;
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

    public static OrderProduct getOrderProductFromJSON(JSONObject jsonObject) throws Exception {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(jsonObject.getString("id"));
        orderProduct.setNote(jsonObject.getString("notes"));
        orderProduct.setPrice(jsonObject.getDouble("price"));
        orderProduct.setTotalPrice(jsonObject.getDouble("totalprice"));
        orderProduct.setQuantity(jsonObject.getInt("quantity"));
        orderProduct.setOrderCode(jsonObject.getString("ordercode"));
        Product product = new Product();
        product.setProductId(jsonObject.getString("productid"));
        product.setImageLink(jsonObject.getString("image"));
        product.setName(jsonObject.getString("productname"));
        orderProduct.setProduct(product);
        orderProduct.setInCampaign(jsonObject.getBoolean("incampaign"));
        String stringObject = jsonObject.getString("campaignid");
        if (!stringObject.equals("null")) {
            Campaign campaign = new Campaign();
            campaign.setId(stringObject);
            orderProduct.setCampaign(campaign);
        }
        return orderProduct;
    }
}
