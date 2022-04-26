package dev.wsgroup.main.views.fragments.tabs.main;

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
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APICampaignCaller;
import dev.wsgroup.main.models.apis.callers.APIChatCaller;
import dev.wsgroup.main.models.apis.callers.APINotificationCaller;
import dev.wsgroup.main.models.apis.callers.APIProductCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Message;
import dev.wsgroup.main.models.dtos.Notification;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewCampaignSearchAdapter;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewProductListAdapter;
import dev.wsgroup.main.models.services.FirebaseDatabaseReferences;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.ObjectSerializer;
import dev.wsgroup.main.views.activities.CartActivity;
import dev.wsgroup.main.views.activities.NotificationActivity;
import dev.wsgroup.main.views.activities.account.SignInActivity;
import dev.wsgroup.main.views.activities.message.MessageListActivity;
import dev.wsgroup.main.views.activities.productviews.PrepareProductActivity;
import dev.wsgroup.main.views.activities.productviews.SearchProductActivity;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class HomeTab extends Fragment {

    private RelativeLayout layoutLoading;
    private LinearLayout layoutNoProductFound;
    private TextView lblRetryGetProduct, txtProductDetailCartCount,
            txtMessageCount, txtNotificationCount, editSearchProduct;
    private CardView cardViewProductDetailCartCount, cardViewMessageCount, cardViewNotificationCount;
    private ScrollView scrollViewHomeFragment;
    private ConstraintLayout constraintLayoutShoppingCart, constraintLayoutMessage,
            constraintLayoutNotification;
    private LinearLayout layoutOngoing, layoutMostOrdered, layoutNew, layoutAll,
            layoutSharingCampaign, layoutSingleCampaign;
    private RecyclerView recViewOngoing, recViewMostOrdered, recViewNew, recViewAll,
            recViewSharingCampaign, recViewSingleCampaign;

    private SharedPreferences sharedPreferences;
    private List<CartProduct> retailList, campaignList;
    private String userId, accountId, token;
    private int cartCount, messageCount, notificationCount, listCount;
    private boolean messageLoading, notificationLoading,
            orderCountOngoing, ratingOngoing,
            orderCountMostOrdered, ratingMostOrdered,
            orderCountNew, ratingNew,
            orderCountAll, ratingAll,
            activeCampaignStatus, readyCampaignStatus;
    private List<Campaign> tempList;
    private RecViewProductListAdapter productAdapter;
    private RecViewCampaignSearchAdapter campaignAdapter;
    private FirebaseDatabaseReferences firebaseReferences;
    private DialogBoxLoading dialogBoxLoading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_home_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutLoading = view.findViewById(R.id.layoutLoading);
        layoutNoProductFound = view.findViewById(R.id.layoutNoProductFound);
        lblRetryGetProduct = view.findViewById(R.id.lblRetryGetProduct);
        txtProductDetailCartCount = view.findViewById(R.id.txtProductDetailCartCount);
        txtMessageCount = view.findViewById(R.id.txtMessageCount);
        txtNotificationCount = view.findViewById(R.id.txtNotificationCount);
        editSearchProduct = view.findViewById(R.id.editSearchProduct);
        recViewOngoing = view.findViewById(R.id.recViewOngoing);
        cardViewProductDetailCartCount = view.findViewById(R.id.cardViewProductDetailCartCount);
        cardViewMessageCount = view.findViewById(R.id.cardViewMessageCount);
        cardViewNotificationCount = view.findViewById(R.id.cardViewNotificationCount);
        scrollViewHomeFragment = view.findViewById(R.id.scrollViewHomeFragment);
        constraintLayoutShoppingCart = view.findViewById(R.id.constraintLayoutShoppingCart);
        constraintLayoutMessage = view.findViewById(R.id.constraintLayoutMessage);
        constraintLayoutNotification = view.findViewById(R.id.constraintLayoutNotification);
        layoutOngoing = view.findViewById(R.id.layoutOngoing);
        layoutMostOrdered = view.findViewById(R.id.layoutMostOrdered);
        layoutNew = view.findViewById(R.id.layoutNew);
        layoutAll = view.findViewById(R.id.layoutAll);
        layoutSharingCampaign = view.findViewById(R.id.layoutSharingCampaign);
        layoutSingleCampaign = view.findViewById(R.id.layoutSingleCampaign);
        recViewOngoing = view.findViewById(R.id.recViewOngoing);
        recViewMostOrdered = view.findViewById(R.id.recViewMostOrdered);
        recViewNew = view.findViewById(R.id.recViewNew);
        recViewAll = view.findViewById(R.id.recViewAll);
        recViewSharingCampaign = view.findViewById(R.id.recViewSharingCampaign);
        recViewSingleCampaign = view.findViewById(R.id.recViewSingleCampaign);

        cartCount = 0;
        setData();
        getProductLists();

        constraintLayoutMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userId.isEmpty()) {
                    Intent signInIntent = new Intent(getContext(), SignInActivity.class);
                    signInIntent.putExtra("MAIN_TAB_POSITION", 0);
                    startActivityForResult(signInIntent, IntegerUtils.REQUEST_LOGIN_FOR_MESSAGE);
                } else {
                    Intent messageIntent = new Intent(getContext(), MessageListActivity.class);
                    messageIntent.putExtra("MAIN_TAB_POSITION", 0);
                    startActivityForResult(messageIntent, IntegerUtils.REQUEST_COMMON);
                }
            }
        });

        constraintLayoutNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userId.isEmpty()) {
                    Intent signInIntent = new Intent(getContext(), SignInActivity.class);
                    signInIntent.putExtra("MAIN_TAB_POSITION", 0);
                    startActivityForResult(signInIntent, IntegerUtils.REQUEST_LOGIN_FOR_CART);
                } else {
                    Intent notificationIntent = new Intent(getContext(), NotificationActivity.class);
                    notificationIntent.putExtra("MAIN_TAB_POSITION", 0);
                    startActivityForResult(notificationIntent, IntegerUtils.REQUEST_COMMON);
                }
            }

        });

        constraintLayoutShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userId.isEmpty()) {
                    Intent signInIntent = new Intent(getContext(), SignInActivity.class);
                    signInIntent.putExtra("MAIN_TAB_POSITION", 0);
                    startActivityForResult(signInIntent, IntegerUtils.REQUEST_LOGIN_FOR_CART);
                } else {
                    Intent cartIntent = new Intent(getContext(), CartActivity.class);
                    cartIntent.putExtra("MAIN_TAB_POSITION", 0);
                    startActivityForResult(cartIntent, IntegerUtils.REQUEST_COMMON);
                }
            }
        });

        lblRetryGetProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProductLists();
            }
        });

        editSearchProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(getContext(), SearchProductActivity.class);
                searchIntent.putExtra("IDENTIFIER", IntegerUtils.IDENTIFIER_SEARCH_BAR);
                startActivityForResult(searchIntent, IntegerUtils.REQUEST_COMMON);
            }
        });
    }

    private void setData() {
        if (getActivity() == null) {
            setFailedState();
        } else {
            sharedPreferences = getActivity().getSharedPreferences("PREFERENCE",
                    Context.MODE_PRIVATE);
            userId = sharedPreferences.getString("USER_ID", "");
            accountId = sharedPreferences.getString("ACCOUNT_ID", "");
            token = sharedPreferences.getString("TOKEN", "");
            if (!userId.isEmpty()) {
                firebaseReferences = new FirebaseDatabaseReferences();
                editCartCountByUser();
                editUnreadMessageCountByUser();
                editUnreadNotificationCountByUser();
                setRealtimeFirebase();
            }
        }
    }

    private void getProductLists() {
        listCount = 50;
        setLoadingState();
        getSharingCampaignList();
        getSingleCampaignList();
        getOngoingList();
        getMostOrderedList();
        getNewList();
        getAllList();
    }

    private void getSharingCampaignList() {
        if (getActivity() == null) {
            setFailedState();
        } else {
            APICampaignCaller.searchCampaign(null, "",
                    getActivity().getApplication(), new APIListener() {
                @Override
                public void onCampaignListFound(List<Campaign> campaignList) {
                    if (campaignList.size() > 0) {
                        for (int i = campaignList.size() - 1; i >= 0; i--) {
                            if (!campaignList.get(i).getShareFlag()) {
                                campaignList.remove(i);
                            }
                        }
                        MethodUtils.sortSharingCampaign(campaignList);
                        setupCampaignRecView(campaignList, recViewSharingCampaign, layoutSharingCampaign);
                    } else {
                        listCount -= 5;
                        finalizeView();
                    }
                }

                @Override
                public void onFailedAPICall(int code) {
                    listCount -= 5;
                    finalizeView();
                }
            });
        }
    }

    private void getSingleCampaignList() {
        if (getActivity() == null) {
            setFailedState();
        } else {
            APICampaignCaller.searchCampaign(null, "",
                    getActivity().getApplication(), new APIListener() {
                @Override
                public void onCampaignListFound(List<Campaign> campaignList) {
                    if (campaignList.size() > 0) {
                        for (int i = campaignList.size() - 1; i >= 0; i--) {
                            if (campaignList.get(i).getShareFlag()) {
                                campaignList.remove(i);
                            }
                        }
                        MethodUtils.sortSingleCampaign(campaignList);
                        setupCampaignRecView(campaignList, recViewSingleCampaign, layoutSingleCampaign);
                    } else {
                        listCount -= 5;
                        finalizeView();
                    }
                }

                @Override
                public void onFailedAPICall(int code) {
                    listCount -= 5;
                    finalizeView();
                }
            });
        }
    }

    private void getOngoingList() {
        orderCountOngoing = false;
        ratingOngoing = false;
        if (getActivity() == null) {
            setFailedState();
        } else {
            APIProductCaller.getProductsByProductStatus(null, "incampaign",
                    getActivity().getApplication(), new APIListener() {
                @Override
                public void onProductListFound(List<Product> productList) {
                    if (!productList.isEmpty()) {
                        APIListener ongoingListener = new APIListener() {
                            @Override
                            public void onProductOrderCountFound(Map<String, Integer> countList) {
                                for (Product product : productList) {
                                    if (countList.get(product.getProductId()) != null) {
                                        product.setOrderCount(countList.get(product.getProductId()));
                                    } else {
                                        product.setOrderCount(0);
                                    }
                                }
                                orderCountOngoing = true;
                                if (ratingOngoing) {
                                    System.out.println("int order on");
                                    setupProductRecView(productList, recViewOngoing, layoutOngoing);
                                }
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
                                ratingOngoing = true;
                                if (orderCountOngoing) {
                                    System.out.println("int rating on");
                                    setupProductRecView(productList, recViewOngoing, layoutOngoing);
                                }
                            }

                            @Override
                            public void onFailedAPICall(int code) {
                                System.out.println("fail in most");
                                listCount -= 10;
                                finalizeView();
                            }
                        };
                        APIProductCaller.getOrderCountByProductList(productList,
                                getActivity().getApplication(), ongoingListener);
                        APIProductCaller.getRatingByProductIdList(productList,
                                getActivity().getApplication(), ongoingListener);
                    } else {
                        listCount -= 10;
                        finalizeView();
                    }
                }

                @Override
                public void onFailedAPICall(int errorCode) {
                    System.out.println("failed most");
                    listCount -= 10;
                    finalizeView();
                }
            });
        }
    }

    private void getMostOrderedList() {
        orderCountMostOrdered = false;
        ratingMostOrdered = false;
        if (getActivity() == null) {
            setFailedState();
        } else {
            APIProductCaller.getProductsWithCompletedOrders(null,
                    getActivity().getApplication(), new APIListener() {
                @Override
                public void onProductListFound(List<Product> productList) {
                    if (!productList.isEmpty()) {
                        APIListener mostOrderedListener = new APIListener() {
                            @Override
                            public void onProductOrderCountFound(Map<String, Integer> countList) {
                                for (Product product : productList) {
                                    if (countList.get(product.getProductId()) != null) {
                                        product.setOrderCount(countList.get(product.getProductId()));
                                    } else {
                                        product.setOrderCount(0);
                                    }
                                }
                                orderCountMostOrdered = true;
                                if (ratingMostOrdered) {
                                    System.out.println("int order most");
                                    setupProductRecView(productList, recViewMostOrdered, layoutMostOrdered);
                                }
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
                                ratingMostOrdered = true;
                                if (orderCountMostOrdered) {
                                    System.out.println("int rating most");
                                    setupProductRecView(productList, recViewMostOrdered, layoutMostOrdered);
                                }
                            }

                            @Override
                            public void onFailedAPICall(int code) {
                                System.out.println("fail in 3");
                                listCount -= 10;
                                finalizeView();
                            }
                        };
                        APIProductCaller.getOrderCountByProductList(productList,
                                getActivity().getApplication(), mostOrderedListener);
                        APIProductCaller.getRatingByProductIdList(productList,
                                getActivity().getApplication(), mostOrderedListener);
                    } else {
                        listCount -= 10;
                        finalizeView();
                    }
                }

                @Override
                public void onFailedAPICall(int errorCode) {
                    System.out.println("failed 3");
                    listCount -= 10;
                    finalizeView();
                }
            });
        }
    }

    private void getNewList() {
        orderCountNew = false;
        ratingNew = false;
        if (getActivity() == null) {
            setFailedState();
        } else {
            APIProductCaller.getNewProductsCurrentWeek(null,
                    getActivity().getApplication(), new APIListener() {
                @Override
                public void onProductListFound(List<Product> productList) {
                    if (!productList.isEmpty()) {
                        APIListener newListener = new APIListener() {
                            @Override
                            public void onProductOrderCountFound(Map<String, Integer> countList) {
                                for (Product product : productList) {
                                    if (countList.get(product.getProductId()) != null) {
                                        product.setOrderCount(countList.get(product.getProductId()));
                                    } else {
                                        product.setOrderCount(0);
                                    }
                                }
                                orderCountNew = true;
                                if (ratingNew) {
                                    System.out.println("int order new");
                                    setupProductRecView(productList, recViewNew, layoutNew);
                                }
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
                                ratingNew = true;
                                if (orderCountNew) {
                                    System.out.println("int rating new");
                                    setupProductRecView(productList, recViewNew, layoutNew);
                                }
                            }

                            @Override
                            public void onFailedAPICall(int code) {
                                System.out.println("fail in 4");
                                listCount -= 10;
                                finalizeView();
                            }
                        };
                        APIProductCaller.getOrderCountByProductList(productList,
                                getActivity().getApplication(), newListener);
                        APIProductCaller.getRatingByProductIdList(productList,
                                getActivity().getApplication(), newListener);
                    } else {
                        listCount -= 10;
                        finalizeView();
                    }
                }

                @Override
                public void onFailedAPICall(int errorCode) {
                    System.out.println("failed 4");
                    listCount -= 10;
                    finalizeView();
                }
            });
        }
    }

    private void getAllList() {
        orderCountAll = false;
        ratingAll = false;
        if (getActivity() == null) {
            setFailedState();
        } else {
            APIProductCaller.getAllProduct(null,
                    getActivity().getApplication(), new APIListener() {
                @Override
                public void onProductListFound(List<Product> productList) {
                    if (!productList.isEmpty()) {
                        APIListener newListener = new APIListener() {
                            @Override
                            public void onProductOrderCountFound(Map<String, Integer> countList) {
                                for (Product product : productList) {
                                    if (countList.get(product.getProductId()) != null) {
                                        product.setOrderCount(countList.get(product.getProductId()));
                                    } else {
                                        product.setOrderCount(0);
                                    }
                                }
                                orderCountAll = true;
                                if (ratingAll) {
                                    System.out.println("int order all");
                                    setupProductRecView(productList, recViewAll, layoutAll);
                                }
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
                                ratingAll = true;
                                if (orderCountAll) {
                                    System.out.println("int rating all");
                                    setupProductRecView(productList, recViewAll, layoutAll);
                                }
                            }

                            @Override
                            public void onFailedAPICall(int code) {
                                System.out.println("fail in 4");
                                listCount -= 10;
                                finalizeView();
                            }
                        };
                        APIProductCaller.getOrderCountByProductList(productList,
                                getActivity().getApplication(), newListener);
                        APIProductCaller.getRatingByProductIdList(productList,
                                getActivity().getApplication(), newListener);
                    } else {
                        listCount -= 10;
                        finalizeView();
                    }
                }

                @Override
                public void onFailedAPICall(int errorCode) {
                    System.out.println("failed 4");
                    listCount -= 10;
                    finalizeView();
                }
            });
        }
    }

    private void setupProductRecView(List<Product> productList, RecyclerView view, LinearLayout layout) {
        if (productList.size() > 0) {
            productAdapter = new RecViewProductListAdapter(getContext(), getActivity());
            productAdapter.setProductsList(productList);
            view.setAdapter(productAdapter);
            view.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
        }
        listCount -= 10;
        finalizeView();
    }

    private void setupCampaignRecView(List<Campaign> list, RecyclerView view, LinearLayout layout) {
        if (list.size() > 0) {
            campaignAdapter = new RecViewCampaignSearchAdapter(getContext(), getActivity()) {
                @Override
                public void executeOnCampaignSelected(Campaign campaign) {
                    if (userId.isEmpty()) {
                        Intent signInIntent = new Intent(getContext(), SignInActivity.class);
                        signInIntent.putExtra("MAIN_TAB_POSITION", 0);
                        startActivityForResult(signInIntent, IntegerUtils.REQUEST_COMMON);
                    } else {
                        onCampaignSelected(campaign);
                    }
                }
            };
            campaignAdapter.setCampaignList(list);
            view.setAdapter(campaignAdapter);
            view.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            layout.setVisibility(View.VISIBLE);
        } else {
            layout.setVisibility(View.GONE);
        }
        listCount -= 5;
        finalizeView();
    }

    private void finalizeView() {
        if (listCount == 0) {
            if ((layoutSharingCampaign.getVisibility() == View.VISIBLE)
                    || (layoutSingleCampaign.getVisibility() == View.VISIBLE)
                    || (layoutOngoing.getVisibility() == View.VISIBLE)
                    || (layoutMostOrdered.getVisibility() == View.VISIBLE)
                    || (layoutNew.getVisibility() == View.VISIBLE)
                    || (layoutAll.getVisibility() == View.VISIBLE)) {
                setLoadedState();
            } else {
                setFailedState();
            }
        }
    }

    private void onCampaignSelected(Campaign campaign) {
        dialogBoxLoading = new DialogBoxLoading(getActivity());
        dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxLoading.show();
        activeCampaignStatus = false; readyCampaignStatus = false;
        String productId = campaign.getProduct().getProductId();
        APIProductCaller.getProductById(productId, getActivity().getApplication(), new APIListener() {
            @Override
            public void onProductFound(Product product) {
                product.setCampaign(campaign);
                APICampaignCaller.getCampaignListByProductId(productId, "active",
                        null, getActivity().getApplication(), new APIListener() {
                    @Override
                    public void onCampaignListFound(List<Campaign> campaignList) {
                        if (product.getCampaignList() == null
                                || product.getCampaignList().size() == 0) {
                            product.setCampaignList(campaignList);
                        } else if (campaignList.size() > 0) {
                            tempList = product.getCampaignList();
                            for (Campaign campaign : campaignList) {
                                tempList.add(campaign);
                            }
                            product.setCampaignList(tempList);
                        }
                        activeCampaignStatus = true;
                        finishSetup(product);
                    }

                    @Override
                    public void onNoJSONFound() {
                        if (product.getCampaignList() == null
                                || product.getCampaignList().size() == 0) {
                            product.setCampaignList(new ArrayList<>());
                        }
                        activeCampaignStatus = true;
                        finishSetup(product);
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        if (dialogBoxLoading.isShowing()) {
                            dialogBoxLoading.dismiss();
                        }
                        MethodUtils.displayErrorAPIMessage(getActivity());
                    }
                });
                APICampaignCaller.getCampaignListByProductId(productId, "ready",
                        null, getActivity().getApplication(), new APIListener() {
                    @Override
                    public void onCampaignListFound(List<Campaign> campaignList) {
                        if (product.getCampaignList() == null
                                || product.getCampaignList().size() == 0) {
                            product.setCampaignList(campaignList);
                        } else if (campaignList.size() > 0) {
                            tempList = product.getCampaignList();
                            for (Campaign campaign : campaignList) {
                                tempList.add(campaign);
                            }
                            product.setCampaignList(tempList);
                        }
                        readyCampaignStatus = true;
                        finishSetup(product);
                    }

                    @Override
                    public void onNoJSONFound() {
                        if (product.getCampaignList() == null
                                || product.getCampaignList().size() == 0) {
                            product.setCampaignList(new ArrayList<>());
                        }
                        readyCampaignStatus = true;
                        finishSetup(product);
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        if (dialogBoxLoading.isShowing()) {
                            dialogBoxLoading.dismiss();
                        }
                        MethodUtils.displayErrorAPIMessage(getActivity());
                    }
                });
            }

            @Override
            public void onFailedAPICall(int code) {
                if (dialogBoxLoading.isShowing()) {
                    dialogBoxLoading.dismiss();
                }
                MethodUtils.displayErrorAPIMessage(getActivity());
            }
        });
    }

    private void finishSetup(Product product) {
        if (activeCampaignStatus && readyCampaignStatus) {
            Intent campaignSelectIntent
                    = new Intent(getContext(), PrepareProductActivity.class);
            campaignSelectIntent.putExtra("PRODUCT", product);
            if (dialogBoxLoading.isShowing()) {
                dialogBoxLoading.dismiss();
            }
            startActivityForResult(campaignSelectIntent, IntegerUtils.REQUEST_MAKE_PURCHASE);
        }
    }

    private void setLoadingState() {
        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoProductFound.setVisibility(View.INVISIBLE);
        scrollViewHomeFragment.setVisibility(View.INVISIBLE);
        layoutSharingCampaign.setVisibility(View.GONE);
        layoutSingleCampaign.setVisibility(View.GONE);
        layoutOngoing.setVisibility(View.GONE);
        layoutMostOrdered.setVisibility(View.GONE);
        layoutNew.setVisibility(View.GONE);
        layoutAll.setVisibility(View.GONE);
    }

    private void setFailedState() {
        layoutNoProductFound.setVisibility(View.VISIBLE);
        layoutLoading.setVisibility(View.INVISIBLE);
        scrollViewHomeFragment.setVisibility(View.INVISIBLE);
    }

    private void setLoadedState() {
        layoutNoProductFound.setVisibility(View.INVISIBLE);
        layoutLoading.setVisibility(View.INVISIBLE);
        scrollViewHomeFragment.setVisibility(View.VISIBLE);
    }


    private void editCartCountByUser() {
        try {
            retailList = (List<CartProduct>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("RETAIL_CART", ""));
            campaignList = (List<CartProduct>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("CAMPAIGN_CART", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(retailList == null && campaignList == null) {
            cardViewProductDetailCartCount.setVisibility(View.INVISIBLE);
        } else {
            if (retailList.size() > 0 || campaignList.size() > 0) {
                cardViewProductDetailCartCount.setVisibility(View.VISIBLE);
                cartCount = retailList.size() + campaignList.size();
                txtProductDetailCartCount.setText(cartCount + "");
            } else {
                cardViewProductDetailCartCount.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void editUnreadMessageCountByUser() {
        messageLoading = true;
        if (getActivity() == null) {
            setFailedState();
        } else {
            APIChatCaller.getCustomerChatMessages(token, null,
                    getActivity().getApplication(), new APIListener() {
                @Override
                public void onMessageListFound(List<Message> list) {
                    getUnreadMessagesCount(list);
                    if (messageCount == 0) {
                        cardViewMessageCount.setVisibility(View.INVISIBLE);
                    } else {
                        cardViewMessageCount.setVisibility(View.VISIBLE);
                        txtMessageCount.setText(messageCount + "");
                    }
                    messageLoading = false;
                }

                @Override
                public void onFailedAPICall(int code) {
                    if (code == IntegerUtils.ERROR_NO_USER) {
                        MethodUtils.displayErrorAccountMessage(getContext(), getActivity());
                    } else {
                        cardViewMessageCount.setVisibility(View.INVISIBLE);
                        txtNotificationCount.setText(notificationCount + "");
                        messageLoading = false;
                    }
                }
            });
        }
    }

    private void editUnreadNotificationCountByUser() {
        notificationLoading = true;
        if (getActivity() == null) {
            setFailedState();
        } else {
            APINotificationCaller.getUsersNotifications(token,
                    getActivity().getApplication(), new APIListener() {
                @Override
                public void onNotificationListFound(List<Notification> list) {
                    getUnreadNotificationsCount(list);
                    if (notificationCount == 0) {
                        cardViewNotificationCount.setVisibility(View.INVISIBLE);
                    } else {
                        cardViewNotificationCount.setVisibility(View.VISIBLE);
                        txtNotificationCount.setText(notificationCount + "");
                    }
                    notificationLoading = false;
                }

                @Override
                public void onFailedAPICall(int code) {
                    if (code == IntegerUtils.ERROR_NO_USER) {
                        MethodUtils.displayErrorAccountMessage(getContext(), getActivity());
                    } else {
                        cardViewNotificationCount.setVisibility(View.INVISIBLE);
                        notificationLoading = false;
                    }
                }
            });
        }
    }

    private void getUnreadMessagesCount(List<Message> list) {
        messageCount = 0;
        if (list.size() > 0) {
            for (Message message : list) {
                if (message.getToId().equals(accountId) && !message.getMessageRead()) {
                    messageCount++;
                }
            }
        }
    }

    private void getUnreadNotificationsCount(List<Notification> list) {
        notificationCount = 0;
        if (list.size() > 0) {
            for (Notification notification : list) {
                if (!notification.getNotificationRead()) {
                    notificationCount++;
                }
            }
        }
    }

    private void setRealtimeFirebase() {
        firebaseReferences = new FirebaseDatabaseReferences();
        firebaseReferences.getUserMessages(accountId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!messageLoading) {
                    editUnreadMessageCountByUser();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });
        firebaseReferences.getUserNotifications(accountId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (!notificationLoading) {
                    editUnreadNotificationCountByUser();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });
    }
}