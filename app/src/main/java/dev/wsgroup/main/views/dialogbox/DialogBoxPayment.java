package dev.wsgroup.main.views.dialogbox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.models.dtos.Discount;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.utils.MethodUtils;

public class DialogBoxPayment extends Dialog {

    private Activity activity;
    private String url, returnUrl, vnpRef;

    public DialogBoxPayment(Activity activity, String url) {
        super(activity);
        this.activity = activity;
        this.url = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                onCancelDialogBox();
            }
        });
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        WebView webView = new WebView(activity.getApplicationContext());
        webView.loadUrl(url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                returnUrl = request.getUrl().toString();
                if (returnUrl.contains("vnp_TxnRef")) {
                    dismiss();
                    if (MethodUtils.getVNPayResponseCode(returnUrl).equals("00")) {
                        vnpRef = MethodUtils.getVNPayRef(returnUrl);
                        onCompletedPayment(vnpRef);
                    } else {
                        onCancelDialogBox();
                    }
                }
                return super.shouldOverrideUrlLoading(view, request);
            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (error.getDescription().toString().contains("ERR_NAME_NOT_RESOLVED")) {
                    dismiss();
                    onStartPaymentFailed();
                }
            }
        });
        setContentView(webView);

    }

    public void onCancelDialogBox() { }

    public void onStartPaymentFailed() {}

    public void onCompletedPayment(String vnpRef) {}
}
