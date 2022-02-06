package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.ObjectSerializer;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.activities.ordering.OrderConfirmActivity;
import dev.wsgroup.main.views.boxes.DialogBoxAlert;
import dev.wsgroup.main.views.boxes.DialogBoxConfirm;

public class RecViewCartSupplierListAdapter extends RecyclerView.Adapter<RecViewCartSupplierListAdapter.ViewHolder> {

    private List<Supplier> supplierList;
    private HashMap<String, List<CartProduct>> shoppingCart;
    private HashMap<String, List<OrderProduct>> shoppingOrder;
    private List<CartProduct> cartProductList;

    private Activity activity;
    private Context context;
    private RecViewCartProductListAdapter cartAdapter;
    private SharedPreferences sharedPreferences;

    public RecViewCartSupplierListAdapter(Context context, Activity activity) {
        sharedPreferences = activity.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        this.activity = activity;
        this.context = context;
    }


    public void setCartList(List<Supplier> supplierList, HashMap<String, List<CartProduct>> productList) {
        this.supplierList = supplierList;
        this.shoppingCart = productList;
    }

    public void setOrderList(List<Supplier> supplierList, HashMap<String, List<OrderProduct>> productList) {
        this.supplierList = supplierList;
        this.shoppingOrder = productList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_view_cart_supplier_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        System.out.println("bind");
        holder.txtRecViewCartSupplierName.setText(supplierList.get(position).getName());
        holder.checkboxCartSupplier.setVisibility(View.GONE);
        holder.lblDeleteCartSuppliers.setVisibility(View.GONE);
        holder.layoutCheckout.setVisibility(View.VISIBLE);
        holder.btnCheckout.setEnabled(false);
        setupCheckout(position, holder);
        setupCartProductList(holder.recViewCartProducts, position,
                shoppingCart.get(supplierList.get(position).getId()), holder);

        if(supplierList.get(position).getStatus()) {
            holder.checkboxCartSupplier.setImageResource(R.drawable.ic_checkbox_checked);
            holder.checkboxCartSupplier.setColorFilter(context.getResources().getColor(R.color.blue_main));
        } else {
            holder.checkboxCartSupplier.setImageResource(R.drawable.ic_checkbox_unchecked);
            holder.checkboxCartSupplier.setColorFilter(context.getResources().getColor(R.color.gray));
        }

//            holder.checkboxCartSupplier.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(supplierList.get(position).getStatus()) {
//                        supplierList.get(position).setStatus(false);
//                        holder.checkboxCartSupplier.setImageResource(R.drawable.ic_checkbox_unchecked);
//                        holder.checkboxCartSupplier.setColorFilter(context.getResources().getColor(R.color.gray));
//                    } else {
//                        supplierList.get(position).setStatus(true);
//                        holder.checkboxCartSupplier.setImageResource(R.drawable.ic_checkbox_checked);
//                        holder.checkboxCartSupplier.setColorFilter(context.getResources().getColor(R.color.blue_main));
//                    }
//                    cartProductList = shoppingCart.get(supplierList.get(position).getId());
//                    for (int i = 0; i < cartProductList.size(); i++) {
//                        cartProductList.get(i).setStatus(supplierList.get(position).getStatus());
//                    }
//                    setupCartProductList(holder.recViewCartProducts, supplierList.get(position).getId(), cartProductList);
//                    onCheckboxSupplierChange();
//                }
//            });

        holder.lblDeleteCartSuppliers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogBoxConfirm dialogBoxConfirm = new DialogBoxConfirm(activity, StringUtils.MES_CONFIRM_DELETE_CART) {
                    @Override
                    public void onYesClicked() {
                        onRemoveSupplier(position);
                    }
                };
                dialogBoxConfirm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogBoxConfirm.show();
            }
        });

        holder.btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Supplier supplier = supplierList.get(position);
                Order order = getSelectedCartProduct(supplier.getId());
                order.setSupplier(supplier);
                Intent checkoutActivity = new Intent(context, OrderConfirmActivity.class);
                checkoutActivity.putExtra("ORDER", (Serializable) order);
                activity.startActivityForResult(checkoutActivity, IntegerUtils.REQUEST_COMMON);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupCheckout(int position, ViewHolder holder) {
        double totalPrice = getTotalPriceCart(position);
        if(totalPrice > 0) {
            holder.layoutTotalPrice.setVisibility(View.VISIBLE);
            holder.txtTotalCartPrice.setText(MethodUtils.convertPriceString(totalPrice));
            holder.btnCheckout.setEnabled(true);
            holder.btnCheckout.getBackground().setTint(context.getResources().getColor(R.color.blue_main));
        } else {
            holder.layoutTotalPrice.setVisibility(View.GONE);
            holder.btnCheckout.setEnabled(false);
            holder.btnCheckout.getBackground().setTint(context.getResources().getColor(R.color.gray_light));
        }
    }

    private double getTotalPriceCart(int position) {
        double totalPrice = 0, tempPrice;
        cartProductList = shoppingCart.get(supplierList.get(position).getId());
        for (CartProduct cartProduct : cartProductList) {
            if(cartProduct.getSelected()) {
                tempPrice = cartProduct.getQuantity();
                if(cartProduct.getCampaignId().isEmpty()) {
                    tempPrice *= cartProduct.getProduct().getRetailPrice();
                } else {
                    tempPrice *= cartProduct.getProduct().getCampaign().getPrice();
                }
                totalPrice += tempPrice;
            }
        }
        return totalPrice;
    }

    private double getProductTotalPrice(CartProduct cartProduct) {
        double price = cartProduct.getQuantity();
        if(cartProduct.getCampaignId().isEmpty()) {
            price *= cartProduct.getProduct().getRetailPrice();
        } else {
            price *= cartProduct.getProduct().getCampaign().getPrice();
        }
        return price;
    }

    private Order getSelectedCartProduct(String supplierId) {
        Order order = new Order();
        List<OrderProduct> list = new ArrayList<>();
        OrderProduct orderProduct;
        cartProductList = shoppingCart.get(supplierId);
        for (CartProduct cartProduct : cartProductList) {
            if(cartProduct.getSelected()) {
                orderProduct = new OrderProduct();
                orderProduct.setProductId(cartProduct.getProduct().getProductId());
                orderProduct.setQuantity(cartProduct.getQuantity());
                orderProduct.setPrice(getProductTotalPrice(cartProduct));
                orderProduct.setCampaignId(cartProduct.getCampaignId());
                orderProduct.setProductType(cartProduct.getProductType());
                orderProduct.setProduct(cartProduct.getProduct());
                orderProduct.setCartProduct(cartProduct);
                orderProduct.setNote("");
                orderProduct.setTypeList(cartProduct.getTypeList());
                list.add(orderProduct);
            }
        }
        order.setCustomerDiscount(null);
        order.setOrderProductList(list);
        order.setCampaignId(list.get(0).getCampaignId());
        return order;
    }

    private void setupCartProductList(RecyclerView recyclerView, int position, List<CartProduct> productList, ViewHolder holder) {
        cartAdapter = new RecViewCartProductListAdapter(context, activity) {
            @Override
            public void onRemoveProduct(int productPosition) {
                CartProduct cartProduct = shoppingCart.get(supplierList.get(position).getId()).get(productPosition);
                APICartCaller.deleteCartItem(sharedPreferences.getString("TOKEN", "" ), cartProduct.getId(),
                        activity.getApplication(), new APIListener() {
                            @Override
                            public void onUpdateCartItemSuccessful() {
                                super.onUpdateCartItemSuccessful();
                                cartProductList = shoppingCart.get(supplierList.get(position).getId());
                                cartProductList.remove(productPosition);
                                if (cartProductList.size() == 0) {
                                    int supplierPosition = 0;
                                    for (int i = 0; i < supplierList.size(); i++) {
                                        if(supplierList.get(i).getId().equals(supplierList.get(position).getId())) {
                                            supplierPosition = i;
                                        }
                                    }
                                    onRemoveSupplier(supplierPosition);
                                } else {
                                    shoppingCart.put(supplierList.get(position).getId(), cartProductList);
                                    try {
                                        sharedPreferences.edit()
                                                .putString("SHOPPING_CART", ObjectSerializer.serialize((Serializable) shoppingCart))
                                                .commit();
                                        sharedPreferences.edit()
                                                .putString("SUPPLIER_LIST", ObjectSerializer.serialize((Serializable) supplierList))
                                                .commit();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                notifyDataSetChanged();
                            }
                        });
            }
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onCheckboxProductChange(ImageView view, int productPosition) {
                super.onCheckboxProductChange(view, productPosition);
                String supplierId = supplierList.get(position).getId();
                if(shoppingCart.get(supplierId).get(productPosition).getSelected()) {
                    shoppingCart.get(supplierId).get(productPosition).setSelected(false);
                    view.setImageResource(R.drawable.ic_checkbox_unchecked);
                    view.setColorFilter(context.getResources().getColor(R.color.gray));
                } else {
                    shoppingCart.get(supplierId).get(productPosition).setSelected(true);
                    view.setImageResource(R.drawable.ic_checkbox_checked);
                    view.setColorFilter(context.getResources().getColor(R.color.blue_main));
                }
//                setupSelectableProduct(supplierId);
//                notifyDataSetChanged();
                setupCheckout(position, holder);
            }
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onPriceMethodChange(String campaignId, int productPosition) {
                super.onPriceMethodChange(campaignId, productPosition);
                shoppingCart.get(supplierList.get(position).getId()).get(productPosition).setCampaignId(campaignId);
//                setupSelectableProduct(supplierList.get(position).getId());
//                notifyDataSetChanged();
                setupCheckout(position, holder);
            }
        };
        cartAdapter.setShoppingCart(productList);
        recyclerView.setAdapter(cartAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL,false));
    }

    public void onRemoveSupplier(int position) {}

    @Override
    public int getItemCount() {
        return supplierList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView checkboxCartSupplier;
        private TextView txtRecViewCartSupplierName, lblDeleteCartSuppliers, txtTotalCartPrice;
        private RecyclerView recViewCartProducts;
        private ConstraintLayout layoutCheckout;
        private LinearLayout layoutTotalPrice;
        private Button btnCheckout;

        public ViewHolder(View view) {
            super(view);
            checkboxCartSupplier = view.findViewById(R.id.checkboxCartSupplier);
            txtRecViewCartSupplierName = view.findViewById(R.id.txtRecViewCartSupplierName);
            lblDeleteCartSuppliers = view.findViewById(R.id.lblDeleteCartSuppliers);
            txtTotalCartPrice = view.findViewById(R.id.txtTotalCartPrice);
            recViewCartProducts = view.findViewById(R.id.recViewCartProducts);
            layoutCheckout = view.findViewById(R.id.layoutCheckout);
            layoutTotalPrice = view.findViewById(R.id.layoutTotalPrice);
            btnCheckout = view.findViewById(R.id.btnCheckout);
        }
    }
}
