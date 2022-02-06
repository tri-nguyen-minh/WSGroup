package dev.wsgroup.main.views.boxes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Discount;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewDiscountAdapter;

public class DialogBoxDiscount extends Dialog {

    private TextView txtDiscountCount;
    private ImageView imgCloseDialogBox;
    private RecyclerView recViewDiscount;

    private String userId;
    private Activity activity;
    private Context context;
    private List<Discount> discountList;

    public DialogBoxDiscount(Activity activity, Context context, String userId, List<Discount> discountList) {
        super(activity);
        this.userId = userId;
        this.activity = activity;
        this.context = context;
        this.discountList = discountList;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_discount);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        txtDiscountCount = findViewById(R.id.txtDiscountCount);
        imgCloseDialogBox = findViewById(R.id.imgCloseDialogBox);
        recViewDiscount = findViewById(R.id.recViewDiscount);


        txtDiscountCount.setText(discountList.size() + "");

        imgCloseDialogBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        RecViewDiscountAdapter adapter = new RecViewDiscountAdapter(context, activity);
        adapter.setDiscountList(discountList);
        recViewDiscount.setAdapter(adapter);
        recViewDiscount.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

    }
}
