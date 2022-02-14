package dev.wsgroup.main.views.dialogbox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.cardview.widget.CardView;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.OrderProduct;
import dev.wsgroup.main.models.utils.IntegerUtils;

public class DialogBoxOrderNote extends Dialog {

    private CardView cardViewParent;
    private ImageView imgCloseDialogBox;
    private EditText editNote;
    private Button btnConfirmNote;

    private int requestCode;
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
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        cardViewParent = findViewById(R.id.cardViewParent);
        imgCloseDialogBox = findViewById(R.id.imgCloseDialogBox);
        editNote = findViewById(R.id.editNote);
        btnConfirmNote = findViewById(R.id.btnConfirmNote);

        editNote.setText(orderProduct.getNote());
        if (requestCode == IntegerUtils.REQUEST_NOTE_READ_ONLY) {
            editNote.setEnabled(false);
            btnConfirmNote.setVisibility(View.GONE);
        } else {
            editNote.setEnabled(true);
            btnConfirmNote.setVisibility(View.VISIBLE);
            btnConfirmNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onConfirmNote(editNote.getText().toString());
                    dismiss();
                }
            });
        }

        imgCloseDialogBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        cardViewParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNote.clearFocus();
            }
        });

        editNote.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onConfirmNote(String note) {}
}
