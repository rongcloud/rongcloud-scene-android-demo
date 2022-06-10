package cn.rc.community.setting.member;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.basis.ui.BaseActivity;
import com.basis.ui.BaseFragment;
import com.basis.ui.CmdKey;
import com.basis.utils.ResUtil;
import com.basis.widget.SearchEditText;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import cn.rc.community.Constants;
import cn.rc.community.R;

/**
 * 社区设置 - 社区成员
 */
public class MemberActivity extends BaseActivity {
    private SearchEditText etSearch;
    private TabLayout tlTitle;
    private ViewPager2 vpPage;

    @Override
    public int setLayoutId() {
        return R.layout.activity_member;
    }

    @Override
    public void init() {
        getWrapBar().setTitle(ResUtil.getString(R.string.cmu_member)).work();
        initView();
    }

    private void initView() {
        etSearch = getView(R.id.et_search);
        tlTitle = getView(R.id.tab_switch);
        vpPage = getView(R.id.vp_switch);

        ArrayList<BaseFragment> fragments = new ArrayList<>();
        MemberFragment onlineFragment = new MemberFragment(ResUtil.getString(R.string.cmu_online_member), Constants.MemberType.ONLINE);//在线列表
        onlineFragment.setTotalMemberCountListener(new MemberFragment.TotalMemberCountListener() {
            @Override
            public void onTotalChange(int total) {
                //在线人数
                tlTitle.getTabAt(0).setText(onlineFragment.getTitle() + " " + total + " ");
            }
        });
        MemberFragment offlineFragment = new MemberFragment(ResUtil.getString(R.string.cmu_offline_member), Constants.MemberType.OFFLINE);//离线列表
        offlineFragment.setTotalMemberCountListener(new MemberFragment.TotalMemberCountListener() {
            @Override
            public void onTotalChange(int total) {
                //离线人数
                tlTitle.getTabAt(1).setText(offlineFragment.getTitle() + " " + total + " ");
            }
        });
        fragments.add(onlineFragment);
        fragments.add(offlineFragment);
        vpPage.setOrientation(ViewPager2.ORIENTATION_HORIZONTAL);
        FragmentStateAdapter fragmentStateAdapter = new FragmentStateAdapter(getSupportFragmentManager(), getLifecycle()) {
            @Override
            public int getItemCount() {
                return fragments.size();
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragments.get(position);
            }
        };
        vpPage.setAdapter(fragmentStateAdapter);
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(tlTitle, vpPage, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(fragments.get(position).getTitle());
            }
        });
        tabLayoutMediator.attach();
        etSearch.setOnSearchListener(new SearchEditText.OnSearchListener() {
            @Override
            public void onSearch(String search) {
                if (TextUtils.isEmpty(search)) {
                    return;
                }
                etSearch.setText("");
                int index = vpPage.getCurrentItem();
                BaseFragment fragment = fragments.get(index);
                if (null != fragment) {
                    ICmd key = new RefreshCmd(MemberFragment.CMD_SEARCH, search);
                    fragment.onRefresh(key);
                }
            }
        });
    }
}
