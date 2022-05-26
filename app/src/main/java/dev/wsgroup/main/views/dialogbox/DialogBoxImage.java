package dev.wsgroup.main.views.dialogbox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import dev.wsgroup.main.R;

public class DialogBoxImage extends Dialog {

    private ImageView imgCloseDialogBox, imgDisplay;

    private Context context;
    private String imageLink;

    public DialogBoxImage(Activity activity, Context context, String imageLink) {
        super(activity);
        this.context = context;
        this.imageLink = imageLink;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_image);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        setCancelable(false);

        imgCloseDialogBox = findViewById(R.id.imgCloseDialogBox);
        imgDisplay = findViewById(R.id.imgDisplay);

        imgCloseDialogBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        Glide.with(context).load(imageLink).into(imgDisplay);
    }
}
