package cn.rongcloud.voiceroomdemo.webview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import cn.rongcloud.voiceroomdemo.R;

public class QlWebView extends WebView {
    public final String JS_Name = "android";
    private ProgressBar progressbar;
    private OnQlListener onQlListener;

    public QlWebView(Context context) {
        this(context, null);
    }

    public QlWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 3, 0, 0));

        Drawable drawable = context.getResources().getDrawable(R.drawable.layer_list_web_progress_bar);
        progressbar.setProgressDrawable(drawable);
        addView(progressbar);
        setWebChromeClient(new QlWebChromeClient());
        addJavascriptInterface(new AndroidJSOb(), JS_Name);
        //默认配置
        initDefaultSetting();
    }

    private void initDefaultSetting() {
        WebSettings webSettings = getSettings();

        webSettings.setUseWideViewPort(true);//可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(true); //显示缩放按钮
        webSettings.setSupportZoom(true);

        webSettings.setJavaScriptEnabled(true);//js交互

        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT); //缓存模式

        webSettings.setDomStorageEnabled(true);//DOM storage
        webSettings.setDatabaseEnabled(true);//database storage
        webSettings.setAppCacheEnabled(true);//ApplicationCaches

        webSettings.setAllowContentAccess(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);

        //https与http混合资源处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }

    public class QlWebChromeClient extends WebChromeClient {

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (null != onQlListener) onQlListener.onTitle(title);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressbar.setVisibility(GONE);
//				progressbar.setProgress(50);
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    /**
     * 不建议使用 会覆盖内部设置
     *
     * @param client
     */
    @Override
    @Deprecated
    public void setWebChromeClient(WebChromeClient client) {
        super.setWebChromeClient(client);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setOnQlListener(OnQlListener onQlListener) {
        this.onQlListener = onQlListener;
    }

    @Override
    public void goBack() {
        if (!canGoBack()) {
            if (null != onQlListener) onQlListener.onFinish();
            return;
        }
        super.goBack();
    }

    /**
     * js交互实现
     */
    public class AndroidJSOb {
        @JavascriptInterface
        public void joinConference(String meetId, String title) {
            if (null != onQlListener) onQlListener.onJoinConference(meetId, title);
        }

        @JavascriptInterface
        public void reLogin() {
            if (null != onQlListener) onQlListener.onRelogin();
        }
    }
}