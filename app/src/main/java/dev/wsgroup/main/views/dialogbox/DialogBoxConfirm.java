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
    private TextView txtConfirmContent;

    private String message;

    public DialogBoxConfirm(Activity activity, String message) {
        super(activity);
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_confirm);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnConfirm = findViewById(R.id.btnYes);
        btnCancel = findViewById(R.id.btnNo);
        txtConfirmContent = findViewById(R.id.txtConfirmContent);
        txtConfirmContent.setText(message);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onYesClicked();
                dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void onYesClicked() {}
}
