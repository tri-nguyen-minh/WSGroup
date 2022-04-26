package dev.wsgroup.main.models.recycleViewAdapters;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.apis.APIListener;
import dev.wsgroup.main.models.apis.callers.APIDiscountCaller;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CampaignMilestone;
import dev.wsgroup.main.models.dtos.CustomerDiscount;
import dev.wsgroup.main.models.dtos.Discount;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import dev.wsgroup.main.views.dialogbox.DialogBoxAlert;
import dev.wsgroup.main.views.dialogbox.DialogBoxDiscountList;
import dev.wsgroup.main.views.dialogbox.DialogBoxLoading;

public class RecViewOrderSupplierListAdapter
        extends RecyclerView.Adapter<RecViewOrderSupplierListAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private SharedPreferences sharedPreferences;
    private String token;
    private int requestState;
    private List<Order> orderList;
    private List<OrderProduct> orderProductList;
    private List<CustomerDiscount> markedForDeleteList, customerDiscountList;
    private double price;
    private DialogBoxLoading dialogBoxLoading;

    public RecViewOrderSupplierListAdapter(Context context, Activity activity, int requestState) {
        this.context = context;
        this.activity = activity;
        this.requestState = requestState;
        sharedPreferences = activity.getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE);
        token = sharedPreferences.getString("TOKEN", "");
    }

    public void setList(List<Order> orderList) {
        this.orderList = orderList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_view_order_supplier_list, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.txtRecViewOrderSupplierName.setText(orderList.get(position).getSupplier().getName());
        holder.lblTotalPrice.setText("Total Order Price");
        holder.lblNoDiscount.setText("No Discount");
        setupDiscountLayout(holder, position);
        price = getTotalPrice(position);
        holder.txtTotalPrice.setText(MethodUtils.formatPriceString(price));
        RecViewOrderProductListAdapter adapter
                = new RecViewOrderProductListAdapter(context, activity, requestState);
        adapter.setOrder(orderList.get(position));
        holder.recViewOrderProductList.setAdapter(adapter);
        holder.recViewOrderProductList.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false));
        holder.layoutParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.editDiscount.hasFocus()) {
                    holder.editDiscount.clearFocus();
                }
            }
        });
    }

    private void setupDiscountLayout(ViewHolder holder, int position) {
        if (requestState == IntegerUtils.REQUEST_ORDER_RETAIL) {
            holder.layoutDiscount.setVisibility(View.VISIBLE);
            holder.lblTotalPrice.setVisibility(View.GONE);
            holder.layoutDiscountPrice.setVisibility(View.GONE);
            holder.cardViewMoreDiscount.setVisibility(View.VISIBLE);
            holder.progressBarLoading.setVisibility(View.INVISIBLE);
            setupNoDiscountState(holder);
            holder.cardViewMoreDiscount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadDiscountList(position, holder);
                }
            });
            holder.editDiscount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean focus) {
                    if (!focus) {
                        MethodUtils.hideKeyboard(view, context);
                        String code = holder.editDiscount.getText().toString();
                        if (code.isEmpty()) {
                            setupNoDiscountState(holder);
                            price = getTotalPrice(position);
                            holder.txtTotalPrice.setText(MethodUtils.formatPriceString(price));
                            setCustomerDiscount(position, null);
                        } else {
                            setupLoadDiscountState(holder);
                            APIDiscountCaller.getCustomerDiscountByDiscountCode(token,
                                    orderList.get(position).getSupplier().getId(), code,
                                    activity.getApplication(), new APIListener() {
                                @Override
                                public void onDiscountListFound(List<CustomerDiscount> discountList) {
                                    if (discountList.size() > 0) {
                                        checkUnwantedDiscount(orderList.get(position), discountList);
                                    }
                                    if (discountList.size() == 0) {
                                        setupNoDiscountState(holder);
                                        price = getTotalPrice(position);
                                        holder.txtTotalPrice.setText(MethodUtils.formatPriceString(price));
                                        setCustomerDiscount(position, null);
                                    } else {
                                        CustomerDiscount discount = discountList.get(0);
                                        setupDiscountFoundState(holder);
                                        applyDiscount(holder, discount.getDiscount(), position);
                                        setCustomerDiscount(position, discount);
                                    }
                                }

                                @Override
                                public void onFailedAPICall(int code) {
                                    if (code == IntegerUtils.ERROR_NO_USER) {
                                        MethodUtils.displayErrorAccountMessage(context, activity);
                                    } else {
                                        setupNoDiscountState(holder);
                                        price = getTotalPrice(position);
                                        holder.txtTotalPrice.setText(MethodUtils.formatPriceString(price));
                                        setCustomerDiscount(position, null);
                                    }
                                }
                            });
                        }
                    }
                }
            });
        } else {
            orderProductList = orderList.get(position).getOrderProductList();
            holder.layoutDiscount.setVisibility(View.GONE);
            holder.lblTotalPrice.setVisibility(View.VISIBLE);
            OrderProduct orderProduct = orderProductList.get(0);
            Campaign campaign = orderProduct.getCampaign();
            price = orderProduct.getProduct().getRetailPrice();
            double totalPrice = orderProduct.getCampaign().getPrice();
            if (campaign.getShareFlag()) {
                CampaignMilestone milestone = MethodUtils.getReachedCampaignMilestone(campaign.getMilestoneList(),
                        orderProduct.getQuantity() + campaign.getQuantityCount());
                if (milestone != null) {
                    totalPrice = milestone.getPrice();
                }
            }
            price -= totalPrice;
            price *= orderProduct.getQuantity();
            holder.txtDiscountPrice.setVisibility(View.VISIBLE);
            holder.txtDiscountPrice.setText(MethodUtils.formatPriceString(price));
        }
    }

    private double getTotalPrice(int position) {
        double totalPrice = 0;
        orderProductList = orderList.get(position).getOrderProductList();
        if (requestState == IntegerUtils.REQUEST_ORDER_RETAIL) {
            for (OrderProduct orderProduct : orderProductList) {
                totalPrice += (orderProduct.getProduct().getRetailPrice() * orderProduct.getQuantity());
            }
        } else {
            OrderProduct orderProduct = orderProductList.get(0);
            Campaign campaign = orderProduct.getCampaign();
            totalPrice = orderProduct.getCampaign().getPrice();
            if (campaign.getShareFlag()) {
                CampaignMilestone milestone = MethodUtils.getReachedCampaignMilestone(campaign.getMilestoneList(),
                        orderProduct.getQuantity() + campaign.getQuantityCount());
                if (milestone != null) {
                    totalPrice = milestone.getPrice();
                }
            }
            totalPrice *= orderProduct.getQuantity();
        }
        return totalPrice;
    }

    private void setupLoadDiscountState(ViewHolder holder) {
        holder.progressBarLoading.setVisibility(View.VISIBLE);
        holder.cardViewMoreDiscount.setVisibility(View.INVISIBLE);
        holder.layoutDiscountPrice.setVisibility(View.INVISIBLE);
        holder.lblNoDiscount.setVisibility(View.INVISIBLE);
    }

    private void setupDiscountFoundState(ViewHolder holder) {
        holder.progressBarLoading.setVisibility(View.INVISIBLE);
        holder.cardViewMoreDiscount.setVisibility(View.VISIBLE);
        holder.layoutDiscountPrice.setVisibility(View.VISIBLE);
        holder.lblNoDiscount.setText("Discount Found!");
        holder.lblNoDiscount.setVisibility(View.VISIBLE);
    }

    private void setupNoDiscountState(ViewHolder holder) {
        holder.progressBarLoading.setVisibility(View.INVISIBLE);
        holder.cardViewMoreDiscount.setVisibility(View.VISIBLE);
        holder.layoutDiscountPrice.setVisibility(View.INVISIBLE);
        holder.lblNoDiscount.setText("No Applicable Discount!");
        holder.lblNoDiscount.setVisibility(View.VISIBLE);
    }

    private void loadDiscountList(int position, ViewHolder holder) {
        dialogBoxLoading = new DialogBoxLoading(activity);
        dialogBoxLoading.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxLoading.show();
        Order order = orderList.get(position);
        APIDiscountCaller.getCustomerDiscountByCondition(token, order.getOrderProductList(),
                order.getTotalPrice(), order.getSupplier().getId(), null,
                activity.getApplication(), new APIListener() {
                    @Override
                    public void onDiscountListFound(List<CustomerDiscount> discountList) {
                        if (dialogBoxLoading.isShowing()) {
                            dialogBoxLoading.dismiss();
                        }
                        if (discountList.size() > 0) {
                            checkUnwantedDiscount(order, discountList);
                        }
                        if (discountList.size() == 0) {
                            displayNoDiscountAlert();
                        } else {
                            customerDiscountList = discountList;
                            DialogBoxDiscountList dialogBox = new DialogBoxDiscountList( activity,
                                    context, customerDiscountList,
                                    IntegerUtils.IDENTIFIER_DISCOUNT_SELECT) {
                                @Override
                                public void setSelectedDiscount(CustomerDiscount discount) {
                                    setupDiscountFoundState(holder);
                                    applyDiscount(holder, discount.getDiscount(), position);
                                    setCustomerDiscount(position, discount);
                                }
                            };
                            dialogBox.getWindow()
                                     .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialogBox.show();
                        }
                    }

                    @Override
                    public void onFailedAPICall(int code) {
                        if (dialogBoxLoading.isShowing()) {
                            dialogBoxLoading.dismiss();
                        }
                        if (code == IntegerUtils.ERROR_NO_USER) {
                            MethodUtils.displayErrorAccountMessage(context, activity);
                        } else {
                            displayNoDiscountAlert();
                        }
                    }
                });
    }

    private void checkUnwantedDiscount(Order order, List<CustomerDiscount> discountList) {
        markedForDeleteList = new ArrayList<>();
        for (CustomerDiscount customerDiscount : discountList) {
            if (checkValidDiscount(order, customerDiscount.getDiscount()) == 0) {
                markedForDeleteList.add(customerDiscount);
            }
        }
        if (markedForDeleteList.size() > 0) {
            for (CustomerDiscount customerDiscount : markedForDeleteList) {
                discountList.remove(customerDiscount);
            }
        }
    }

    private int checkValidDiscount(Order order, Discount discount) {
        int count = 0;
        if (discount.getMinPrice() == 0 && !discount.getProductId().equals("null")) {
            for (OrderProduct orderProduct : order.getOrderProductList()) {
                if (orderProduct.getProduct().getProductId().equals(discount.getProductId())) {
                    count++;
                }
            }
        } else if (discount.getMinPrice() > 0 && discount.getProductId().equals("null")) {
            if (order.getTotalPrice() >= discount.getMinPrice()) {
                count = 1;
            }
        } else {
            if (order.getTotalPrice() >= discount.getMinPrice()) {
                for (OrderProduct orderProduct : order.getOrderProductList()) {
                    if (orderProduct.getProduct().getProductId().equals(discount.getProductId())) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private void applyDiscount(ViewHolder holder, Discount discount, int position) {
        price = getTotalPrice(position);
        double discountPrice = discount.getDiscountPrice();
        holder.editDiscount.setText(discount.getCode());
        holder.lblNoDiscount.setText(discount.getDescription());
        holder.txtDiscountPrice.setText(MethodUtils.formatPriceString(discountPrice));
        price -= discountPrice;
        holder.txtTotalPrice.setText(MethodUtils.formatPriceString(price));
    }

    private void displayNoDiscountAlert() {
        DialogBoxAlert dialogBoxAlert = new DialogBoxAlert(activity,
                IntegerUtils.CONFIRM_ACTION_CODE_FAILED, StringUtils.MES_ERROR_NO_DISCOUNT,"");
        dialogBoxAlert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogBoxAlert.show();
    }

    public void setCustomerDiscount(int position, CustomerDiscount customerDiscount) {}

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtRecViewOrderSupplierName, txtTotalPrice,
                        txtDiscountPrice, lblNoDiscount, lblTotalPrice;
        private RecyclerView recViewOrderProductList;
        private LinearLayout layoutDiscount, layoutDiscountPrice, layoutParent;
        private EditText editDiscount;
        private ProgressBar progressBarLoading;
        private CardView cardViewMoreDiscount;

        public ViewHolder(View view) {
            super(view);
            txtRecViewOrderSupplierName = view.findViewById(R.id.txtRecViewOrderSupplierName);
            txtTotalPrice = view.findViewById(R.id.txtTotalPrice);
            txtDiscountPrice = view.findViewById(R.id.txtDiscountPrice);
            lblNoDiscount = view.findViewById(R.id.lblNoDiscount);
            lblTotalPrice = view.findViewById(R.id.lblTotalPrice);
            recViewOrderProductList = view.findViewById(R.id.recViewOrderProductList);
            layoutDiscount = view.findViewById(R.id.layoutDiscount);
            layoutDiscountPrice = view.findViewById(R.id.layoutDiscountPrice);
            layoutParent = view.findViewById(R.id.layoutParent);
            editDiscount = view.findViewById(R.id.editDiscount);
            progressBarLoading = view.findViewById(R.id.progressBarLoading);
            cardViewMoreDiscount = view.findViewById(R.id.cardViewMoreDiscount);
        }
    }
}
