package dev.wsgroup.main.views.fragments.tabs.product;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICategoryCaller;
import dev.wsgroup.main.models.apis.callers.APIProductCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.Category;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewProductListAdapter;

public class DetailTab extends Fragment {

    private TextView txtProductQuantity, txtProductCategory, txtProductDescription,
            txtSupplierName, txtMoreSuppliersProductsName;
    private RecyclerView recViewMoreSuppliersProducts;

    private Intent intent;
    private Product product;
    private String supplierId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtProductQuantity = view.findViewById(R.id.txtProductQuantity);
        txtProductCategory = view.findViewById(R.id.txtProductCategory);
        txtProductDescription = view.findViewById(R.id.txtProductDescription);
        txtSupplierName = view.findViewById(R.id.txtSupplierName);
        txtMoreSuppliersProductsName = view.findViewById(R.id.txtMoreSuppliersProductsName);
        recViewMoreSuppliersProducts = view.findViewById(R.id.recViewMoreSuppliersProducts);

        intent = getActivity().getIntent();
        product = (Product)intent.getSerializableExtra("PRODUCT");

        if(product != null) {
            supplierId = product.getSupplier().getId();
            Campaign campaign = product.getCampaign();
            if(campaign != null) {
                if (campaign.getStatus().equals("active")) {
                    int quantity = product.getQuantity() - campaign.getQuantity();
                    txtProductQuantity.setText(quantity + "");
                } else {
                    txtProductQuantity.setText(product.getQuantity() + "");
                }
            } else {
                txtProductQuantity.setText(product.getQuantity() + "");
            }
            txtProductDescription.setText(product.getDescription());
            txtSupplierName.setText(product.getSupplier().getName());
            txtMoreSuppliersProductsName.setText(product.getSupplier().getName());

            APICategoryCaller.getCategoryById(product.getCategoryId(), getActivity().getApplication(), new APIListener() {
                @Override
                public void onCategoryFound(Category category) {
                    super.onCategoryFound(category);
                    txtProductCategory.setText(category.getName());
                }
            });

            getProductList();
        }

    }
    private void getProductList() {
        APIProductCaller.getAllProduct(getActivity().getApplication(), new APIListener() {
            @Override
            public void onProductListFound(List<Product> productList) {
                super.onProductListFound(productList);
                if(!productList.isEmpty()) {
                    setupView(productList);
                } else {
                    setupNoProductView();
                }
            }
            @Override
            public void onFailedAPICall(int errorCode) {
                super.onFailedAPICall(errorCode);
                setupNoProductView();
            }
        });
    }

    private void setupView(List<Product> productList) {
        RecViewProductListAdapter adapter = new RecViewProductListAdapter(getContext(), getActivity());
        adapter.setProductsList(productList);
        recViewMoreSuppliersProducts.setAdapter(adapter);
        recViewMoreSuppliersProducts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupNoProductView() {
    }
}