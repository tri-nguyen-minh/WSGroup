package dev.wsgroup.main.views.dialogbox;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import dev.wsgroup.main.R;

public class DialogBoxConfirm extends Dialog {

    private Button btnConfirm, btnCancel;
    private TextView txtConfirmContent, txtConfirmDescription;

    private String message, description;

    public DialogBoxConfirm(Activity activity, String message) {
        super(activity);
        this.message = message;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_confirm);
        setCancelable(false);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnConfirm = findViewById(R.id.btnYes);
        btnCancel = findViewById(R.id.btnNo);
        txtConfirmContent = findViewById(R.id.txtConfirmContent);
        txtConfirmDescription = findViewById(R.id.txtConfirmDescription);

        txtConfirmDescription.setVisibility(View.GONE);

        txtConfirmContent.setText(message);
        if (description != null) {
            if (!description.isEmpty()) {
                txtConfirmDescription.setVisibility(View.VISIBLE);
                txtConfirmDescription.setText(description);
            }
        }
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                onYesClicked();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    public void onYesClicked() {}
}
