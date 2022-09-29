package cn.rongcloud.profile.webview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class CommonWebViewClient extends WebViewClient {
    Activity activity;

    public CommonWebViewClient(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        view.loadUrl("javascript:returnValue()");
        Log.e("onPageFinished", "url = " + url);
        super.onPageFinished(view, url);
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
//            load_fail.setVisibility(View.GONE);
        Log.e("onPageStarted", "url = " + url);
        super.onPageStarted(view, url, favicon);
    }

    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//            load_fail.setVisibility(View.VISIBLE);
        Log.e("onReceivedError", "url = " + failingUrl);
        Log.e("onReceivedError", "errorCode = " + errorCode);
        Log.e("onReceivedError", "description = " + description);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (url.startsWith("tel")) {
            // 修改web拨号跳异常界面的审核问题
            Intent intent = new Intent(Intent.ACTION_DIAL);
            Uri data = Uri.parse(url);
            intent.setData(data);
            activity.startActivity(intent);
            return true;
        }
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        Log.e("onReceivedSslError", "error = " + error.toString());
        super.onReceivedSslError(view, handler, error);
    }
}
