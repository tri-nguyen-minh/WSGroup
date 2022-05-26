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

import androidx.cardview.widget.CardView;

import dev.wsgroup.main.R;
import dev.wsgroup.main.models.dtos.Campaign;

public class DialogBoxSelectImage extends Dialog {

    private ImageView imgCloseDialogBox;
    private CardView cardViewTakeImage, cardViewSelectImage;

    public DialogBoxSelectImage(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_select_image);
        setCancelable(false);

        imgCloseDialogBox = findViewById(R.id.imgCloseDialogBox);
        cardViewTakeImage = findViewById(R.id.cardViewTakeImage);
        cardViewSelectImage = findViewById(R.id.cardViewSelectImage);

        imgCloseDialogBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        cardViewTakeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                executeTakePhoto();
            }
        });
        cardViewSelectImage.setOnClickListener(new View.OnClickListener() {
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
