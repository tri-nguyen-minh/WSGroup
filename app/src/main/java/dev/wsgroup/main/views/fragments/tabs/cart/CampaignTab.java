package dev.wsgroup.main.views.fragments.tabs.cart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewCartSupplierListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.order.ConfirmOrderActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class CampaignTab extends Fragment {

    private LinearLayout layoutNoShoppingCart;
    private ConstraintLayout layoutCartDetail, layoutLoading;
    private RecyclerView recViewCartSupplier;

    private double totalPrice;
    private SharedPreferences sharedPreferences;
    private RecViewCartSupplierListAdapter adapter;
    private List<Supplier> supplierCampaignList;
    private HashMap<String, ArrayList<CartProduct>> campaignCart;
    private ArrayList<CartProduct> cartList, tempCartList;
    private List<Campaign> campaignList;
    private DialogBoxLoading dialogBoxLoading;
    private int campaignCount;
    private Supplier supplier;
    private CartProduct cartProduct;

    public CampaignTab() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart_campaign_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutNoShoppingCart = view.findViewById(R.id.layoutNoShoppingCart);
        layoutCartDetail = view.findViewById(R.id.layoutCartDetail);
        layoutLoading = view.findViewById(R.id.layoutLoading);
        recViewCartSupplier = view.findViewById(R.id.recViewCartSupplier);

        sharedPreferences = getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);

        setupCartView();
    }

    private void setupCartView() {
        setLoadingState();
        cartList = (ArrayList<CartProduct>)
                getActivity().getIntent().getSerializableExtra("CAMPAIGN_CART");
        if (cartList != null && cartList.size() > 0) {
            findCampaign();
        } else {
            setEmptyState();
        }
    }
    
    private void findCampaign() {
        campaignList = new ArrayList<>();
        campaignCount = cartList.size();
        for (CartProduct cartProduct : cartList) {
            APICampaignCaller.getCampaignById(cartProduct.getCampaign().getId(),
                    getActivity().getApplication(), new APIListener() {
                        @Override
                        public void onCampaignListFound(List<Campaign> list) {
                            if (list.size() > 0) {
                                campaignList.add(list.get(0));
                            }
                            campaignCount--;
                            if (campaignCount == 0) {
                                editCampaignCart();
                            }
                        }

                        @Override
                        public void onFailedAPICall(int code) {
                            campaignCount--;
                            if (campaignCount == 0) {
                                editCampaignCart();
                            }
                        }
                    });
        }
    }

    private void editCampaignCart() {
        if (campaignList.size() > 0) {
            for (int i = 0; i < cartList.size(); i++) {
                cartProduct = cartList.get(i);
                int index = findCampaignIndexById(cartProduct.getCampaign().getId());
                if (index >= 0) {
                    cartProduct.setCampaign(campaignList.get(index));
                }
                cartList.set(i, cartProduct);
            }
        }
        setupAdapter();
    }

    private void setupAdapter() {
        adaptList();
        adapter = new RecViewCartSupplierListAdapter(getContext(), getActivity(),
                IntegerUtils.IDENTIFIER_CAMPAIGN_CART) {
            @Override
            public void onRemove(String cartProductId) {
                dialogBoxLoading = new DialogBoxLoading(getActivity());
                dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxLoading.show();
                String token = sharedPreferences.getString("TOKEN", "" );
                APICartCaller.deleteCartItem(token, cartProductId, getActivity().getApplication(),
                        new APIListener() {
                    @Override
                    public void onUpdateSuccessful() {
                        int index = findCartProductIndexById(cartProductId);
                        if (index >= 0) {
                            cartList.remove(index);
                        }
                        if (dialogBoxLoading.isShowing()) {
                            dialogBoxLoading.dismiss();
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
            public void onCheckout(int supplierPosition, int productPosition) {
                ArrayList<Order> ordersList = new ArrayList<>();
                List<OrderProduct> selectedProductList = new ArrayList<>();
                Supplier supplier = supplierCampaignList.get(supplierPosition);
                CartProduct cartProduct = campaignCart.get(supplier.getId()).get(productPosition);
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setProduct(cartProduct.getProduct());
                orderProduct.setQuantity(cartProduct.getQuantity());
                orderProduct.setPrice(cartProduct.getProduct().getRetailPrice());
                totalPrice = cartProduct.getQuantity() * cartProduct.getProduct().getRetailPrice();
                orderProduct.setTotalPrice(totalPrice);
                orderProduct.setCartProduct(cartProduct);
                orderProduct.setCampaign(cartProduct.getCampaign());
                selectedProductList.add(orderProduct);
                Order order = new Order();
                order.setTotalPrice(totalPrice);
                order.setCampaign(cartProduct.getCampaign());
                order.setSupplier(supplier);
                order.setInCart(true);
                order.setOrderProductList(selectedProductList);
                order.setLoyaltyDiscountPercent(0);
                ordersList.add(order);
                DialogBoxConfirm dialogBoxConfirm = new DialogBoxConfirm(getActivity(),
                        StringUtils.MES_CONFIRM_CHECKOUT) {
                    @Override
                    public void onYesClicked() {
                        Intent confirmOrderIntent = new Intent(getContext(), ConfirmOrderActivity.class);
                        confirmOrderIntent.putExtra("ORDER_LIST", ordersList);
                        confirmOrderIntent.putExtra("REQUEST_CODE", IntegerUtils.REQUEST_ORDER_CAMPAIGN);
                        confirmOrderIntent.putExtra("CART_STATUS", true);
                        startActivityForResult(confirmOrderIntent, IntegerUtils.REQUEST_COMMON);
                    }
                };
                dialogBoxConfirm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxConfirm.show();

            }
        };
        adapter.setCartList(supplierCampaignList, campaignCart);
        recViewCartSupplier.setAdapter(adapter);
        recViewCartSupplier.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL,false));
        setLoadedState();
    }

    private void adaptList() {
        supplierCampaignList = new ArrayList<>();
        campaignCart = new HashMap<>();
        for (CartProduct cartProduct : cartList) {
            supplier = cartProduct.getProduct().getSupplier();
            tempCartList = campaignCart.get(supplier.getId());
            if(tempCartList == null) {
                supplierCampaignList.add(supplier);
                tempCartList = new ArrayList<>();
            }
            tempCartList.add(cartProduct);
            campaignCart.put(supplier.getId(), tempCartList);
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

    private int findCampaignIndexById(String id) {
        for (int i = 0; i < campaignList.size(); i++) {
            if (campaignList.get(i).getId().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutCartDetail.setVisibility(View.GONE);
        layoutNoShoppingCart.setVisibility(View.GONE);
    }

    private void setLoadedState() {
        layoutLoading.setVisibility(View.GONE);
        layoutCartDetail.setVisibility(View.VISIBLE);
        layoutNoShoppingCart.setVisibility(View.GONE);
    }

    private void setEmptyState() {
        layoutLoading.setVisibility(View.GONE);
        layoutCartDetail.setVisibility(View.GONE);
        layoutNoShoppingCart.setVisibility(View.VISIBLE);
    }
}