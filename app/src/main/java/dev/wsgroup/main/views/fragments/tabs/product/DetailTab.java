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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICategoryCaller;
import dev.wsgroup.main.models.apis.callers.APIProductCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.Category;
import dev.wsgroup.main.models.dtos.Discount;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.dtos.Supplier;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewProductListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.views.activities.SupplierActivity;
import dev.wsgroup.main.views.activities.account.SignInActivity;
import dev.wsgroup.main.views.activities.productviews.CampaignListActivity;
import dev.wsgroup.main.views.activities.productviews.PrepareProductActivity;
import dev.wsgroup.main.views.activities.productviews.SearchProductActivity;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class DetailTab extends Fragment {

    private TextView txtProductQuantity, txtProductCategory, txtProductDescription,
            txtSupplierName, txtMoreSuppliersProductsName;
    private RecyclerView recViewMoreSuppliersProducts;
    private ViewFlipper viewFlipperProduct;
    private ImageView imgCommonFlipper, imgProductCategory, imgProduct;
    private TextView txtProductName, txtProductOrderCount, txtProductReviewCount,
            txtRatingProduct, txtRetailPrice, txtCampaignCount, lblDescriptionLength,
            txtLoyaltyDiscount, lblCampaignCount;
    private MaterialRatingBar ratingProduct;
    private LinearLayout linearLayoutCampaign;
    private ConstraintLayout constraintLayoutSupplierName, layoutSelectCampaign,
            layoutLoyalty, constraintLayoutProductCategory;
    private Button btnPurchaseProduct;
    private LinearLayout layoutProductQuantityWithCampaign, layoutMoreSuppliersProducts;

    private SharedPreferences sharedPreferences;
    private Intent intent;
    private Product product;
    private Supplier supplier;
    private String userId;
    private boolean orderCountCheck, ratingCheck;

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
        viewFlipperProduct = view.findViewById(R.id.viewFlipperProduct);
        imgProductCategory = view.findViewById(R.id.imgProductCategory);
        imgProduct = view.findViewById(R.id.imgProduct);
        txtProductName = view.findViewById(R.id.txtProductName);
        txtProductOrderCount = view.findViewById(R.id.txtProductOrderCount);
        txtProductReviewCount = view.findViewById(R.id.txtProductReviewCount);
        txtRatingProduct = view.findViewById(R.id.txtRatingProduct);
        txtRetailPrice = view.findViewById(R.id.txtProductRetailPrice);
        txtCampaignCount = view.findViewById(R.id.txtCampaignCount);
        lblDescriptionLength = view.findViewById(R.id.lblDescriptionLength);
        txtLoyaltyDiscount = view.findViewById(R.id.txtLoyaltyDiscount);
        lblCampaignCount = view.findViewById(R.id.lblCampaignCount);
        ratingProduct = view.findViewById(R.id.ratingProduct);
        linearLayoutCampaign = view.findViewById(R.id.linearLayoutCampaign);
        constraintLayoutSupplierName = view.findViewById(R.id.constraintLayoutSupplierName);
        layoutSelectCampaign = view.findViewById(R.id.layoutSelectCampaign);
        layoutLoyalty = view.findViewById(R.id.layoutLoyalty);
        constraintLayoutProductCategory = view.findViewById(R.id.constraintLayoutProductCategory);
        btnPurchaseProduct = view.findViewById(R.id.btnPurchaseProduct);
        layoutProductQuantityWithCampaign = view.findViewById(R.id.layoutProductQuantityWithCampaign);
        layoutMoreSuppliersProducts = view.findViewById(R.id.layoutMoreSuppliersProducts);

        sharedPreferences = getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        userId = sharedPreferences.getString("USER_ID", "");
        intent = getActivity().getIntent();
        product = (Product) intent.getSerializableExtra("PRODUCT");

        if(product != null) {
            setProduct();
        }
        lblDescriptionLength.setText("Show more");
        txtProductDescription.setMaxLines(4);
        imgProductCategory.setVisibility(View.GONE);
        layoutMoreSuppliersProducts.setVisibility(View.GONE);

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

        btnPurchaseProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                product.setCampaign(null);
                startDialogBoxProductDetail();
            }
        });
    }

    private void setProduct() {
        txtProductName.setText(product.getName());
        txtRatingProduct.setText(product.getRating() + "");
        txtRetailPrice.setText(MethodUtils.formatPriceString(product.getRetailPrice()));
        txtProductOrderCount.setText(MethodUtils.formatOrderOrReviewCount(product.getOrderCount()));
        txtProductReviewCount.setText(MethodUtils.formatOrderOrReviewCount(product.getReviewCount()));
        ratingProduct.setRating((float) product.getRating());
        ratingProduct.setIsIndicator(true);
        if(product.getImageList() != null && !product.getImageList().isEmpty()) {
            if (product.getImageList().size() > 1) {
                imgProduct.setVisibility(View.GONE);
                viewFlipperProduct.setVisibility(View.VISIBLE);
                for (String imageLink : product.getImageList()) {
                    imgCommonFlipper = new ImageView(getContext());
                    Glide.with(getContext()).load(imageLink).into(imgCommonFlipper);
                    viewFlipperProduct.addView(imgCommonFlipper);
                }
                viewFlipperProduct.setAutoStart(true);
                viewFlipperProduct.setFlipInterval(4000);
                viewFlipperProduct.setInAnimation(AnimationUtils.loadAnimation(getContext(),
                        R.anim.anim_slide_in_left));
                viewFlipperProduct.setOutAnimation(AnimationUtils.loadAnimation(getContext(),
                        R.anim.anim_slide_out_left));
                viewFlipperProduct.startFlipping();
            } else {
                imgProduct.setVisibility(View.VISIBLE);
                viewFlipperProduct.setVisibility(View.GONE);
                Glide.with(getContext()).load(product.getImageList().get(0)).into(imgProduct);
            }
        }
        linearLayoutCampaign.setVisibility(View.GONE);
        layoutSelectCampaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent campaignSelectIntent = new Intent(getContext(), CampaignListActivity.class);
                campaignSelectIntent.putExtra("PRODUCT", product);
                startActivityForResult(campaignSelectIntent, IntegerUtils.REQUEST_SELECT_CAMPAIGN);
            }
        });
        txtProductQuantity.setVisibility(View.VISIBLE);
        layoutProductQuantityWithCampaign.setVisibility(View.GONE);
        txtProductQuantity.setText(getAvailableQuantity() + "");
        int count = getActiveCampaignCount();
        if(count > 0) {
            linearLayoutCampaign.setVisibility(View.VISIBLE);
            txtCampaignCount.setText(count + "");
            lblCampaignCount.setText(count > 1 ? "Ongoing Campaigns" : "Ongoing Campaign");
        }
        txtProductDescription.setText(product.getDescription());
        supplier = product.getSupplier();
        txtSupplierName.setText(supplier.getName());
        txtMoreSuppliersProductsName.setText(supplier.getName());
        if (supplier.getLoyaltyStatus() != null
                && supplier.getLoyaltyStatus().getDiscountPercent() != 0) {
            layoutLoyalty.setVisibility(View.VISIBLE);
            txtLoyaltyDiscount.setText(supplier.getLoyaltyStatus().getDiscountPercent() + "%");
        } else {
            layoutLoyalty.setVisibility(View.GONE);
        }
        if (getAvailableQuantity() == 0) {
            btnPurchaseProduct.setEnabled(false);
            btnPurchaseProduct.getBackground().setTint(getContext().getResources()
                              .getColor(R.color.gray_light));
        } else {
            btnPurchaseProduct.setEnabled(true);
            btnPurchaseProduct.getBackground().setTint(getContext().getResources()
                              .getColor(R.color.blue_main));
        }
        APICategoryCaller.getCategoryById(product.getCategoryId(),
                getActivity().getApplication(), new APIListener() {
            @Override
            public void onCategoryFound(Category category) {
                if (category != null) {
                    txtProductCategory.setText(category.getName());
                    imgProductCategory.setVisibility(View.VISIBLE);
                    constraintLayoutProductCategory.setOnClickListener(null);
                    constraintLayoutProductCategory.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent searchIntent
                                    = new Intent(getContext(), SearchProductActivity.class);
                            searchIntent.putExtra("IDENTIFIER",
                                                    IntegerUtils.IDENTIFIER_SEARCH_CATEGORY);
                            searchIntent.putExtra("SEARCH_STRING", category.getCategoryId());
                            startActivityForResult(searchIntent, IntegerUtils.REQUEST_COMMON);
                        }
                    });
                }
            }
        });
        getSupplierProductList();
    }

    private int getActiveCampaignCount() {
        int count = 0;
        if (product.getCampaignList().size() > 0) {
            for (Campaign campaign : product.getCampaignList()) {
                if (campaign.getStatus().equals("active")) {
                    count++;
                }
            }
        }
        return count;
    }

    private int getAvailableQuantity() {
        int count = 0;
        if (product.getCampaignList().size() > 0) {
            for (Campaign campaign : product.getCampaignList()) {
                count += campaign.getMaxQuantity();
            }
        }
        count = product.getQuantity() - count;
        return Math.max(count, 0);
    }

    private void getSupplierProductList() {
        APIProductCaller.getProductListBySupplierId(product.getSupplier().getId(),
                null, getActivity().getApplication(), new APIListener() {
            @Override
            public void onProductListFound(List<Product> productList) {
                if(productList.size() > 1) {
                    for (int i = (productList.size() - 1); i >= 0; i--) {
                        if (productList.get(i).getProductId().equals(product.getProductId())) {
                            productList.remove(i);
                            i = 0;
                        }
                    }

                    orderCountCheck = false; ratingCheck = false;
                    APIListener listener = new APIListener() {
                        @Override
                        public void onProductOrderCountFound(Map<String, Integer> countList) {
                            for (Product product : productList) {
                                if (countList.get(product.getProductId()) != null) {
                                    product.setOrderCount(countList.get(product.getProductId()));
                                }else {
                                    product.setOrderCount(0);
                                }
                            }
                            orderCountCheck = true;
                            setupMoreProductView(productList);
                        }
                        @Override
                        public void onRatingListCount(Map<String, Double> ratingList) {
                            for (Product product : productList) {
                                if (ratingList.get(product.getProductId()) != null) {
                                    product.setRating(ratingList.get(product.getProductId()));
                                } else {
                                    product.setRating(0);
                                }
                            }
                            ratingCheck = true;
                            setupMoreProductView(productList);
                        }

                        @Override
                        public void onFailedAPICall(int code) {
                            setupNoProductView();
                        }
                    };
                    APIProductCaller.getOrderCountByProductList(productList,
                            getActivity().getApplication(), listener);
                    APIProductCaller.getRatingByProductIdList(productList,
                            getActivity().getApplication(), listener);
                    setupMoreProductView(productList);
                } else {
                    setupNoProductView();
                }
            }
            @Override
            public void onFailedAPICall(int errorCode) {
                setupNoProductView();
            }
        });
    }

    private void setupMoreProductView(List<Product> productList) {
        if (orderCountCheck && ratingCheck) {
            System.out.println("supplier list: " + productList.size());
            layoutMoreSuppliersProducts.setVisibility(View.VISIBLE);
            RecViewProductListAdapter adapter = new RecViewProductListAdapter(getContext(), getActivity());
            adapter.setProductsList(productList);
            recViewMoreSuppliersProducts.setAdapter(adapter);
            recViewMoreSuppliersProducts.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
        }
    }

    private void setupNoProductView() {
        layoutMoreSuppliersProducts.setVisibility(View.GONE);
    }

    private void startDialogBoxProductDetail() {
        if (userId.isEmpty()) {
            Intent SignInIntent = new Intent(getContext(), SignInActivity.class);
            startActivityForResult(SignInIntent, IntegerUtils.REQUEST_LOGIN);
        } else {
            Intent campaignSelectIntent = new Intent(getContext(), PrepareProductActivity.class);
            campaignSelectIntent.putExtra("PRODUCT", product);
            startActivityForResult(campaignSelectIntent, IntegerUtils.REQUEST_MAKE_PURCHASE);
        }
    }
}