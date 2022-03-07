package cn.rc.demo;

import android.content.Context;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.android.arouter.launcher.ARouter;
import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.utils.ScreenUtil;

import java.util.List;

public class HomeBarAdapter extends RcySAdapter<HomeBottomBar, RcyHolder> {
    int containId;

    public HomeBarAdapter(Context context, int containId) {
        super(context, R.layout.item_home);
        this.containId = containId;
    }

    int width() {
        return ScreenUtil.getScreenWidth() / getItemCount();
    }

    @Override
    public void convert(RcyHolder holder, HomeBottomBar homeBottomBar, int position) {
        holder.itemView.getLayoutParams().width = width();
        holder.setImageResource(R.id.icon, homeBottomBar.icon);
        holder.setText(R.id.title, homeBottomBar.title);
        holder.setVisible(R.id.tv_unread, homeBottomBar.hasRedPoint);
        // selected
        holder.itemView.setSelected(homeBottomBar.selected);
        if (homeBottomBar.selected) {
            switchTo(homeBottomBar.router);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetSelected();
                homeBottomBar.selected = true;
                homeBottomBar.hasRedPoint = false;
                notifyDataSetChanged();
                switchTo(homeBottomBar.router);
            }
        });
    }

    void resetSelected() {
        List<HomeBottomBar> data = getData();
        for (HomeBottomBar bar : data) {
            bar.selected = false;
        }
    }

    void switchTo(String router) {
        FragmentManager manager = ((FragmentActivity) context).getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment f = manager.findFragmentByTag(router);
        if (null == f) {
            f = (Fragment) ARouter.getInstance().build(router).navigation();
        }
        transaction.replace(containId, f, router);
        transaction.commitAllowingStateLoss();
    }
}
