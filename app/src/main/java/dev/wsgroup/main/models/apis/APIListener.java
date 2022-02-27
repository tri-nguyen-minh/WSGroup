package dev.wsgroup.main.models.apis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.models.dtos.Address;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Category;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.dtos.User;

public class APIListener {

    public void onFailedAPICall(int code) {}

    public void onUserFound(User user) { }

    public void onCompletingRegistrationRequest() {}

    public void onUpdateProfileSuccessful() {}

    public void onUpdateCartItemSuccessful() {}

    public void onAddCartItemSuccessful(CartProduct cartProduct) {}

    public void onProductListFound(List<Product> productList) {}

    public void onCartListFound(HashMap<String, List<CartProduct>> retailCart, List<Supplier> supplierRetailList,
                                HashMap<String, List<CartProduct>> campaignCart, List<Supplier> supplierCampaignList) {}

    public void onProductFound(Product product) {}

    public void onCategoryFound(Category category) {}

    public void onCampaignFound(Campaign campaign) {}

    public void onCampaignListFound(List<Campaign> campaignList) {}

    public void onCampaignMapFound(Map<String, List<Campaign>> campaignMap) {}

    public void onNoCampaignFound() {}

    public void onOrderSuccessful(Order order) {}

    public void onOrderFound(List<Order> orderList) {}

    public void onAddressListFound(List<Address> addressList) {}

    public void onAddressFound(Address address) {}

    public void onUpdateAddressSuccessful(Address address) {}

    public void onSupplierFound(Supplier supplier) {}

}
