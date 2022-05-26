package dev.wsgroup.main.models.apis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.models.dtos.Address;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Category;
import dev.wsgroup.main.models.dtos.CustomerDiscount;
import dev.wsgroup.main.models.dtos.LoyaltyStatus;
import dev.wsgroup.main.models.dtos.Message;
import dev.wsgroup.main.models.dtos.Notification;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderHistory;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.dtos.Review;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.dtos.User;

public class APIListener {

    public void onFailedAPICall(int code) {}

    public void onNoJSONFound() {}

    public void onUpdateSuccessful() {}

    public void onUserFound(User user, String message) { }

    public void onAddCartItemSuccessful(CartProduct cartProduct) {}

    public void onCartListFound(ArrayList<CartProduct> retailCartProductList,
                                ArrayList<CartProduct> campaignCartProductList) {}

    public void onProductListFound(List<Product> productList) {}

    public void onCategoryListFound(List<Category> categoryList) {}

    public void onCampaignListFound(List<Campaign> campaignList) {}

    public void onOrderSuccessful(Order order) {}

    public void onOrderFound(List<Order> orderList) {}

    public void onAddressListFound(List<Address> addressList) {}

    public void onAddressAPICompleted(Address address) {}

    public void onSupplierListFound(List<Supplier> supplierList) {}

    public void onReviewAdded(Review review) {}

    public void onReviewListFound(List<Review> reviewList) {}

    public void onRatingListCount(Map<String, Double> ratingMap) {}

    public void onReviewCountFound(int count, double rating) {}

    public void onProductOrderCountFound(Map<String, Integer> countList) {}

    public void onResultStringFound(String result) {}

    public void onLoyaltyStatusListFound(List<LoyaltyStatus> loyaltyStatusList) {}

    public void onDiscountListFound(List<CustomerDiscount> discountList) {}

    public void onMessageListFound(List<Message> messageList) {}

    public void onOrderHistoryFound(List<OrderHistory> historyList) {}

    public void onNotificationListFound(List<Notification> notificationList) {}

    public void onShippingFeeFound(int shippingFee) {}

}
