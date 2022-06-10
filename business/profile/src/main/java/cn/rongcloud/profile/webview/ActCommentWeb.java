package cn.rongcloud.profile.webview;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.basis.ui.BaseActivity;
import com.basis.utils.UIKit;
import com.basis.widget.interfaces.IWrapBar;

import cn.rongcloud.config.router.RouterPath;
import cn.rongcloud.profile.R;


/**
 * @author: BaiCQ
 * @ClassName: ActCommentWeb
 * @Description: 只加载https
 */
@Route(path = RouterPath.ROUTER_H5)
public class ActCommentWeb extends BaseActivity {
    public final static String tag = "ActCommentWeb";
    public final static String _https = "https";
    //拦截url
    private final static int _codeLogin = 10003;
    private RCWebView webview;
    private String loadUrl;
    private String title;
    private String reLoadUrl;
    //是否是https的请求
    private boolean isHttps = false;

    /**
     * 加载url
     *
     * @param mActivity
     * @param loadUrl   加载url
     * @param title     标题
     */
    public static void openCommentWeb(Activity mActivity, String loadUrl, String title) {
        Intent intent = new Intent(mActivity, ActCommentWeb.class);
        intent.putExtra(UIKit.KEY_BASE, loadUrl);
        intent.putExtra(UIKit.KEY_BASE1, title);
        mActivity.startActivity(intent);
    }

    @Override
    public int setLayoutId() {
        return R.layout.act_commment_web;
    }


    public void init() {
        loadUrl = getIntent().getStringExtra(UIKit.KEY_BASE);
        title = getIntent().getStringExtra(UIKit.KEY_BASE1);
        Log.e(tag, "loadUrl = " + loadUrl + " title = " + title);
        getWrapBar().setTitle(title).setElevation(IWrapBar.DEFAULT_ELEVATION).work();
        webview = findViewById(R.id.webview);
        isHttps = TextUtils.isEmpty(loadUrl) ? false : loadUrl.contains(_https);
        WebViewUtil.openWebViewCache(this, webview, isHttps);
        //添加js
        webview.setWebViewClient(new CommonWebViewClient(activity));
        WebViewUtil.loadUrl(this, webview, loadUrl);

        webview.setOnLoadListener(new OnLoadListener() {
            @Override
            public void onTitle(String h5Title) {
                if (TextUtils.isEmpty(title) && !TextUtils.isEmpty(h5Title)) {
                    getWrapBar().setTitle(h5Title).work();
                }
            }

            @Override
            public void onFinish() {
                finish();
            }
        });
    }
}
