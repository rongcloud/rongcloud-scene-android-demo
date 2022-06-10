package cn.rc.community.channel.details;

import android.app.Activity;
import android.content.Intent;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.basis.ui.BaseActivity;
import com.basis.utils.UIKit;

import cn.rc.community.R;

/**
 * 标注（置顶）消息界面
 */
public class MarkMessageActivity extends BaseActivity {

    @Override
    public int setLayoutId() {
        return R.layout.activity_mark_message;
    }

    public static void openMarkMessage(Activity activity, String channelId, int requestCode) {
        Intent intent = new Intent(activity, MarkMessageActivity.class);
        intent.putExtra(UIKit.KEY_BASE, channelId);
        activity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void init() {
        getWrapBar().setTitle(R.string.cmu_tagged_information).work();
        initFragment();
    }

    public void initFragment() {
        String channelId = getIntent().getStringExtra(UIKit.KEY_BASE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = MarkMsgFragment.newInstance(channelId);
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }
}
