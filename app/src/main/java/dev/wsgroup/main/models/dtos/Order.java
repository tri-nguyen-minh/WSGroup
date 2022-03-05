package dev.wsgroup.main.models.dtos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {

    private List<OrderProduct> orderProductList;
    private String id, code, status, dateCreated, dateUpdated,
            updateReason, paymentId, advanceId, paymentMethod;
    private double discountPrice, shippingFee, totalPrice, advanceFee;
    private Supplier supplier;
    private Address address;
    private Campaign campaign;
    private CustomerDiscount customerDiscount;
    private Payment payment;
    private boolean inCart;

    public Order() {
    }

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

    public String getUpdateReason() {
        return updateReason;
    }

    public void setUpdateReason(String updateReason) {
        this.updateReason = updateReason;
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

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
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

    public static Order getOrderFromJSON(JSONObject jsonObject) throws Exception {
        Order order = new Order();
        order.setId(jsonObject.getString("id"));

        String stringObject = jsonObject.getString("customerdiscountcodeid");
        if (!stringObject.equals("null")) {
            CustomerDiscount customerDiscount = new CustomerDiscount();
            customerDiscount.setId(stringObject);
            order.setCustomerDiscount(customerDiscount);
        }
        order.setStatus(jsonObject.getString("status"));
        stringObject = jsonObject.getString("campaignid");
        if (!stringObject.equals("null")) {
            Campaign campaign = new Campaign();
            campaign.setId(stringObject);
            order.setCampaign(campaign);
        }
        Address address = new Address();
        address.setId(jsonObject.getString("addressid"));
        address.setAddressString(jsonObject.getString("address"));
        order.setAddress(address);

        stringObject = jsonObject.getString("paymentid");
        if (!stringObject.equals("null")) {
            Payment payment = new Payment();
            payment.setId(stringObject);
            order.setPayment(payment);
        }
        order.setDateCreated(jsonObject.getString("createdat"));
        order.setDateUpdated(jsonObject.getString("updatedat"));
        order.setDiscountPrice(jsonObject.getDouble("discountprice"));
        order.setShippingFee(jsonObject.getDouble("shippingfee"));
        order.setCode(jsonObject.getString("ordercode"));
        order.setTotalPrice(jsonObject.getDouble("totalprice"));
        Supplier supplier = new Supplier();
        supplier.setId(jsonObject.getString("supplierid"));
        supplier.setName(jsonObject.getString("suppliername"));
        order.setSupplier(supplier);
        order.setUpdateReason(jsonObject.getString("reasonforupdatestatus"));
        JSONArray jsonArray = jsonObject.getJSONArray("details");
        List<OrderProduct> orderProductList = new ArrayList<>();
        OrderProduct orderProduct;
        for (int i = 0; i < jsonArray.length(); i++) {
            orderProduct = OrderProduct.getOrderProductFromJSON(jsonArray.getJSONObject(i));
            orderProductList.add(orderProduct);
        }
        order.setOrderProductList(orderProductList);
        return order;
    }

}
