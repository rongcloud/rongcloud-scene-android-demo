package cn.rongcloud.profile.region;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.basis.adapter.RcyHolder;
import com.basis.adapter.RcySAdapter;
import com.basis.ui.BaseActivity;
import com.basis.utils.GsonUtil;
import com.basis.utils.Logger;
import com.basis.utils.ResUtil;
import com.basis.utils.ScreenUtil;
import com.basis.utils.UIKit;
import com.basis.widget.SearchEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import cn.rongcloud.profile.R;
import cn.rongcloud.profile.region.sort.SideBar;


public class RegionActivity extends BaseActivity implements View.OnClickListener {
    public final static int CODE_REGION = 10023;
    RecyclerView rvContacts;
    private SideBar sideBar;
    private List<Region> data = new ArrayList<>();
    private LinearLayoutManager layoutManager;
    private SearchEditText editText;
    private View ivDelete, cancel;
    private RegionAdapter adapter;

    public static void openRegionPage(Activity activity) {
        activity.startActivityForResult(new Intent(activity, RegionActivity.class), CODE_REGION);
    }

    @Override
    public int setLayoutId() {
        return R.layout.activity_region;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.cancel == id) {
            onBackCode();
        } else if (R.id.iv_delete == id) {
            if (null != editText) editText.setText("");
        }
    }

    void initSearch() {
        editText = findViewById(R.id.et_search);
        ivDelete = findViewById(R.id.iv_delete);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        ivDelete.setOnClickListener(this);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                UIKit.setVisible(ivDelete, !TextUtils.isEmpty(text));
                search(text);

            }
        });
        // 搜索
        editText.setOnSearchListener(new SearchEditText.OnSearchListener() {
            @Override
            public void onSearch(String search) {
                search(search);
            }
        });
    }

    /**
     * 检索 输入变化和软件盘搜索
     *
     * @param search 检索文字
     */
    void search(String search) {
        if (null == data || data.isEmpty() || null == adapter) {
            return;
        }
        if (TextUtils.isEmpty(search)) {
            adapter.setData(data, true);
        } else {
            List<Region> list = new ArrayList<>();
            for (Region r : data) {
                if (r.locale.getZh().contains(search) || r.region.contains(search)) {
                    list.add(r);
                }
            }
            adapter.setData(list, true);
        }
    }

    @Override
    public void init() {
        getWrapBar().setHide(true).work();
        rvContacts = findViewById(R.id.rv_contacts);
        layoutManager = new LinearLayoutManager(this);
        rvContacts.setLayoutManager(layoutManager);
        adapter = new RegionAdapter(activity);
        rvContacts.setAdapter(adapter);
        sideBar = findViewById(R.id.side_bar);
        // 重置高度
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                ScreenUtil.getScreenHeight() / 2);
        lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        sideBar.setLayoutParams(lp);
        sideBar.requestLayout();
        sideBar.setOnStrSelectCallBack(new SideBar.ISideBarSelectCallBack() {
            @Override
            public void onSelectStr(int index, String selectStr) {
                for (int i = 0; i < data.size(); i++) {
                    if (selectStr.equalsIgnoreCase(data.get(i).getFirstLetter())) {
                        layoutManager.scrollToPositionWithOffset(i, 0);
                        return;
                    }
                }
            }
        });
        parserRegion();
        adapter.setData(data, true);
        initSearch();
    }

    void parserRegion() {
        String lan = Locale.getDefault().getLanguage();
        Logger.e(TAG, "lan = " + lan);
        String json = ResUtil.readStringFromAssets("region.json");
        data = GsonUtil.json2List(json, Region.class);
        if (null != data && !data.isEmpty()) {
            for (Region r : data) {
                r.locale.initPinYin(TextUtils.equals("zh", lan));
            }
        }
        Collections.sort(data);
    }

    public static class RegionAdapter extends RcySAdapter<Region, RcyHolder> {
        RegionAdapter(Context context) {
            super(context, R.layout.item_region);
        }

        @Override
        public void convert(RcyHolder holder, Region region, int position) {
            //根据position获取首字母作为目录catalog
            String catalog = region.getFirstLetter();
            //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if (position == getPositionForSection(catalog)) {
                holder.setVisible(R.id.catalog, true);
                holder.setText(R.id.catalog, region.getFirstLetter());
            } else {
                holder.setVisible(R.id.catalog, false);
            }
            holder.setText(R.id.name, region.locale.getLocal());
            holder.setText(R.id.num, "+" + region.getRegion());
            holder.rootView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.putExtra("region", region);
                    ((Activity) context).setResult(Activity.RESULT_OK, i);
                    ((Activity) context).finish();
                }
            });
        }

        /**
         * 获取catalog首次出现位置
         */
        public int getPositionForSection(String catalog) {
            for (int i = 0; i < getItemCount(); i++) {
                String sortStr = getItem(i).getFirstLetter();
                if (catalog.equalsIgnoreCase(sortStr)) {
                    return i;
                }
            }
            return -1;
        }
    }
}
