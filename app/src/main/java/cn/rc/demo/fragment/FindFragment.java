package cn.rc.demo.fragment;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.basis.ui.BaseFragment;

import cn.rc.demo.R;
import cn.rongcloud.config.router.RouterPath;
import cn.rongcloud.profile.webview.CommonWebViewClient;
import cn.rongcloud.profile.webview.RCWebView;
import cn.rongcloud.profile.webview.WebViewUtil;

@Route(path = RouterPath.FRAGMENT_FIND)
public class FindFragment extends BaseFragment {
    private final static String URL = "https://m.rongcloud.cn/activity/rtc20";

    @Override
    public int setLayoutId() {
        return R.layout.fragment_find;
    }

    @Override
    public void init() {
        RCWebView webview = getView(R.id.webview);
        WebViewUtil.openWebViewCache(activity, webview, true);
        //添加js
        webview.setWebViewClient(new CommonWebViewClient(activity));
        //添加弹框 alert title 拦截
        WebViewUtil.loadUrl(activity, webview, URL);
    }
}
