package dev.wsgroup.main.views.dialogbox;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Discount;
import dev.wsgroup.main.models.utils.MethodUtils;

public class DialogBoxDiscountCard extends Dialog {

    private ImageView imgCloseDialogBox;
    private TextView txtDiscountDescription, txtDiscountCode, txtDiscountPrice, txtDiscountQuantity,
            txtDiscountStartDate, txtDiscountEndDate, txtDiscountMinQuantity, txtDiscountMinPrice;
    private LinearLayout linearLayoutQuantityCondition, linearLayoutPriceCondition;
    private Button btnSaveDiscount;

    private Discount discount;
    private String userId;

    public DialogBoxDiscountCard(Activity activity, Discount discount, String userId) {
        super(activity);
        this.discount = discount;
        this.userId = userId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_discount_single);
        setCancelable(false);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgCloseDialogBox = findViewById(R.id.imgCloseDialogBox);
        txtDiscountDescription = findViewById(R.id.txtDiscountDescription);
        txtDiscountCode = findViewById(R.id.txtDiscountCode);
        txtDiscountPrice = findViewById(R.id.txtDiscountPrice);
        txtDiscountQuantity = findViewById(R.id.txtDiscountQuantity);

        txtDiscountEndDate = findViewById(R.id.txtDiscountEndDate);
        txtDiscountMinQuantity = findViewById(R.id.txtDiscountMinQuantity);
        txtDiscountMinPrice = findViewById(R.id.txtDiscountMinPrice);
//        linearLayoutNoCondition = findViewById(R.id.linearLayoutNoCondition);
        linearLayoutQuantityCondition = findViewById(R.id.linearLayoutQuantityCondition);
        linearLayoutPriceCondition = findViewById(R.id.linearLayoutPriceCondition);
        btnSaveDiscount = findViewById(R.id.btnSaveDiscount);

        txtDiscountDescription.setText(discount.getDescription());
        txtDiscountCode.setText(discount.getCode());
        txtDiscountPrice.setText(MethodUtils.formatPriceString(discount.getDiscountPrice()));
        txtDiscountQuantity.setText(discount.getQuantity() + "");

        imgCloseDialogBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        txtDiscountEndDate.setText(discount.getEndDate());
//        if(discount.getMinPrice() == 0 && discount.getMinQuantity() == 0) {
////            linearLayoutNoCondition.setVisibility(View.VISIBLE);
//            linearLayoutQuantityCondition.setVisibility(View.GONE);
//            linearLayoutPriceCondition.setVisibility(View.GONE);
//        } else {
////            linearLayoutNoCondition.setVisibility(View.GONE);
//            if(discount.getMinQuantity() > 0) {
//                txtDiscountMinQuantity.setText(discount.getMinQuantity() + "");
//                linearLayoutQuantityCondition.setVisibility(View.VISIBLE);
//            } else {
//                linearLayoutQuantityCondition.setVisibility(View.GONE);
//            }
//            if(discount.getMinPrice() > 0) {
//                txtDiscountMinPrice.setText(MethodUtils.formatPriceString(discount.getMinPrice()));
//                linearLayoutPriceCondition.setVisibility(View.VISIBLE);
//            } else {
//                linearLayoutPriceCondition.setVisibility(View.GONE);
//            }
//        }
        if(discount.getStatus()) {
            btnSaveDiscount.setEnabled(true);
            btnSaveDiscount.setText("SAVE DISCOUNT");
        } else {
            btnSaveDiscount.setEnabled(false);
            btnSaveDiscount.setText("DISCOUNT SAVED");
        }
        btnSaveDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}
