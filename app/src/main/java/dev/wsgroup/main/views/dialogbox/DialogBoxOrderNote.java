package dev.wsgroup.main.views.dialogbox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.utils.IntegerUtils;
import dev.wsgroup.main.models.utils.MethodUtils;

public class DialogBoxOrderNote extends Dialog {

    private CardView cardViewParent;
    private ImageView imgCloseDialogBox;
    private EditText editNote;
    private Button btnConfirmNote;
    private LinearLayout layoutLetterCount;
    private TextView txtLetterCount, lblLetterSeparator, lblLetterCount;

    private int requestCode, letterCount;
    private Context context;
    private OrderProduct orderProduct;

    public DialogBoxOrderNote(Activity activity, Context context, OrderProduct orderProduct, int requestCode) {
        super(activity);
        this.context = context;
        this.requestCode = requestCode;
        this.orderProduct = orderProduct;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_order_note);
        setCancelable(false);

        cardViewParent = findViewById(R.id.cardViewParent);
        imgCloseDialogBox = findViewById(R.id.imgCloseDialogBox);
        editNote = findViewById(R.id.editNote);
        btnConfirmNote = findViewById(R.id.btnConfirmNote);
        layoutLetterCount = findViewById(R.id.layoutLetterCount);
        txtLetterCount = findViewById(R.id.txtLetterCount);
        lblLetterSeparator = findViewById(R.id.lblLetterSeparator);
        lblLetterCount = findViewById(R.id.lblLetterCount);


        if (requestCode == IntegerUtils.REQUEST_NOTE_READ_ONLY) {
            editNote.setEnabled(false);
            if (orderProduct.getNote().equals("null")) {
                editNote.setText("");
            } else {
                editNote.setText(orderProduct.getNote());
            }
            layoutLetterCount.setVisibility(View.GONE);
        } else {
            letterCount = 0;
            checkLetterCount();
            editNote.setEnabled(true);
            editNote.setText("");
            layoutLetterCount.setVisibility(View.VISIBLE);
            btnConfirmNote.setVisibility(View.VISIBLE);
        }
        btnConfirmNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (requestCode != IntegerUtils.REQUEST_NOTE_READ_ONLY) {
                    onConfirmNote(editNote.getText().toString());
                }
                dismiss();
            }
        });

        imgCloseDialogBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        cardViewParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editNote.hasFocus()) {
                    editNote.clearFocus();
                }
            }
        });

        editNote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    MethodUtils.hideKeyboard(view, context);
                }
            }
        });
        editNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                letterCount = charSequence.length();
                checkLetterCount();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
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

    public void onConfirmNote(String note) {}
}
