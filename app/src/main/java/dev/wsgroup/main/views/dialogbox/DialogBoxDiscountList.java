package dev.wsgroup.main.views.dialogbox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.CustomerDiscount;
import dev.wsgroup.main.models.recycleViewAdapters.RecViewDiscountListAdapter;
import dev.wsgroup.main.models.utils.IntegerUtils;

public class DialogBoxDiscountList extends Dialog {

    private ImageView imgCloseDialogBox;
    private RecyclerView recViewDiscountList;

    private Context context;
    private List<CustomerDiscount> customerDiscountList;
    private int identifier;

    public DialogBoxDiscountList(Activity activity, Context context,
                                 List<CustomerDiscount> customerDiscountList, int identifier) {
        super(activity);
        this.context = context;
        this.customerDiscountList = customerDiscountList;
        this.identifier = identifier;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_discount_list);
        setCancelable(false);

        imgCloseDialogBox = findViewById(R.id.imgCloseDialogBox);
        recViewDiscountList = findViewById(R.id.recViewDiscountList);
        imgCloseDialogBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        RecViewDiscountListAdapter adapter = new RecViewDiscountListAdapter(context, identifier) {
            @Override
            public void onDiscountSelected(CustomerDiscount discount) {
                dismiss();
                setSelectedDiscount(discount);
            }
        };
        adapter.setDiscountList(customerDiscountList);
        recViewDiscountList.setAdapter(adapter);
        recViewDiscountList.setLayoutManager(new LinearLayoutManager(context,
                LinearLayoutManager.VERTICAL, false));

    }

    public void setSelectedDiscount(CustomerDiscount discount) {}
}
