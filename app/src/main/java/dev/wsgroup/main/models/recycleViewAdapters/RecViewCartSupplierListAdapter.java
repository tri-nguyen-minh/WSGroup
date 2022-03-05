package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class RecViewCartSupplierListAdapter extends RecyclerView.Adapter<RecViewCartSupplierListAdapter.ViewHolder> {

    private List<Supplier> supplierList;
    private HashMap<String, List<CartProduct>> shoppingCart;

    private Activity activity;
    private Context context;
    private int identifier;
    private RecViewCartProductListAdapter cartAdapter;

    public RecViewCartSupplierListAdapter(Context context, Activity activity, int identifier) {
        this.activity = activity;
        this.context = context;
        this.identifier = identifier;
    }

    public void setCartList(List<Supplier> supplierList, HashMap<String, List<CartProduct>> productList) {
        this.supplierList = supplierList;
        this.shoppingCart = productList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                    .inflate(R.layout.recycle_view_cart_supplier_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtRecViewCartSupplierName.setText(supplierList.get(position).getName());
        holder.checkboxCartSupplier.setVisibility(View.GONE);
        setupCartProductList(holder.recViewCartProducts, position,
                shoppingCart.get(supplierList.get(position).getId()), holder);
        holder.checkboxCartSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void setupCartProductList(RecyclerView recyclerView, int position, List<CartProduct> productList, ViewHolder holder) {
        cartAdapter = new RecViewCartProductListAdapter(context, activity, identifier) {

            @Override
            public void onRemoveProduct(String cartProductId) {
                onRemove(cartProductId);
            }

            @Override
            public void onCheckboxProductChanged(String cartProductId, boolean status) {
                onCheckboxChanged(cartProductId, status);
            }

            @Override
            public void onPriceChanging() {
                RecViewCartSupplierListAdapter.this.onPriceChanging();
            }

            @Override
            public void onPriceChanged() {
                RecViewCartSupplierListAdapter.this.onPriceChanged();
            }

            @Override
            public void onCheckout(int productPosition) {
                RecViewCartSupplierListAdapter.this.onCheckout(position, productPosition);
            }
        };
        cartAdapter.setShoppingCart(productList);
        recyclerView.setAdapter(cartAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.VERTICAL,false));
    }

    public void onCheckboxChanged(String cartProductId, boolean newStatus) { }

    public void onRemove(String cartProductId) {}

    public void onPriceChanging() {}

    public void onPriceChanged() {}

    public void onCheckout(int supplierPosition, int productPosition) {}

    @Override
    public int getItemCount() {
        return supplierList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView checkboxCartSupplier;
        private TextView txtRecViewCartSupplierName, txtTotalCartPrice;
        private RecyclerView recViewCartProducts;

        public ViewHolder(View view) {
            super(view);
            checkboxCartSupplier = view.findViewById(R.id.checkboxCartSupplier);
            txtRecViewCartSupplierName = view.findViewById(R.id.txtRecViewCartSupplierName);
            txtTotalCartPrice = view.findViewById(R.id.txtTotalCartPrice);
            recViewCartProducts = view.findViewById(R.id.recViewCartProducts);
        }
    }
}
