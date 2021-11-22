package cn.rongcloud.voiceroomdemo.webview;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.basis.ui.BaseActivity;

import cn.rongcloud.voiceroomdemo.R;


/**
 * @author: BaiCQ
 * @ClassName: ActCommentWeb
 * @Description: 只加载https
 */
public class ActCommentWeb extends BaseActivity {
    public final static String tag = "ActCommentWeb";
    public final static String _https = "https";
    public final static String KEY_BASIC = "key_basic";
    public final static String KEY_URL = "key_url";
    //拦截url
    private final static int _codeLogin = 10003;
    private QlWebView webview;
    private String currnetUrl;
    private String curretnTitle;
    private String reLoadUrl;
    //是否是https的请求
    private boolean isHttps = false;
    private TextView tvTitle;
    private Button btnRefresh, btnBack;

    /**
     * 加载url
     *
     * @param mActivity
     * @param loadUrl   加载url
     * @param title     标题
     */
    public static void openCommentWeb(Activity mActivity, String loadUrl, String title) {
        Intent intent = new Intent(mActivity, ActCommentWeb.class);
        intent.putExtra(KEY_URL, loadUrl);
        intent.putExtra(KEY_BASIC, title);
        mActivity.startActivity(intent);
    }

    @Override
    public int setLayoutId() {
        return R.layout.act_commment_web;
    }

    public void init() {
        currnetUrl = getIntent().getStringExtra(KEY_URL);
        curretnTitle = getIntent().getStringExtra(KEY_BASIC);
        getWrapBar().setTitle(curretnTitle).work();
        tvTitle = findViewById(R.id.title);
        btnRefresh = findViewById(R.id.title_refresh);
        btnBack = findViewById(R.id.title_back);
        webview = findViewById(R.id.webview);
        Log.e(tag, "currnetUrl = " + currnetUrl);
        isHttps = TextUtils.isEmpty(currnetUrl) ? false : currnetUrl.contains(_https);
        tvTitle.setText(curretnTitle);
        CommonUtil.openWebViewCache(this, webview, isHttps);
        //添加js
        webview.setWebViewClient(new MyWebViewClient());
        //添加弹框 alert title 拦截
        webview.setWebChromeClient(new MyWebChromeClient());
        //添加js交互
        webview.addJavascriptInterface(new JsObject(), "android");
        //同步cookie
//        CommonUtil.syncCookie(this, currnetUrl);
        CommonUtil.loadUrl(this, webview, currnetUrl);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.reload();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (webview.canGoBack()) {
                    webview.goBack();
                } else {
                    finish();
                }
            }
        });
    }

    class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            webview.loadUrl("javascript:returnValue()");
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
            reLoadUrl = failingUrl;
            Log.e("onReceivedError", "url = " + failingUrl);
            Log.e("onReceivedError", "errorCode = " + errorCode);
            Log.e("onReceivedError", "description = " + description);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.e("shouldOverride", "url = " + url);
            if (url.startsWith("tel")) {
                // 修改web拨号跳异常界面的审核问题
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse(url);
                intent.setData(data);
                startActivity(intent);
                return true;
            }
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Log.e("onReceivedSslError", "error = " + error.toString());
            //接受所有认证
            if (isHttps) {
                handler.proceed();
            } else {
                super.onReceivedSslError(view, handler, error);
            }
        }
    }

    class MyWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            tvTitle.setText(title);
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            Log.e("onJsAlert", "message = " + message);
            return super.onJsAlert(view, url, message, result);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
        }

        @Override
        public void onPermissionRequest(PermissionRequest request) {
            Log.e("onPermissionRequest", "request = " + request.getResources()[0]);
            request.grant(request.getResources());
        }
    }

    public class JsObject {
        @JavascriptInterface
        public void joinConference(String meetId, String title) { //入会
            Toast.makeText(ActCommentWeb.this, "执行 Android 加入会议", Toast.LENGTH_LONG).show();
        }
    }
}
