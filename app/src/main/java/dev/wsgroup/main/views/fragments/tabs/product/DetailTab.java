package dev.wsgroup.main.views.fragments.tabs.product;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICategoryCaller;
import dev.wsgroup.main.models.apis.callers.APIProductCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.Category;
import dev.wsgroup.main.models.dtos.Discount;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewProductListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.views.activities.SupplierActivity;
import dev.wsgroup.main.views.activities.productviews.CampaignListActivity;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class DetailTab extends Fragment {

    private TextView txtProductQuantity, txtProductQuantityWithCampaign, txtProductQuantityCampaign,
            txtProductCategory, txtProductDescription, txtSupplierName, txtMoreSuppliersProductsName;
    private RecyclerView recViewMoreSuppliersProducts, recViewDiscountSimple;
    private ViewFlipper viewFlipperProduct;
    private ImageView imgCommonFlipper;
    private TextView txtProductName, txtProductOrderCount, txtProductReviewCount, txtRatingProduct,
            txtRetailPrice, txtCampaignDescription, txtCampaignPrice, txtCampaignQuantity,
            txtDiscountEndDate, txtCampaignOrderCount, txtCampaignQuantityCount,
            txtCampaignQuantityBar, lblCampaignOrderCount, lblCampaignQuantitySeparator,
            txtCampaignNote, txtCampaignCount, lblDescriptionLength;
    private MaterialRatingBar ratingProduct;
//    private RatingBar ratingProduct;
    private LinearLayout linearLayoutCampaign;
    private ConstraintLayout layoutRetailQuantity,
            layoutCampaignQuantity, constraintLayoutSupplierName, layoutSelectCampaign;
    private ProgressBar progressBarQuantityCount;
    private LinearLayout layoutProductQuantityWithCampaign, layoutMoreSuppliersProducts;

    private SharedPreferences sharedPreferences;
    private Intent intent;
    private Product product;
    private String userId;
    private Campaign campaign;
    private List<Discount> discountList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_product_detail_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        txtProductQuantity = view.findViewById(R.id.txtProductQuantity);
        txtProductQuantityWithCampaign = view.findViewById(R.id.txtProductQuantityWithCampaign);
        txtProductQuantityCampaign = view.findViewById(R.id.txtProductQuantityTotalWithCampaign);
        txtProductCategory = view.findViewById(R.id.txtProductCategory);
        txtProductDescription = view.findViewById(R.id.txtProductDescription);
        txtSupplierName = view.findViewById(R.id.txtSupplierName);
        txtMoreSuppliersProductsName = view.findViewById(R.id.txtMoreSuppliersProductsName);
        recViewMoreSuppliersProducts = view.findViewById(R.id.recViewMoreSuppliersProducts);
//        recViewDiscountSimple = view.findViewById(R.id.recViewDiscountSimple);
        viewFlipperProduct = view.findViewById(R.id.viewFlipperProduct);
        txtProductName = view.findViewById(R.id.txtProductName);
        txtProductOrderCount = view.findViewById(R.id.txtProductOrderCount);
        txtProductReviewCount = view.findViewById(R.id.txtProductReviewCount);
        txtRatingProduct = view.findViewById(R.id.txtRatingProduct);
        txtRetailPrice = view.findViewById(R.id.txtProductRetailPrice);
        txtCampaignDescription = view.findViewById(R.id.txtCampaignDescription);
        txtCampaignPrice = view.findViewById(R.id.txtCampaignPrice);
        txtCampaignQuantity = view.findViewById(R.id.txtCampaignQuantity);
        txtDiscountEndDate = view.findViewById(R.id.txtDiscountEndDate);
        txtCampaignOrderCount = view.findViewById(R.id.txtCampaignOrderCount);
        txtCampaignQuantityCount = view.findViewById(R.id.txtCampaignQuantityCount);
        txtCampaignQuantityBar = view.findViewById(R.id.txtCampaignQuantityBar);
        lblCampaignOrderCount = view.findViewById(R.id.lblCampaignOrderCount);
        lblCampaignQuantitySeparator = view.findViewById(R.id.lblCampaignQuantitySeparator);
        txtCampaignNote = view.findViewById(R.id.txtCampaignNote);
        txtCampaignCount = view.findViewById(R.id.txtCampaignCount);
        lblDescriptionLength = view.findViewById(R.id.lblDescriptionLength);
        ratingProduct = view.findViewById(R.id.ratingProduct);
        linearLayoutCampaign = view.findViewById(R.id.linearLayoutCampaign);
        layoutRetailQuantity = view.findViewById(R.id.layoutRetailQuantity);
        layoutCampaignQuantity = view.findViewById(R.id.layoutCampaignQuantity);
        constraintLayoutSupplierName = view.findViewById(R.id.constraintLayoutSupplierName);
        layoutSelectCampaign = view.findViewById(R.id.layoutSelectCampaign);
        progressBarQuantityCount = view.findViewById(R.id.progressBarQuantityCount);
        layoutProductQuantityWithCampaign = view.findViewById(R.id.layoutProductQuantityWithCampaign);
        layoutMoreSuppliersProducts = view.findViewById(R.id.layoutMoreSuppliersProducts);

        sharedPreferences = getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("USER_ID", "");
        intent = getActivity().getIntent();
        product = (Product)intent.getSerializableExtra("PRODUCT");

        if(product != null) {
            campaign = product.getCampaign();
            setProduct();
        }
        lblDescriptionLength.setText("Show more");
        txtProductDescription.setMaxLines(4);

        constraintLayoutSupplierName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent supplierIntent = new Intent(getContext(), SupplierActivity.class);
                supplierIntent.putExtra("SUPPLIER_ID", product.getSupplier().getId());
                startActivity(supplierIntent);
            }
        });
        txtMoreSuppliersProductsName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent supplierIntent = new Intent(getContext(), SupplierActivity.class);
                supplierIntent.putExtra("SUPPLIER_ID", product.getSupplier().getId());
                startActivity(supplierIntent);
            }
        });

        lblDescriptionLength.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtProductDescription.getMaxLines() == Integer.MAX_VALUE) {
                    txtProductDescription.setMaxLines(4);
                    lblDescriptionLength.setText("Show more");
                } else {
                    txtProductDescription.setMaxLines(Integer.MAX_VALUE);
                    lblDescriptionLength.setText("Show less");
                }
            }
        });
    }

    private void setProduct() {
        txtProductName.setText(product.getName());
        txtProductOrderCount.setText(getOrderCountByProductId(product.getProductId()));
        txtProductReviewCount.setText(getReviewCountByProductId(product.getProductId()));

        txtRatingProduct.setText(product.getRating() + "");
        txtRetailPrice.setText(MethodUtils.formatPriceString(product.getRetailPrice()));
        ratingProduct.setRating((float)product.getRating());

        if(product.getImageList() != null) {
            for (String imageLink : product.getImageList()) {
                imgCommonFlipper = new ImageView(getContext());
                Glide.with(getContext()).load(imageLink).into(imgCommonFlipper);
                viewFlipperProduct.addView(imgCommonFlipper);
            }
            imgCommonFlipper = new ImageView(getContext());
            imgCommonFlipper.setImageResource(R.drawable.img_unavailable);
            viewFlipperProduct.addView(imgCommonFlipper);
            viewFlipperProduct.setAutoStart(true);
            viewFlipperProduct.setFlipInterval(4000);
            viewFlipperProduct.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_in_left));
            viewFlipperProduct.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.anim_slide_out_left));
            viewFlipperProduct.startFlipping();
        }
        if(product.getCampaignList().size() > 0) {
            linearLayoutCampaign.setVisibility(View.VISIBLE);
            txtCampaignCount.setText(product.getCampaignList().size() + "");
            layoutSelectCampaign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent campaignSelectIntent = new Intent(getContext(), CampaignListActivity.class);
                    campaignSelectIntent.putExtra("PRODUCT", product);
                    startActivityForResult(campaignSelectIntent, IntegerUtils.REQUEST_SELECT_CAMPAIGN);
                }
            });
        } else {
            linearLayoutCampaign.setVisibility(View.GONE);
            txtProductQuantity.setVisibility(View.VISIBLE);
            layoutProductQuantityWithCampaign.setVisibility(View.GONE);
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
        getSupplierProductList();
    }

    private void getSupplierProductList() {
        APIProductCaller.getProductListBySupplierId(product.getSupplier().getId(),null, getActivity().getApplication(), new APIListener() {
            @Override
            public void onProductListFound(List<Product> productList) {
                super.onProductListFound(productList);
                if(!productList.isEmpty()) {
                    for (int i = (productList.size() - 1); i >= 0; i--) {
                        if (productList.get(i).getProductId().equals(product.getProductId())) {
                            productList.remove(i);
                            i = 0;
                        }
                    }
                    setupMoreProductView(productList);
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

    private String getOrderCountByProductId(String productId) {
        int count = 12000;
//        API get total order count by productId
        return MethodUtils.formatOrderOrReviewCount(count);
    }

    private String getReviewCountByProductId(String productId) {
        int count = 12000;
//        API get total review count by productId
        return MethodUtils.formatOrderOrReviewCount(count);
    }

    private void getDiscountList() {

//        call API discount list

        discountList = new ArrayList<>();
        Discount discount = new Discount();
        discount.setId("1");
        discount.setSupplierId("testing");
        discount.setCode("111111");
        discount.setDescription("Special discount for orders of more than 50 items");
        discount.setDiscountPrice(25000);
        discount.setEndDate("2022-01-24T00:00:00.000Z");
        discount.setMinPrice(300000);
        discount.setMinQuantity(10);
        discount.setStatus(true);
        discountList.add(discount);
        discount = new Discount();
        discount.setId("2");
        discount.setSupplierId("testing");
        discount.setCode("111213");
        discount.setDescription("20K off for orders above 500.000");
        discount.setDiscountPrice(20000);
        discount.setEndDate("2022-03-01T00:00:00.000Z");
        discount.setMinPrice(500000);
        discount.setStatusByString("ready");
        discountList.add(discount);
        discount = new Discount();
        discount.setId("3");
        discount.setSupplierId("testing");
        discount.setCode("533241");
        discount.setDescription("Spring discount for all orders");
        discount.setDiscountPrice(5000);
        discount.setEndDate("2022-02-24T00:00:00.000Z");
        discount.setMinQuantity(5);
        discount.setStatusByString("unready");
        discountList.add(discount);
        discount = new Discount();
        discount.setId("4");
        discount.setSupplierId("testing");
        discount.setCode("098123");
        discount.setDescription("another discount for all orders");
        discount.setDiscountPrice(25000);
        discount.setEndDate("2022-01-12T00:00:00.000Z");
        discount.setStatus(false);
        discountList.add(discount);
    }

    private void setupMoreProductView(List<Product> productList) {
        layoutMoreSuppliersProducts.setVisibility(View.VISIBLE);
        RecViewProductListAdapter adapter = new RecViewProductListAdapter(getContext(), getActivity());
        adapter.setProductsList(productList);
        recViewMoreSuppliersProducts.setAdapter(adapter);
        recViewMoreSuppliersProducts.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void setupNoProductView() {
        layoutMoreSuppliersProducts.setVisibility(View.GONE);
    }
}