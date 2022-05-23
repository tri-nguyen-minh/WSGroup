package dev.wsgroup.main.views.fragments.tabs.cart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICampaignCaller;
import dev.wsgroup.main.models.apis.callers.APICartCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewCartSupplierListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.ObjectSerializer;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.order.ConfirmOrderActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class RetailTab extends Fragment {

    private LinearLayout layoutNoShoppingCart, layoutTotalPrice;
    private ConstraintLayout layoutCartDetail, layoutLoading;
    private RecyclerView recViewCartSupplier;
    private TextView txtTotalCartPrice;
    private Button btnCheckout;

    private double totalPrice, orderTotalPrice;
    private SharedPreferences sharedPreferences;
    private RecViewCartSupplierListAdapter adapter;
    private List<Supplier> supplierRetailList;
    private HashMap<String, List<CartProduct>> retailCart;
    private HashMap<String, List<Campaign>> activeCampaignMap, readyCampaignMap;
    private List<CartProduct> cartList, tempCartList;
    private List<Campaign> campaignList, tempCampaignList;
    private DialogBoxLoading dialogBoxLoading;
    private int activeCampaignCount, readyCampaignCount;
    private Supplier supplier;
    private CartProduct cartProduct;
    private Product product;

    public RetailTab() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart_retail_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutNoShoppingCart = view.findViewById(R.id.layoutNoShoppingCart);
        layoutTotalPrice = view.findViewById(R.id.layoutTotalPrice);
        layoutCartDetail = view.findViewById(R.id.layoutCartDetail);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        recViewCartSupplier = view.findViewById(R.id.recViewCartSupplier);
        txtTotalCartPrice = view.findViewById(R.id.txtTotalCartPrice);
        btnCheckout = view.findViewById(R.id.btnCheckout);

        setupCartView();
        setupTotalPrice();
        setupCheckoutButton();

        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<Order> ordersList = new ArrayList<>();
                Order order;
                OrderProduct orderProduct;
                for (Supplier supplier : supplierRetailList) {
                    orderTotalPrice = 0;
                    List<OrderProduct> selectedProductList = new ArrayList<>();
                    tempCartList = retailCart.get(supplier.getId());
                    for (CartProduct cartProduct : tempCartList) {
                        if (cartProduct.getSelectedFlag()) {
                            orderProduct = new OrderProduct();
                            orderProduct.setProduct(cartProduct.getProduct());
                            orderProduct.setQuantity(cartProduct.getQuantity());
                            orderProduct.setPrice(cartProduct.getProduct().getRetailPrice());
                            totalPrice = cartProduct.getQuantity() * cartProduct.getProduct().getRetailPrice();
                            orderTotalPrice += totalPrice;
                            orderProduct.setTotalPrice(totalPrice);
                            orderProduct.setCartProduct(cartProduct);
                            orderProduct.setCampaign(null);
                            selectedProductList.add(orderProduct);
                        }
                    }
                    if (selectedProductList.size() > 0) {
                        order = new Order();
                        order.setTotalPrice(orderTotalPrice);
                        order.setCampaign(null);
                        order.setSupplier(supplier);
                        order.setInCart(true);
                        order.setOrderProductList(selectedProductList);
                        order.setLoyaltyDiscountPercent(0);
                        ordersList.add(order);
                    }
                }
                DialogBoxConfirm dialogBoxConfirm = new DialogBoxConfirm(getActivity(),
                                                        StringUtils.MES_CONFIRM_CHECKOUT) {
                    @Override
                    public void onYesClicked() {
                        Intent confirmOrderIntent = new Intent(getContext(), ConfirmOrderActivity.class);
                        confirmOrderIntent.putExtra("ORDER_LIST", ordersList);
                        confirmOrderIntent.putExtra("REQUEST_CODE", IntegerUtils.REQUEST_ORDER_RETAIL);
                        confirmOrderIntent.putExtra("CART_STATUS", true);
                        startActivityForResult(confirmOrderIntent, IntegerUtils.REQUEST_COMMON);
                    }
                };
                dialogBoxConfirm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxConfirm.show();
            }
        });
    }

    private void getCartFromSession() {
        try {
            sharedPreferences = getActivity().getSharedPreferences("PREFERENCE",
                                                                    Context.MODE_PRIVATE);
            cartList = (List<CartProduct>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("RETAIL_CART", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupCartView() {
        getCartFromSession();
        if (cartList.size() > 0) {
            layoutNoShoppingCart.setVisibility(View.INVISIBLE);
            layoutLoading.setVisibility(View.VISIBLE);
            layoutCartDetail.setVisibility(View.VISIBLE);
            findCampaign();
        } else {
            layoutNoShoppingCart.setVisibility(View.VISIBLE);
            layoutLoading.setVisibility(View.INVISIBLE);
            layoutCartDetail.setVisibility(View.INVISIBLE);
        }
    }
    private void findCampaign() {
        activeCampaignMap = new HashMap<>();
        readyCampaignMap = new HashMap<>();
        activeCampaignCount = cartList.size();
        readyCampaignCount = cartList.size();
        for (CartProduct cartProduct : cartList) {
            APICampaignCaller.getCampaignListByProductId(cartProduct.getProduct().getProductId(),
                    "active", getActivity().getApplication(), new APIListener() {
                @Override
                public void onCampaignListFound(List<Campaign> campaignList) {
                    activeCampaignCount--;
                    activeCampaignMap.put(cartProduct.getId(), campaignList);
                    if (activeCampaignCount == 0 && readyCampaignCount == 0) {
                        addCampaignToCart();
                        setupAdapter();
                    }
                }
                @Override
                public void onNoJSONFound() {
                    activeCampaignCount--;
                    if (activeCampaignCount == 0 && readyCampaignCount == 0) {
                        addCampaignToCart();
                        setupAdapter();
                    }
                }
            });
            APICampaignCaller.getCampaignListByProductId(cartProduct.getProduct().getProductId(),
                    "ready", getActivity().getApplication(), new APIListener() {
                @Override
                public void onCampaignListFound(List<Campaign> campaignList) {
                    readyCampaignCount--;
                    readyCampaignMap.put(cartProduct.getId(), campaignList);
                    if (activeCampaignCount == 0 && readyCampaignCount == 0) {
                        addCampaignToCart();
                        setupAdapter();
                    }
                }
                @Override
                public void onNoJSONFound() {
                    readyCampaignCount--;
                    if (activeCampaignCount == 0 && readyCampaignCount == 0) {
                        addCampaignToCart();
                        setupAdapter();
                    }
                }
            });
        }
    }

    private void setupAdapter() {
        layoutLoading.setVisibility(View.INVISIBLE);
        adaptList();
        adapter = new RecViewCartSupplierListAdapter(getContext(), getActivity(),
                IntegerUtils.IDENTIFIER_RETAIL_CART) {

            @Override
            public void onRemove(String cartProductId) {
                dialogBoxLoading = new DialogBoxLoading(getActivity());
                dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                String token = sharedPreferences.getString("TOKEN", "" );
                APICartCaller.deleteCartItem(token, cartProductId,
                        getActivity().getApplication(), new APIListener() {
                    @Override
                    public void onUpdateSuccessful() {
                        if (dialogBoxLoading.isShowing()) {
                            dialogBoxLoading.dismiss();
                        }
                        layoutCartDetail.setVisibility(View.INVISIBLE);
                        layoutLoading.setVisibility(View.VISIBLE);
                        int index = findCartProductIndexById(cartProductId);
                        if (index >= 0) {
                            cartList.remove(index);
                        }
                        try {
                            sharedPreferences.edit()
                                    .putString("RETAIL_CART",
                                            ObjectSerializer.serialize((Serializable) cartList))
                                    .apply();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (dialogBoxLoading.isShowing()) {
                            dialogBoxLoading.dismiss();
                        }
                        if (cartList.size() > 0) {
                            adaptList();
                            setupTotalPrice();
                            setupCheckoutButton();
                            finishRecView();
                            layoutCartDetail.setVisibility(View.VISIBLE);
                            layoutLoading.setVisibility(View.INVISIBLE);
                        } else {
                            layoutNoShoppingCart.setVisibility(View.VISIBLE);
                            layoutLoading.setVisibility(View.INVISIBLE);
                            layoutCartDetail.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        if (dialogBoxLoading.isShowing()) {
                            dialogBoxLoading.dismiss();
                        }
                        if (code == IntegerUtils.ERROR_NO_USER) {
                            MethodUtils.displayErrorAccountMessage(getContext(),
                                    getActivity());
                        } else {
                            MethodUtils.displayErrorAPIMessage(getActivity());
                        }
                    }
                });
            }

            @Override
            public void onCheckboxChanged(String cartProductId, boolean newStatus) {
                int index = findCartProductIndexById(cartProductId);
                CartProduct cartProduct = null;
                if (index >= 0) {
                    cartProduct = cartList.get(index);
                    cartProduct.setSelectedFlag(newStatus);
                    cartList.set(index, cartProduct);
                }
                adaptList();
                setupTotalPrice();
                setupCheckoutButton();
                notifyDataSetChanged();
            }

            @Override
            public void onPriceChanging() {
                setupTotalPrice();
                btnCheckout.setEnabled(false);
                btnCheckout.getBackground().setTint(getResources().getColor(R.color.gray_light));
            }

            @Override
            public void onPriceChanged() {
                btnCheckout.setEnabled(true);
                btnCheckout.getBackground().setTint(getResources().getColor(R.color.blue_main));
            }
        };
        finishRecView();
    }

    private void finishRecView() {
        adapter.setCartList(supplierRetailList, retailCart);
        recViewCartSupplier.setAdapter(adapter);
        recViewCartSupplier.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));
    }

    private void adaptList() {
        supplierRetailList = new ArrayList<>();
        retailCart = new HashMap<>();
        for (CartProduct cartProduct : cartList) {
            supplier = cartProduct.getProduct().getSupplier();
            tempCartList = retailCart.get(supplier.getId());
            if(tempCartList == null) {
                supplierRetailList.add(supplier);
                tempCartList = new ArrayList<>();
            }
            tempCartList.add(cartProduct);
            retailCart.put(supplier.getId(), tempCartList);
        }
    }

    private void addCampaignToCart() {
        if (activeCampaignMap.size() > 0 || readyCampaignMap.size() > 0) {
            for (int i = 0; i < cartList.size(); i++) {
                cartProduct = cartList.get(i);
                campaignList = activeCampaignMap.get(cartList.get(i).getId());
                if (campaignList == null) {
                    campaignList = new ArrayList<>();
                }
                tempCampaignList = readyCampaignMap.get(cartList.get(i).getId());
                if (tempCampaignList != null && tempCampaignList.size() > 0) {
                    for (Campaign campaign : tempCampaignList) {
                        campaignList.add(campaign);
                    }
                }
                product = cartProduct.getProduct();
                product.setCampaignList(campaignList);
                cartProduct.setProduct(product);
                cartList.set(i, cartProduct);
            }
        }
    }

    private int findCartProductIndexById(String id) {
        for (int i = 0; i < cartList.size(); i++) {
            if (cartList.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private void setupTotalPrice() {
        getTotalPrice();
        if (totalPrice == 0) {
            layoutTotalPrice.setVisibility(View.INVISIBLE);
        } else {
            layoutTotalPrice.setVisibility(View.VISIBLE);
            txtTotalCartPrice.setText(MethodUtils.formatPriceString(totalPrice));
        }
    }

    private double getTotalPrice() {
        totalPrice = 0;
        for (CartProduct cartProduct : cartList) {
            if (cartProduct.getSelectedFlag()) {
                totalPrice += (cartProduct.getQuantity() * cartProduct.getProduct().getRetailPrice());
            }
        }
        return totalPrice;
    }

    private void setupCheckoutButton() {
        if (checkSelectedCartProduct()) {
            btnCheckout.setEnabled(true);
            btnCheckout.getBackground().setTint(getResources().getColor(R.color.blue_main));
        } else {
            btnCheckout.setEnabled(false);
            btnCheckout.getBackground().setTint(getResources().getColor(R.color.gray_light));
        }
    }

    private boolean checkSelectedCartProduct() {
        for (CartProduct cartProduct : cartList) {
            if (cartProduct.getSelectedFlag()) {
                return true;
            }
        }
        return false;
    }
}