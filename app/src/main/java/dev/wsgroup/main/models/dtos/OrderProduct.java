package dev.wsgroup.main.models.dtos;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OrderProduct implements Serializable {
    private String id, productId, productType, note,
            campaignId, addressId, paymentId, status;
    private int quantity;
    private double price, totalPrice;
    private Product product;
    private CartProduct cartProduct;
    private List<String> typeList;

    public OrderProduct() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public static OrderProduct getOrderProductFromJSON(JSONObject jsonObject) throws Exception {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setId(jsonObject.getString("id"));
        orderProduct.setNote(jsonObject.getString("notes"));
        orderProduct.setPrice(jsonObject.getDouble("price"));
        orderProduct.setTotalPrice(jsonObject.getDouble("totalprice"));
        orderProduct.setQuantity(jsonObject.getInt("quantity"));
        Product product = new Product();
        product.setImageLink(jsonObject.getString("image"));
        product.setProductId(jsonObject.getString("productid"));
        product.setName(jsonObject.getString("productname"));
        orderProduct.setProduct(product);
        String jsonString = jsonObject.getString("typeofproduct");
        orderProduct.setProductType((jsonString != null) ? jsonString : "");
        return orderProduct;
    }
}
