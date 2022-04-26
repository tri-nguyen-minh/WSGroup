package dev.wsgroup.main.views.dialogbox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.utils.MethodUtils;

public class DialogBoxSetupPayment extends Dialog {

    private ImageView imgCloseDialogBox;
    private TextView txtPaymentPrice;
    private Spinner spinnerBank;
    private Button btnConfirm;

    private Context context;
    private double price;
    private String bankString;
    private final String[] bankData = {"NCBank ", "SCBank ",
                                        "Dong A Bank ", "SacomBank ",
                                        "TPBank ", "OceanBank ",
                                        "BIDV ", "Techcombank ",
                                        "VPBank ", "MBBank ",
                                        "ACBank ", "OCBank "};

    public DialogBoxSetupPayment(Activity activity, Context context, double price) {
        super(activity);
        this.context = context;
        this.price = price;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_setup_payment);
        setCancelable(false);

        imgCloseDialogBox = findViewById(R.id.imgCloseDialogBox);
        txtPaymentPrice = findViewById(R.id.txtPaymentPrice);
        spinnerBank = findViewById(R.id.spinnerBank);
        btnConfirm = findViewById(R.id.btnConfirm);

        txtPaymentPrice.setText(MethodUtils.formatPriceString(price));
        ArrayAdapter adapter =
                new ArrayAdapter<>(context, R.layout.spinner_selected_right, bankData);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_right);

        spinnerBank.setAdapter(adapter);
        spinnerBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int positionInt, long positionLong) {
                if (positionInt == 0) {
                    bankString = "NCB";
                } else {
                    bankString = bankData[positionInt];
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerBank.setSelection(0);

        imgCloseDialogBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                onCancel();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                onBankSelected(bankString);
            }
        });
    }

    public void onBankSelected(String bank) {}

    public void onCancel() {}
}
