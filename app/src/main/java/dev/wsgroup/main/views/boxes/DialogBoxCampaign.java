package dev.wsgroup.main.views.boxes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Campaign;
import dev.wsgroup.main.models.dtos.CartProduct;
import dev.wsgroup.main.models.dtos.Product;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewCampaignListAdapter;
import dev.wsgroup.main.models.utils.MethodUtils;

public class DialogBoxCampaign extends Dialog {

    private ConstraintLayout layoutCampaign;
    private ImageView imgCloseDialogBox;
    private TextView txtCampaignPrice, txtDiscountEndDate, txtCampaignOrderCount,
                    txtCampaignQuantityCount, txtCampaignQuantityBar,
                    lblProductOrderCount, lblCampaignQuantitySeparator;
    private ProgressBar progressBarQuantityCount;

    private Activity activity;
    private Context context;
    private Product product;
    private Campaign campaign;

    public DialogBoxCampaign(Activity activity, Context context, Product product) {
        super(activity);
        this.activity = activity;
        this.context = context;
        this.product = product;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_campaign);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        layoutCampaign = findViewById(R.id.layoutCampaign);
        imgCloseDialogBox = findViewById(R.id.imgCloseDialogBox);
        txtCampaignPrice = findViewById(R.id.txtCampaignPrice);
        txtDiscountEndDate = findViewById(R.id.txtDiscountEndDate);
        txtCampaignOrderCount = findViewById(R.id.txtCampaignOrderCount);
        txtCampaignQuantityCount = findViewById(R.id.txtCampaignQuantityCount);
        txtCampaignQuantityBar = findViewById(R.id.txtCampaignQuantityBar);
        lblProductOrderCount = findViewById(R.id.lblProductOrderCount);
        lblCampaignQuantitySeparator = findViewById(R.id.lblCampaignQuantitySeparator);
        progressBarQuantityCount = findViewById(R.id.progressBarQuantityCount);

        campaign = product.getCampaign();

        txtCampaignPrice.setText(MethodUtils.convertPriceString(campaign.getPrice()));
        txtDiscountEndDate.setText(MethodUtils.formatDate(campaign.getEndDate()));
        txtCampaignOrderCount.setText(campaign.getOrderCount() + "");
        txtCampaignQuantityCount.setText(campaign.getQuantityCount() + "");
        txtCampaignQuantityBar.setText(campaign.getQuantity() + "");
        if (campaign.getOrderCount() > 1) {
            lblProductOrderCount.setText("waiting orders");
        } else {
            lblProductOrderCount.setText("waiting order");
        }
        lblCampaignQuantitySeparator.setText("/");
        progressBarQuantityCount.setMax(campaign.getQuantity());
        progressBarQuantityCount.setProgress(campaign.getQuantityCount());
        layoutCampaign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeOnCampaignSelectedOnDialog(campaign);
                dismiss();
            }
        });

        imgCloseDialogBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void executeOnCampaignSelectedOnDialog(Campaign campaign) {}
}
