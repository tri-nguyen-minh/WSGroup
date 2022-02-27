package dev.wsgroup.main.views.fragments.tabs.cart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICartCaller;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewCartSupplierListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.ObjectSerializer;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.ordering.ConfirmActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class CampaignTab extends Fragment {

    private LinearLayout layoutNoShoppingCart;
    private ConstraintLayout layoutCartDetail;
    private RecyclerView recViewCartSupplier;

    private double totalPrice;
    private SharedPreferences sharedPreferences;
    private RecViewCartSupplierListAdapter adapter;
    private List<Supplier> supplierCampaignList;
    private HashMap<String, List<CartProduct>> campaignCart;
    private List<CartProduct> cartProductList;
    private DialogBoxLoading dialogBoxLoading;

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
        recViewCartSupplier = view.findViewById(R.id.recViewCartSupplier);

        setupCartView();
    }

    private void getCartFromSession() {
        try {
            sharedPreferences = getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
            campaignCart = (HashMap<String, List<CartProduct>>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("CAMPAIGN_CART", ""));
            supplierCampaignList = (ArrayList<Supplier>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("SUPPLIER_CAMPAIGN_LIST", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupCartView() {
        getCartFromSession();
        if (supplierCampaignList.size() > 0) {
            layoutNoShoppingCart.setVisibility(View.INVISIBLE);
            layoutCartDetail.setVisibility(View.VISIBLE);
            adapter = new RecViewCartSupplierListAdapter(getContext(), getActivity(),
                    IntegerUtils.IDENTIFIER_CAMPAIGN_CART) {
                @Override
                public void onRemove(int productPosition, int supplierPosition) {
                    super.onRemove(productPosition, supplierPosition);
                    dialogBoxLoading = new DialogBoxLoading(getActivity());
                    dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogBoxLoading.show();
                    CartProduct cartProduct = campaignCart.get(supplierCampaignList.get(supplierPosition).getId()).get(productPosition);
                    String token = sharedPreferences.getString("TOKEN", "" );
                    APICartCaller.deleteCartItem(token, cartProduct.getId(),
                            getActivity().getApplication(), new APIListener() {
                                @Override
                                public void onUpdateCartItemSuccessful() {
                                    super.onUpdateCartItemSuccessful();
                                    cartProductList = campaignCart.get(supplierCampaignList.get(supplierPosition).getId());
                                    cartProductList.remove(productPosition);
                                    Supplier supplier = supplierCampaignList.get(supplierPosition);
                                    if (cartProductList.size() == 0) {
                                        campaignCart.remove(supplier.getId());
                                        supplierCampaignList.remove(supplierPosition);
                                    } else {
                                        campaignCart.put(supplier.getId(), cartProductList);
                                    }
                                    try {
                                        sharedPreferences.edit()
                                                .putString("CAMPAIGN_CART", ObjectSerializer.serialize((Serializable) campaignCart))
                                                .putString("SUPPLIER_CAMPAIGN_LIST", ObjectSerializer.serialize((Serializable) supplierCampaignList))
                                                .commit();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    if (dialogBoxLoading.isShowing()) {
                                        dialogBoxLoading.dismiss();
                                    }
                                    notifyDataSetChanged();
                                }

                                @Override
                                public void onFailedAPICall(int code) {
                                    super.onFailedAPICall(code);
                                    if (dialogBoxLoading.isShowing()) {
                                        dialogBoxLoading.dismiss();
                                    }
                                    DialogBoxAlert dialogBox =
                                            new DialogBoxAlert(getActivity(),
                                                    IntegerUtils.CONFIRM_ACTION_CODE_FAILED,
                                                    StringUtils.MES_ERROR_FAILED_API_CALL,"");
                                    dialogBox.show();
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
                    order.setCampaign(cartProduct.getCampaign());
                    order.setSupplier(supplier);
                    order.setInCart(true);
                    order.setOrderProductList(selectedProductList);
                    ordersList.add(order);
                    DialogBoxConfirm dialogBoxConfirm = new DialogBoxConfirm(getActivity(), StringUtils.MES_CONFIRM_CHECKOUT) {
                        @Override
                        public void onYesClicked() {
                            super.onYesClicked();
                            Intent confirmOrderIntent = new Intent(getContext(), ConfirmActivity.class);
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
        } else {
            layoutNoShoppingCart.setVisibility(View.VISIBLE);
            layoutCartDetail.setVisibility(View.INVISIBLE);
        }
    }
}