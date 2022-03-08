package dev.wsgroup.main.views.dialogbox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dev.wsgroup.main.models.dtos.Discount;
import dev.wsgroup.main.models.dtos.Order;
import dev.wsgroup.main.models.utils.MethodUtils;

public class DialogBoxPayment extends Dialog {

    private TextView txtDiscountCount;
    private ImageView imgCloseDialogBox;
    private RecyclerView recViewDiscount;
    private WebView webView;

    private Activity activity;
    private Context context;
    private List<Discount> discountList;
    private String url, returnUrl, vnpRef;
    private Order order;

    public DialogBoxPayment(Activity activity, String url, Order order) {
        super(activity);
        this.activity = activity;
        this.url = url;
        this.order = order;
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
//        setContentView(R.layout.dialog_box_discount);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        WebView webView = new WebView(activity.getApplicationContext());
        webView.loadUrl(url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                returnUrl = request.getUrl().toString();
                if (returnUrl.contains("vnp_TxnRef")) {
                    vnpRef = MethodUtils.getVNPayRef(returnUrl);
                    dismiss();
                    onCompletedPayment(vnpRef);
                }
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        setContentView(webView);

    }

    public void onCancelDialogBox() {
    }

    public void onCompletedPayment(String vnpRef) { }
}
