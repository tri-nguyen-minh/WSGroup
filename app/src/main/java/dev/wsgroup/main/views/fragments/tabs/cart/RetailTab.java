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

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Array;
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
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.ObjectSerializer;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.ordering.ConfirmActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxConfirm;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class RetailTab extends Fragment {

    private LinearLayout layoutNoShoppingCart, layoutTotalPrice;
    private ConstraintLayout layoutCartDetail, layoutCheckout;
    private RecyclerView recViewCartSupplier;
    private TextView txtTotalCartPrice;
    private Button btnCheckout;

    private double totalPrice;
    private SharedPreferences sharedPreferences;
    private RecViewCartSupplierListAdapter adapter;
    private List<Supplier> supplierRetailList;
    private HashMap<String, List<CartProduct>> retailCart;
    private List<CartProduct> cartProductList;
    private DialogBoxLoading dialogBoxLoading;

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
        layoutCheckout = view.findViewById(R.id.layoutCheckout);
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
                    List<OrderProduct> selectedProductList = new ArrayList<>();
                    cartProductList = retailCart.get(supplier.getId());
                    for (CartProduct cartProduct : cartProductList) {
                        if (cartProduct.getSelectedFlag()) {
                            orderProduct = new OrderProduct();
                            orderProduct.setProduct(cartProduct.getProduct());
                            orderProduct.setQuantity(cartProduct.getQuantity());
                            orderProduct.setPrice(cartProduct.getProduct().getRetailPrice());
                            totalPrice = cartProduct.getQuantity() * cartProduct.getProduct().getRetailPrice();
                            orderProduct.setTotalPrice(totalPrice);
                            orderProduct.setCartProduct(cartProduct);
                            selectedProductList.add(orderProduct);
                        }
                    }
                    if (selectedProductList.size() > 0) {
                        order = new Order();
                        order.setCampaign(null);
                        order.setSupplier(supplier);
                        order.setInCart(true);
                        order.setOrderProductList(selectedProductList);
                        ordersList.add(order);
                    }
                }
                DialogBoxConfirm dialogBoxConfirm = new DialogBoxConfirm(getActivity(), StringUtils.MES_CONFIRM_CHECKOUT) {
                    @Override
                    public void onYesClicked() {
                        super.onYesClicked();
                        Intent confirmOrderIntent = new Intent(getContext(), ConfirmActivity.class);
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
            sharedPreferences = getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
            retailCart = (HashMap<String, List<CartProduct>>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("RETAIL_CART", ""));
            supplierRetailList = (ArrayList<Supplier>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("SUPPLIER_RETAIL_LIST", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupCartView() {
        getCartFromSession();
        if (supplierRetailList.size() > 0) {
            layoutNoShoppingCart.setVisibility(View.INVISIBLE);
            layoutCartDetail.setVisibility(View.VISIBLE);
            adapter = new RecViewCartSupplierListAdapter(getContext(), getActivity(),
                    IntegerUtils.IDENTIFIER_RETAIL_CART) {

                @Override
                public void onRemove(int productPosition, int supplierPosition) {
                    super.onRemove(productPosition, supplierPosition);
                    dialogBoxLoading = new DialogBoxLoading(getActivity());
                    dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogBoxLoading.show();
                    CartProduct cartProduct = retailCart.get(supplierRetailList.get(supplierPosition).getId()).get(productPosition);
                    String token = sharedPreferences.getString("TOKEN", "" );
                    APICartCaller.deleteCartItem(token, cartProduct.getId(),
                            getActivity().getApplication(), new APIListener() {
                                @Override
                                public void onUpdateCartItemSuccessful() {
                                    super.onUpdateCartItemSuccessful();
                                    cartProductList = retailCart.get(supplierRetailList.get(supplierPosition).getId());
                                    cartProductList.remove(productPosition);
                                    Supplier supplier = supplierRetailList.get(supplierPosition);
                                    if (cartProductList.size() == 0) {
                                        retailCart.remove(supplier.getId());
                                        supplierRetailList.remove(supplierPosition);
                                    } else {
                                        retailCart.put(supplier.getId(), cartProductList);
                                    }
                                    try {
                                        sharedPreferences.edit()
                                                .putString("RETAIL_CART", ObjectSerializer.serialize((Serializable) retailCart))
                                                .putString("SUPPLIER_RETAIL_LIST", ObjectSerializer.serialize((Serializable) supplierRetailList))
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
                public void onCheckboxChanged(int productPosition, int supplierPosition, boolean newStatus) {
                    cartProductList = retailCart.get(supplierRetailList.get(supplierPosition).getId());
                    CartProduct cartProduct = cartProductList.get(productPosition);
                    cartProduct.setSelectedFlag(newStatus);
                    cartProductList.set(productPosition, cartProduct);
                    retailCart.put(supplierRetailList.get(supplierPosition).getId(), cartProductList);
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
            adapter.setCartList(supplierRetailList, retailCart);
            recViewCartSupplier.setAdapter(adapter);
            recViewCartSupplier.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.VERTICAL, false));
        } else {
            layoutNoShoppingCart.setVisibility(View.VISIBLE);
            layoutCartDetail.setVisibility(View.INVISIBLE);
        }
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
        for (Supplier supplier : supplierRetailList) {
            cartProductList = retailCart.get(supplier.getId());
            for (CartProduct cartProduct : cartProductList) {
                if (cartProduct.getSelectedFlag()) {
                    totalPrice += (cartProduct.getQuantity() * cartProduct.getProduct().getRetailPrice());
                }
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
        for (Supplier supplier : supplierRetailList) {
            cartProductList = retailCart.get(supplier.getId());
            for (CartProduct cartProduct : cartProductList) {
                if (cartProduct.getSelectedFlag()) {
                    return true;
                }
            }
        }
        return false;
    }
}