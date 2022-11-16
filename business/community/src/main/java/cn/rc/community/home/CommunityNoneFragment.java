package cn.rc.community.home;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.basis.ui.BaseActivity;
import com.basis.ui.BaseFragment;
import com.basis.ui.CmdKey;
import com.basis.utils.Logger;
import com.basis.utils.UIKit;
import com.google.android.material.imageview.ShapeableImageView;

import cn.rc.community.R;
import cn.rongcloud.config.router.RouterPath;

/**
 * 社区首页 -> 右侧创建/发现
 */
public class CommunityNoneFragment extends BaseFragment implements View.OnClickListener {


    private ShapeableImageView ivCoverId;
    private TextView tvCommunityName;
    private LinearLayout create;
    private LinearLayout find;

    @Override
    public int setLayoutId() {
        return R.layout.fragment_community_create;
    }

    @Override
    public void init() {
        ivCoverId = (ShapeableImageView) getView(R.id.iv_cover_id);
        tvCommunityName = (TextView) getView(R.id.tv_community_name);
        create = (LinearLayout) getView(R.id.create);
        find = (LinearLayout) getView(R.id.find);
    }

    @Override
    public void initListener() {
        super.initListener();
        create.setOnClickListener(this::onClick);
        find.setOnClickListener(this::onClick);
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.create) {
            UIKit.startActivity(activity, CreateCommunityActivity.class);
        } else if (id == R.id.find) {
            jumpToFind();
        }
    }

    private void jumpToFind() {
        Logger.e(TAG,"activity = "+activity.getClass().getSimpleName());
        if (activity instanceof BaseActivity){
            activity.onRefresh(new RefreshCmd(CmdKey.KEY_HOME_SWITCH, RouterPath.FRAGMENT_FIND));
        }
    }


}
