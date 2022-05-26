package dev.wsgroup.main.views.dialogbox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.dtos.Review;
import dev.wsgroup.main.models.utils.MethodUtils;
import dev.wsgroup.main.models.utils.StringUtils;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class DialogBoxReview extends Dialog {

    private EditText editReviewRating, editReview;
    private MaterialRatingBar ratingReview;
    private Button btnConfirmReview;
    private ImageView imgCloseDialogBox;
    private TextView txtLetterCount, lblLetterSeparator, lblLetterCount, txtDate;
    private LinearLayout layoutLetterCount, layoutDate;

    private OrderProduct orderProduct;
    private Review review;
    private Activity activity;
    private Context context;
    private int letterCount;

    public DialogBoxReview(Activity activity, Context context, OrderProduct orderProduct) {
        super(activity);
        this.activity = activity;
        this.context = context;
        this.orderProduct = orderProduct;
        if (orderProduct != null) {
            review = orderProduct.getReview();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_review);
        setCancelable(false);

        editReviewRating = findViewById(R.id.editReviewRating);
        editReview = findViewById(R.id.editReview);
        ratingReview = findViewById(R.id.ratingReview);
        btnConfirmReview = findViewById(R.id.btnConfirmReview);
        imgCloseDialogBox = findViewById(R.id.imgCloseDialogBox);
        txtLetterCount = findViewById(R.id.txtLetterCount);
        lblLetterSeparator = findViewById(R.id.lblLetterSeparator);
        lblLetterCount = findViewById(R.id.lblLetterCount);
        txtDate = findViewById(R.id.txtDate);
        layoutLetterCount = findViewById(R.id.layoutLetterCount);
        layoutDate = findViewById(R.id.layoutDate);

        editReviewRating.setEnabled(false);

        View.OnClickListener closeListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        };

        if (review == null) {
            letterCount = 0;
            layoutLetterCount.setVisibility(View.VISIBLE);
            layoutDate.setVisibility(View.GONE);
            editReview.setEnabled(true);
            editReview.setText("");
            ratingReview.setIsIndicator(false);
            btnConfirmReview.setText("SUBMIT");
            checkLetterCount();
            enablingButton();

            ratingReview.setOnRatingChangeListener(new MaterialRatingBar.OnRatingChangeListener() {
                @Override
                public void onRatingChanged(MaterialRatingBar ratingBar, float barRating) {
                    editReviewRating.setText(barRating + "");
                    enablingButton();
                }
            });
            editReview.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    letterCount = charSequence.length();
                    checkLetterCount();
                    enablingButton();

                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            btnConfirmReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Review review = new Review();
                    review.setRating(ratingReview.getRating());
                    review.setReview(editReview.getText().toString());
                    review.setProductId(orderProduct.getProduct().getProductId());
                    DialogBoxConfirm dialogBoxConfirm = new DialogBoxConfirm(activity,
                            StringUtils.MES_CONFIRM_ADD_REVIEW) {
                        @Override
                        public void onYesClicked() {
                            DialogBoxReview.this.dismiss();
                            onConfirmReview(review);
                        }
                    };
                    dialogBoxConfirm.setDescription(StringUtils.MES_ALERT_ADD_REVIEW_DESCRIPTION);
                    dialogBoxConfirm.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialogBoxConfirm.show();
                }
            });
        } else {
            layoutLetterCount.setVisibility(View.GONE);
            layoutDate.setVisibility(View.VISIBLE);
            editReview.setEnabled(false);
            ratingReview.setIsIndicator(true);
            btnConfirmReview.setText("CLOSE");
            btnConfirmReview.setOnClickListener(closeListener);
            ratingReview.setRating(((float) review.getRating()));
            editReviewRating.setText(((float) review.getRating()) + "");
            editReview.setText(review.getReview());
            txtDate.setText(MethodUtils.formatDate(review.getDate(), true));
        }
        imgCloseDialogBox.setOnClickListener(closeListener);
    }

    private boolean checkValidReview() {
        if (!editReview.getText().toString().isEmpty() && ratingReview.getRating() > 0) {
            return true;
        }
        return false;
    }

    private void enablingButton() {
        if (checkValidReview()) {
            btnConfirmReview.setEnabled(true);
            btnConfirmReview.getBackground().setTint(context.getResources().getColor(R.color.blue_main));
        } else {
            btnConfirmReview.setEnabled(false);
            btnConfirmReview.getBackground().setTint(context.getResources().getColor(R.color.gray_light));
        }
    }

    private void checkLetterCount() {
        txtLetterCount.setText(letterCount + "");
        if (letterCount == 200) {
            txtLetterCount.setTextColor(context.getResources().getColor(R.color.red));
            lblLetterSeparator.setTextColor(context.getResources().getColor(R.color.red));
            lblLetterCount.setTextColor(context.getResources().getColor(R.color.red));
        } else {
            txtLetterCount.setTextColor(context.getResources().getColor(R.color.gray));
            lblLetterSeparator.setTextColor(context.getResources().getColor(R.color.gray));
            lblLetterCount.setTextColor(context.getResources().getColor(R.color.gray));
        }
    }

    public void onConfirmReview(Review review) { }
}
