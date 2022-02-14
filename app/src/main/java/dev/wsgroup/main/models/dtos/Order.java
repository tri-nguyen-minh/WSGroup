package dev.wsgroup.main.models.dtos;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {

    private List<OrderProduct> orderProductList;
    private String id, campaignId, customerId, code, status;
    private double discountPrice, shippingFee, totalPrice;
    private Supplier supplier;
    private Address address;
    private CustomerDiscount customerDiscount;
    private Payment payment;
    private String dateCreated, dateUpdated;

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

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
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

    public static Order getOrderFromJSON(JSONObject jsonObject) throws Exception {
        Order order = new Order();
        order.setId(jsonObject.getString("id"));

        String stringObject = jsonObject.getString("customerdiscountcodeid");
        CustomerDiscount customerDiscount = new CustomerDiscount();
        customerDiscount.setId((!stringObject.equals("null")) ? stringObject : "");
        order.setCustomerDiscount(customerDiscount);

        order.setStatus(jsonObject.getString("status"));
        stringObject = jsonObject.getString("campaignid");
        order.setCampaignId((!stringObject.equals("null")) ? stringObject : "");
        stringObject = jsonObject.getString("addressid");
        Address address = new Address();
        address.setId((!stringObject.equals("null")) ? stringObject : "");
        address.setAddressString(jsonObject.getString("address"));
        order.setAddress(address);

        stringObject = jsonObject.getString("paymentid");
        Payment payment = new Payment();
        payment.setId((!stringObject.equals("null")) ? stringObject : "");
        order.setPayment(payment);
        order.setDiscountPrice(jsonObject.getDouble("discountprice"));
        order.setShippingFee(jsonObject.getDouble("shippingfee"));
        order.setTotalPrice(jsonObject.getDouble("totalprice"));
        order.setDateCreated(jsonObject.getString("createdat"));
        order.setDateUpdated(jsonObject.getString("updatedat"));

        order.setCode(jsonObject.getString("ordercode"));
        Supplier supplier = new Supplier();
        supplier.setId(jsonObject.getString("supplierid"));
        supplier.setName(jsonObject.getString("suppliername"));
        order.setSupplier(supplier);

        List<OrderProduct> orderProductList = new ArrayList<>();
        OrderProduct orderProduct;
        JSONArray jsonArray = jsonObject.getJSONArray("details");
        for (int i = 0; i < jsonArray.length(); i++) {
            orderProduct = OrderProduct.getOrderProductFromJSON(jsonArray.getJSONObject(i));
            orderProductList.add(orderProduct);
        }
        order.setOrderProductList(orderProductList);
        return order;
    }

}
