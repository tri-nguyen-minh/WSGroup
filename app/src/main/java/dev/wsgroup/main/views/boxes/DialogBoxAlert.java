package dev.wsgroup.main.views.boxes;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.utils.IntegerUtils;

public class DialogBoxAlert extends Dialog implements View.OnClickListener {

    private Button btnConfirmRegister;
    private TextView lblConfirmText, lblDescription;
    private ImageView imgDialogConfirm;
    private String message, description;
    private int actionCode;

    public DialogBoxAlert(Activity activity, int actionCode, String message, String description) {
        super(activity);
        this.actionCode = actionCode;
        this.message = message;
        this.description = description;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_alert);
        btnConfirmRegister = findViewById(R.id.btnConfirmRegister);
        lblConfirmText = findViewById(R.id.lblConfirmText);
        lblDescription = findViewById(R.id.lblDescription);
        imgDialogConfirm = findViewById(R.id.imgDialogConfirm);
        btnConfirmRegister.setOnClickListener(this);
        switch (actionCode) {
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
                imgDialogConfirm.setColorFilter(ContextCompat.getColor(getContext(), R.color.red));
                break;
            }
        }
        if(!message.isEmpty()) {
            lblConfirmText.setVisibility(View.VISIBLE);
            lblConfirmText.setText(message);
        } else {
            lblConfirmText.setVisibility(View.GONE);
        }
        if(!description.isEmpty()) {
            lblDescription.setVisibility(View.VISIBLE);
            lblDescription.setText(description);
        } else {
            lblDescription.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        dismiss();
        onClickAction();
    }

    public void onClickAction() {}
}