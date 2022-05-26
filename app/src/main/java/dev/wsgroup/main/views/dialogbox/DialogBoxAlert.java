package dev.wsgroup.main.views.dialogbox;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.utils.IntegerUtils;

public class DialogBoxAlert extends Dialog {

    private Button btnConfirmRegister;
    private TextView lblConfirmText, lblDescription;
    private ImageView imgDialogConfirm;
    private String message, description;
    private int requestCode;

    public DialogBoxAlert(Activity activity, int requestCode, String message, String description) {
        super(activity);
        this.requestCode = requestCode;
        this.message = message;
        this.description = description;
    }


    public DialogBoxAlert(Activity activity, int requestCode, String message) {
        super(activity);
        this.requestCode = requestCode;
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_alert);
        setCancelable(false);

        btnConfirmRegister = findViewById(R.id.btnConfirmRegister);
        lblConfirmText = findViewById(R.id.lblConfirmText);
        lblDescription = findViewById(R.id.lblDescription);
        imgDialogConfirm = findViewById(R.id.imgDialogConfirm);

        switch (requestCode) {
            case IntegerUtils.CONFIRM_ACTION_CODE_SUCCESS: {
                imgDialogConfirm.setImageResource(R.drawable.ic_check);
                lblDescription.setVisibility(View.GONE);
                imgDialogConfirm.setColorFilter(ContextCompat.getColor(getContext(), R.color.green));
                break;
            }
            case IntegerUtils.CONFIRM_ACTION_CODE_FAILED: {
                imgDialogConfirm.setImageResource(R.drawable.ic_cross);
                imgDialogConfirm.setColorFilter(ContextCompat.getColor(getContext(), R.color.red));
                break;
            }
            case IntegerUtils.CONFIRM_ACTION_CODE_ALERT: {
                imgDialogConfirm.setVisibility(View.GONE);
                break;
            }
        }
        if(!message.isEmpty()) {
            lblConfirmText.setVisibility(View.VISIBLE);
            lblConfirmText.setText(message);
        } else {
            lblConfirmText.setVisibility(View.GONE);
        }
        if(description == null || description.isEmpty()) {
            lblDescription.setVisibility(View.GONE);
        } else {
            lblDescription.setVisibility(View.VISIBLE);
            lblDescription.setText(description);
        }

        btnConfirmRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                onClickAction();
            }
        });
    }

    public void onClickAction() {}
}