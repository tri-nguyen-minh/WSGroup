package dev.wsgroup.main.models.dtos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {

    private List<OrderProduct> orderProductList;
    private String id, code, status, dateCreated,
            dateUpdated, paymentId, advanceId, paymentMethod;
    private double discountPrice, shippingFee, totalPrice, advanceFee;
    private int loyaltyDiscountPercent;
    private boolean inCart;
    private Supplier supplier;
    private Address address;
    private Campaign campaign;
    private CustomerDiscount customerDiscount;
    private List<String> imageList;

    public Order() { }

    public List<OrderProduct> getOrderProductList() {
        return orderProductList;
    }

    public void setOrderProductList(List<OrderProduct> orderProductList) {
        this.orderProductList = orderProductList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(double discountPrice) {
        this.discountPrice = discountPrice;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    public CustomerDiscount getCustomerDiscount() {
        return customerDiscount;
    }

    public void setCustomerDiscount(CustomerDiscount customerDiscount) {
        this.customerDiscount = customerDiscount;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(String dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public boolean isInCart() {
        return inCart;
    }

    public void setInCart(boolean inCart) {
        this.inCart = inCart;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getAdvanceId() {
        return advanceId;
    }

    public void setAdvanceId(String advanceId) {
        this.advanceId = advanceId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAdvanceFee() {
        return advanceFee;
    }

    public void setAdvanceFee(double advanceFee) {
        this.advanceFee = advanceFee;
    }

    public int getLoyaltyDiscountPercent() {
        return loyaltyDiscountPercent;
    }

    public void setLoyaltyDiscountPercent(int loyaltyDiscountPercent) {
        this.loyaltyDiscountPercent = loyaltyDiscountPercent;
    }
    public List<String> getImageList() {
        return imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    public static Order getObjectFromJSON(JSONObject jsonObject) throws Exception {
        String stringObject;
        Order order = new Order();
        order.setId(jsonObject.getString("id"));
        try {
            stringObject = jsonObject.getString("customerdiscountcodeid");
            if (!stringObject.equals("null")) {
                CustomerDiscount customerDiscount = new CustomerDiscount();
                customerDiscount.setId(stringObject);
                order.setCustomerDiscount(customerDiscount);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        order.setStatus(jsonObject.getString("status"));
        try {
            stringObject = jsonObject.getString("campaignid");
            Campaign campaign = new Campaign();
            campaign.setId(stringObject);
            order.setCampaign(campaign);
            order.setAdvanceId(jsonObject.getString("advancedid"));
            stringObject = jsonObject.getString("advancefee");
            if (!stringObject.equals("null")) {
                order.setAdvanceFee(Double.parseDouble(stringObject));
            } else {
                order.setAdvanceFee(0);
            }
        } catch (Exception e) {
            order.setAdvanceFee(0);
        }
        Address address = new Address();
        address.setAddressString(jsonObject.getString("address"));
        order.setAddress(address);
        order.setPaymentId(jsonObject.getString("paymentid"));
        order.setDateCreated(jsonObject.getString("createdat"));
        order.setDateUpdated(jsonObject.getString("updatedat"));
        order.setDiscountPrice(jsonObject.getDouble("discountprice"));
        order.setShippingFee(jsonObject.getDouble("shippingfee"));
        order.setCode(jsonObject.getString("ordercode"));
        order.setTotalPrice(jsonObject.getDouble("totalprice"));
        order.setPaymentMethod(jsonObject.getString("paymentmethod"));
        order.setLoyaltyDiscountPercent(0);
        try {
            stringObject = jsonObject.getString("loyalCustomerDiscountPercent");
        } catch (Exception e) {
            stringObject = jsonObject.getString("loyalcustomerdiscountpercent");
        }
        if (!stringObject.equals("null")) {
            order.setLoyaltyDiscountPercent(Integer.parseInt(stringObject));
        }
        JSONArray jsonArray = jsonObject.getJSONArray("details");
        List<OrderProduct> orderProductList = new ArrayList<>();
        OrderProduct orderProduct;
        for (int i = 0; i < jsonArray.length(); i++) {
            orderProduct = OrderProduct.getObjectFromJSON(jsonArray.getJSONObject(i));
            if (order.getCampaign() != null) {
                orderProduct.setCampaign(order.getCampaign());
                orderProduct.setInCampaign(true);
            } else {
                orderProduct.setInCampaign(false);
            }
            orderProductList.add(orderProduct);
        }
        order.setOrderProductList(orderProductList);
        return order;
    }

}
