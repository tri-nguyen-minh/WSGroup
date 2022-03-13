package dev.wsgroup.main.views.fragments.tabs.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIChatCaller;
import dev.wsgroup.main.models.apis.callers.APIProductCaller;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Message;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewProductListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.ObjectSerializer;
import dev.wsgroup.main.views.activities.CartActivity;
import dev.wsgroup.main.views.activities.account.SignInActivity;
import dev.wsgroup.main.views.activities.message.MessageListActivity;
import dev.wsgroup.main.views.activities.productviews.SearchProductActivity;

public class HomeTab extends Fragment {

    private RelativeLayout layoutLoading;
    private LinearLayout layoutNoProductFound;
    private TextView lblRetryGetProduct, txtProductDetailCartCount, txtMessageCount;
    private CardView cardViewProductDetailCartCount, cardViewMessageCount;
    private ScrollView scrollViewHomeFragment;
    private ConstraintLayout constraintLayoutShoppingCart, constraintLayoutMessage,
            constraintLayoutNotification;
    private TextView editSearchProduct;
    private LinearLayout layoutMostPopularProduct,
            layoutProduct2, layoutProduct3, layoutProduct4;
    private TextView  lblViewMoreMostPopularProduct,
            lblViewMoreProduct2, lblViewMoreProduct3, lblViewMoreProduct4;
    private RecyclerView recViewMostPopularProduct,
            recViewProduct2, recViewProduct3, recViewProduct4;

    private SharedPreferences sharedPreferences;
    private List<CartProduct> retailList, campaignList;
    private List<Message> messageList;
    private String userId;
    private int cartCount, messageCount, listCount;
    private boolean orderCountMostPopular, ratingMostPopular,
                    orderCount2, rating2,
                    orderCount3, rating3,
                    orderCount4, rating4;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        sharedPreferences = getActivity().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
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
        lblViewMoreMostPopularProduct = view.findViewById(R.id.lblViewMoreMostPopularProduct);
        recViewMostPopularProduct = view.findViewById(R.id.recViewMostPopularProduct);
        cardViewProductDetailCartCount = view.findViewById(R.id.cardViewProductDetailCartCount);
        cardViewMessageCount = view.findViewById(R.id.cardViewMessageCount);
        scrollViewHomeFragment = view.findViewById(R.id.scrollViewHomeFragment);
        constraintLayoutShoppingCart = view.findViewById(R.id.constraintLayoutShoppingCart);
        constraintLayoutMessage = view.findViewById(R.id.constraintLayoutMessage);
        constraintLayoutNotification = view.findViewById(R.id.constraintLayoutNotification);
        editSearchProduct = view.findViewById(R.id.editSearchProduct);
        layoutMostPopularProduct = view.findViewById(R.id.layoutMostPopularProduct);
        layoutProduct2 = view.findViewById(R.id.layoutProduct2);
        layoutProduct3 = view.findViewById(R.id.layoutProduct3);
        layoutProduct4 = view.findViewById(R.id.layoutProduct4);
        lblViewMoreMostPopularProduct = view.findViewById(R.id.lblViewMoreMostPopularProduct);
        lblViewMoreProduct2 = view.findViewById(R.id.lblViewMoreProduct2);
        lblViewMoreProduct3 = view.findViewById(R.id.lblViewMoreProduct3);
        lblViewMoreProduct4 = view.findViewById(R.id.lblViewMoreProduct3);
        recViewMostPopularProduct = view.findViewById(R.id.recViewMostPopularProduct);
        recViewProduct2 = view.findViewById(R.id.recViewProduct2);
        recViewProduct3 = view.findViewById(R.id.recViewProduct3);
        recViewProduct4 = view.findViewById(R.id.recViewProduct4);

        cartCount = 0;
        try {
            userId = sharedPreferences.getString("USER_ID", "");
            editCartCountByUser();
            editUnreadMessageByUser();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Set<String> list = new LinkedHashSet<>();
        list.add("3c2623eb-f4ba-4fe9-8f33-c3e604250ab5");
        list.add("1bd7c4d0-80dd-4a33-a229-df536a621aed");
        list.add("1bd7c4d0-80dd-4a33-a229-df536a621aed");
        list.add("b53602fc-fc91-483b-a528-799338cbe13f");
        list.add("b53602fc-fc91-483b-a528-799338cbe13f");
        list.add("b53602fc-fc91-483b-a528-799338cbe13f");
        list.add("84bb4393-be10-406c-93ec-cc92be9a6da0");

//        try {
//            JSONObject jsonObject = new JSONObject();
//            JSONArray jsonArray = new JSONArray();
//            jsonArray.put(list);
//            jsonObject.put("listAccountIds", jsonArray);
//            System.out.println(jsonObject);
//        } catch (Exception e) {
//            System.out.println(e);
//        }

        constraintLayoutNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("testing");
                String token = sharedPreferences.getString("TOKEN","");
                APIChatCaller.updateReadMessages(token, "179989ab-5a63-4bb0-ba53-1c202dfb5340",
                        "9a7ccab9-4974-4322-844d-c07dcb9cb70d", getActivity().getApplication(), new APIListener() {
                });
            }
        });

//        fill product list
        getProductLists();

        constraintLayoutMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userId.isEmpty()) {
                    Intent signInIntent = new Intent(getContext(), SignInActivity.class);
                    startActivityForResult(signInIntent, IntegerUtils.REQUEST_LOGIN_FOR_MESSAGE);
                } else {
                    Intent messageIntent = new Intent(getContext(), MessageListActivity.class);
                    startActivityForResult(messageIntent, IntegerUtils.REQUEST_COMMON);
                }
            }
        });

        constraintLayoutShoppingCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(userId.isEmpty()) {
                    Intent signInIntent = new Intent(getContext(), SignInActivity.class);
                    startActivityForResult(signInIntent, IntegerUtils.REQUEST_LOGIN_FOR_CART);
                } else {
                    Intent cartIntent = new Intent(getContext(), CartActivity.class);
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

        lblViewMoreMostPopularProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        lblViewMoreProduct2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        lblViewMoreProduct3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        lblViewMoreProduct4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void getProductLists() {
        listCount = 40;
        System.out.println(listCount);
        layoutLoading.setVisibility(View.VISIBLE);
        layoutNoProductFound.setVisibility(View.INVISIBLE);
        scrollViewHomeFragment.setVisibility(View.INVISIBLE);
        layoutMostPopularProduct.setVisibility(View.INVISIBLE);
        layoutProduct2.setVisibility(View.INVISIBLE);
        layoutProduct3.setVisibility(View.INVISIBLE);
        layoutProduct4.setVisibility(View.INVISIBLE);
        getMostPopularProductList();
        getProductList2();
        getProductList3();
        getProductList4();
    }

    private void getMostPopularProductList() {
        orderCountMostPopular = false;
        ratingMostPopular = false;
        System.out.println("in most");
        APIProductCaller.getAllProduct(null,
                getActivity().getApplication(), new APIListener() {
            @Override
            public void onProductListFound(List<Product> productList) {
                if(!productList.isEmpty()) {
                    APIListener mostPopularListener = new APIListener() {
                        @Override
                        public void onProductOrderCountFound(Map<String, Integer> countList) {
                            for (Product product : productList) {
                                if (countList.get(product.getProductId()) != null) {
                                    product.setOrderCount(countList.get(product.getProductId()));
                                }else {
                                    product.setOrderCount(0);
                                }
                            }
                            orderCountMostPopular = true;
                            listCount -= 5;
                            setupMostPopularView(productList);
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
                            ratingMostPopular = true;
                            listCount -= 5;
                            setupMostPopularView(productList);
                        }

                        @Override
                        public void onFailedAPICall(int code) {
                            System.out.println("fail in most");
                            listCount -= 10;
                            finalizeView();
                        }
                    };
                    APIProductCaller.getOrderCountByProductList(productList,
                            getActivity().getApplication(), mostPopularListener);
                    APIProductCaller.getRatingByProductIdList(productList,
                            getActivity().getApplication(), mostPopularListener);
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

    private void getProductList2() {
        orderCount2 = false;
        rating2 = false;
        APIProductCaller.getAllProduct(null,
                getActivity().getApplication(), new APIListener() {
            @Override
            public void onProductListFound(List<Product> productList) {
                if(!productList.isEmpty()) {
                    APIListener mostPopularListener = new APIListener() {
                        @Override
                        public void onProductOrderCountFound(Map<String, Integer> countList) {
                            for (Product product : productList) {
                                if (countList.get(product.getProductId()) != null) {
                                    product.setOrderCount(countList.get(product.getProductId()));
                                }else {
                                    product.setOrderCount(0);
                                }
                            }
                            orderCount2 = true;
                            listCount -= 5;
                            setup2View(productList);
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
                            rating2 = true;
                            listCount -= 5;
                            setup2View(productList);
                        }

                        @Override
                        public void onFailedAPICall(int code) {
                            System.out.println("fail in 2");
                            listCount -= 10;
                            finalizeView();
                        }
                    };
                    APIProductCaller.getOrderCountByProductList(productList,
                            getActivity().getApplication(), mostPopularListener);
                    APIProductCaller.getRatingByProductIdList(productList,
                            getActivity().getApplication(), mostPopularListener);
                } else {
                    listCount -= 10;
                    finalizeView();
                }
            }
            @Override
            public void onFailedAPICall(int errorCode) {
                System.out.println("failed 2");
                listCount -= 10;
                finalizeView();
            }
        });
    }

    private void getProductList3() {
        orderCount3 = false;
        rating3 = false;
        APIProductCaller.getAllProduct(null,
                getActivity().getApplication(), new APIListener() {
            @Override
            public void onProductListFound(List<Product> productList) {
                if(!productList.isEmpty()) {
                    APIListener mostPopularListener = new APIListener() {
                        @Override
                        public void onProductOrderCountFound(Map<String, Integer> countList) {
                            for (Product product : productList) {
                                if (countList.get(product.getProductId()) != null) {
                                    product.setOrderCount(countList.get(product.getProductId()));
                                }else {
                                    product.setOrderCount(0);
                                }
                            }
                            orderCount3 = true;
                            listCount -= 5;
                            setup3View(productList);
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
                            rating3 = true;
                            listCount -= 5;
                            setup3View(productList);
                        }

                        @Override
                        public void onFailedAPICall(int code) {
                            System.out.println("fail in 3");
                            listCount -= 10;
                            finalizeView();
                        }
                    };
                    APIProductCaller.getOrderCountByProductList(productList,
                            getActivity().getApplication(), mostPopularListener);
                    APIProductCaller.getRatingByProductIdList(productList,
                            getActivity().getApplication(), mostPopularListener);
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

    private void getProductList4() {
        orderCount4 = false;
        rating4 = false;
        APIProductCaller.getAllProduct(null,
                getActivity().getApplication(), new APIListener() {
            @Override
            public void onProductListFound(List<Product> productList) {
                if(!productList.isEmpty()) {
                    APIListener mostPopularListener = new APIListener() {
                        @Override
                        public void onProductOrderCountFound(Map<String, Integer> countList) {
                            for (Product product : productList) {
                                if (countList.get(product.getProductId()) != null) {
                                    product.setOrderCount(countList.get(product.getProductId()));
                                }else {
                                    product.setOrderCount(0);
                                }
                            }
                            orderCount4 = true;
                            listCount -= 5;
                            setup4View(productList);
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
                            rating4 = true;
                            listCount -= 5;
                            setup4View(productList);
                        }

                        @Override
                        public void onFailedAPICall(int code) {
                            System.out.println("fail in 4");
                            listCount -= 10;
                            finalizeView();
                        }
                    };
                    APIProductCaller.getOrderCountByProductList(productList,
                            getActivity().getApplication(), mostPopularListener);
                    APIProductCaller.getRatingByProductIdList(productList,
                            getActivity().getApplication(), mostPopularListener);
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

    private void setupMostPopularView(List<Product> productList) {
        if (orderCountMostPopular && ratingMostPopular) {
            RecViewProductListAdapter adapter = new RecViewProductListAdapter(getContext(),
                                                                                getActivity());
            adapter.setProductsList(productList);
            recViewMostPopularProduct.setAdapter(adapter);
            recViewMostPopularProduct.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            layoutMostPopularProduct.setVisibility(View.VISIBLE);
        }
        finalizeView();
    }

    private void setup2View(List<Product> productList) {
        if (orderCount2 && rating2) {
            Collections.sort(productList, new Comparator<Product>() {
                @Override
                public int compare(Product product1, Product product2) {
                    return (int) (product1.getRetailPrice() - product2.getRetailPrice());
                }
            });
            RecViewProductListAdapter adapter = new RecViewProductListAdapter(getContext(),
                    getActivity());
            adapter.setProductsList(productList);
            recViewProduct2.setAdapter(adapter);
            recViewProduct2.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            layoutProduct2.setVisibility(View.VISIBLE);
        }
        finalizeView();
    }

    private void setup3View(List<Product> productList) {
        if (orderCount3 && rating3) {
            Collections.sort(productList, new Comparator<Product>() {
                @Override
                public int compare(Product product1, Product product2) {
                    return (int) (product1.getRating() - product2.getRating());
                }
            });
            RecViewProductListAdapter adapter = new RecViewProductListAdapter(getContext(),
                    getActivity());
            adapter.setProductsList(productList);
            recViewProduct3.setAdapter(adapter);
            recViewProduct3.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            layoutProduct3.setVisibility(View.VISIBLE);
        }
        finalizeView();
    }

    private void setup4View(List<Product> productList) {
        if (orderCount4 && rating4) {
            Collections.sort(productList, new Comparator<Product>() {
                @Override
                public int compare(Product product1, Product product2) {
                    return (int) (product1.getQuantity() - product2.getQuantity());
                }
            });
            RecViewProductListAdapter adapter = new RecViewProductListAdapter(getContext(),
                    getActivity());
            adapter.setProductsList(productList);
            recViewProduct4.setAdapter(adapter);
            recViewProduct4.setLayoutManager(new LinearLayoutManager(getContext(),
                    LinearLayoutManager.HORIZONTAL, false));
            layoutProduct4.setVisibility(View.VISIBLE);
        }
        finalizeView();
    }

    private void setupNoProductView() {
        layoutNoProductFound.setVisibility(View.VISIBLE);
        layoutLoading.setVisibility(View.INVISIBLE);
        scrollViewHomeFragment.setVisibility(View.INVISIBLE);
    }

    private void finalizeView() {
        System.out.println("listCount " + listCount);
        if (listCount == 0) {
            if ((layoutMostPopularProduct.getVisibility() == View.VISIBLE)
                    && (layoutProduct2.getVisibility() == View.VISIBLE)
                    && (layoutProduct3.getVisibility() == View.VISIBLE)
                    && (layoutProduct4.getVisibility() == View.VISIBLE)) {
                layoutNoProductFound.setVisibility(View.INVISIBLE);
                layoutLoading.setVisibility(View.INVISIBLE);
                scrollViewHomeFragment.setVisibility(View.VISIBLE);
            } else {
                setupNoProductView();
            }
        }
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

    private void editUnreadMessageByUser() {
        try {
            messageList = (List<Message>) ObjectSerializer
                    .deserialize(sharedPreferences.getString("MESSAGE_LIST", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (messageList == null) {
            cardViewMessageCount.setVisibility(View.INVISIBLE);
        } else {
            if (messageList.size() == 0) {
                cardViewMessageCount.setVisibility(View.INVISIBLE);
            } else {
                messageCount = 0;
                for (Message message : messageList) {
                    if (!message.getMessageRead()) {
                        messageCount++;
                    }
                }
                if (messageCount > 0) {
                    cardViewMessageCount.setVisibility(View.VISIBLE);
                    txtMessageCount.setText(messageCount + "");
                } else {
                    cardViewMessageCount.setVisibility(View.INVISIBLE);
                }
            }
        }
    }
}