package dev.wsgroup.main.views.dialogbox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Campaign;

public class DialogBoxSelectImage extends Dialog {

    private ImageView imgCloseDialogBox;
    private Button btnTakePhoto, btnSelectPhoto;

    public DialogBoxSelectImage(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_select_image);

        imgCloseDialogBox = findViewById(R.id.imgCloseDialogBox);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto);

        imgCloseDialogBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                executeTakePhoto();
            }
        });
        btnSelectPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                executeSelectPhoto();
            }
        });
    }

    public void executeTakePhoto() {}

    public void executeSelectPhoto() {}
}
