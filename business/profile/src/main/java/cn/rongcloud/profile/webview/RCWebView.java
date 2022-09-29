package cn.rongcloud.profile.webview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

import cn.rongcloud.profile.R;


public class RCWebView extends WebView {
    private ProgressBar progressbar;
    private OnLoadListener listener;

    public RCWebView(Context context) {
        this(context, null);
    }

    public RCWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        progressbar = new ProgressBar(context, null, android.R.attr.progressBarStyleHorizontal);
        progressbar.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 3, 0, 0));

        Drawable drawable = context.getResources().getDrawable(R.drawable.layer_list_web_progress_bar);
        progressbar.setProgressDrawable(drawable);
        addView(progressbar);
        setWebChromeClient(new DefaultWebChromeClient());
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

        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ONLY); //缓存模式

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

    public class DefaultWebChromeClient extends WebChromeClient {
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (null != listener) listener.onTitle(title);
            if (null != webChromeClient) {
                webChromeClient.onReceivedTitle(view, title);
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressbar.setVisibility(GONE);
            } else {
                if (progressbar.getVisibility() == GONE)
                    progressbar.setVisibility(VISIBLE);
                progressbar.setProgress(newProgress);
            }
            if (null != webChromeClient) webChromeClient.onProgressChanged(view, newProgress);
        }

        @Override
        public void onPermissionRequest(PermissionRequest request) {
            if (null == webChromeClient) {
                request.grant(request.getResources());
            } else {
                webChromeClient.onPermissionRequest(request);
            }
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

    WebChromeClient webChromeClient;

    public void addWebChromeClient(WebChromeClient client) {
        this.webChromeClient = client;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        LayoutParams lp = (LayoutParams) progressbar.getLayoutParams();
        lp.x = l;
        lp.y = t;
        progressbar.setLayoutParams(lp);
        super.onScrollChanged(l, t, oldl, oldt);
    }

    public void setOnLoadListener(OnLoadListener listener) {
        this.listener = listener;
    }

    @Override
    public void goBack() {
        if (!canGoBack()) {
            if (null != listener) listener.onFinish();
            return;
        }
        super.goBack();
    }
}