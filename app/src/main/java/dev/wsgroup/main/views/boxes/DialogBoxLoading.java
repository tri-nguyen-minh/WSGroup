package dev.wsgroup.main.views.boxes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import dev.wsgroup.main.R;

public class DialogBoxLoading extends Dialog {

    public DialogBoxLoading(Activity activity) {
        super(activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_box_loading);
    }
}
