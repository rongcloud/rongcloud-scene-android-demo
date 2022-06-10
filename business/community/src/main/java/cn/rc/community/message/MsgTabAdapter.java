package cn.rc.community.message;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.utils.Logger;

import cn.rc.community.R;

public class MsgTabAdapter extends RcySAdapter<MsgTabAdapter.MsgTab, RcyHolder> {
    private ViewPager2 vp;
    private int selected;

    protected void attach(RecyclerView recyclerView, ViewPager2 vp) {
        if (null != recyclerView) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            layoutManager.setOrientation(RecyclerView.HORIZONTAL);
            recyclerView.setAdapter(this);
        }
        this.vp = vp;
        vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                setSelected(position);
            }
        });
    }

    public int getSelected() {
        return selected;
    }

    protected void setSelected(int position) {
        selected = position;
        MsgTab tab = getItem(selected);
        if (null != tab) {
            tab.unReadCount = 0;
        }
        notifyDataSetChanged();
    }

    protected void refreshUnreadCount(int index, int count) {
        Logger.e("MsgTabAdapter", "refreshUnreadCount:index = " + index + " count = " + count);

        MsgTab tab = getItem(index);
        if (null != tab) {
            tab.unReadCount = index == selected ? 0 : count;
            notifyItemChanged(index);
        }
    }

    public MsgTabAdapter(Context context) {
        super(context, R.layout.item_message_tab);
    }

    @Override
    public void convert(RcyHolder holder, MsgTab item, int position) {
        holder.itemView.setSelected(selected == position);
        holder.setText(R.id.title, item.title);
        TextView title = holder.getView(R.id.title);
        title.setTypeface(selected == position ? Typeface.defaultFromStyle(Typeface.BOLD) :
                Typeface.defaultFromStyle(Typeface.NORMAL));
        holder.setVisible(R.id.tv_unread, selected != position && item.unReadCount > 0);
        holder.setText(R.id.tv_unread, item.unReadCount + "");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != vp) vp.setCurrentItem(position);
                setSelected(position);
            }
        });
    }

    public static class MsgTab {
        private final String title;
        private int unReadCount = 0;

        MsgTab(String title) {
            this.title = title;
        }
    }
}
