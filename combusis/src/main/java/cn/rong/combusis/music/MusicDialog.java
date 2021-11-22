package cn.rong.combusis.music;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.rongcloud.common.ui.widget.ActionSnackBar;

import java.util.ArrayList;
import java.util.List;

import cn.rong.combusis.AudioEffectManager;
import cn.rong.combusis.R;
import cn.rong.combusis.common.base.BaseBottomSheetDialogFragment;
import cn.rong.combusis.common.utils.UIKit;
import cn.rong.combusis.music.fragment.FragmentMusicAdd;
import cn.rong.combusis.music.fragment.FragmentMusicControl;
import cn.rong.combusis.music.fragment.FragmentMusicList;

public class MusicDialog extends BaseBottomSheetDialogFragment implements View.OnClickListener {
    private String roomId;
    private ActionSnackBar actionSnackBar;
    private ViewPager2 viewPager;
    private List<Fragment> fragments = new ArrayList<>(8);
    private View tabMusicList, tabMusicAdd, tabMusicControl;
    private View atmosphereMusic;

    public MusicDialog(String roomId) {
        super(R.layout.layout_music_dialog);
        this.roomId = roomId;
    }

    void initActionSnackBar(View view) {
        // 加载音效
        AudioEffectManager.INSTANCE.init();

        actionSnackBar = ActionSnackBar.Companion.make((ViewGroup) view, R.layout.layout_music_atmosphere);
        View actionView = actionSnackBar.getView();
        actionView.setBackgroundColor(UIKit.getResources().getColor(R.color.transparent));
        actionSnackBar.addCallback(new BaseTransientBottomBar.BaseCallback<ActionSnackBar>() {
            @Override
            public void onDismissed(ActionSnackBar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
            }

            @Override
            public void onShown(ActionSnackBar transientBottomBar) {
                super.onShown(transientBottomBar);
            }
        });
        View[] atmospherevs = new View[3];
        atmospherevs[0] = actionView.findViewById(R.id.tv_music_atmosphere_enter);
        atmospherevs[1] = actionView.findViewById(R.id.tv_music_atmosphere_clap);
        atmospherevs[2] = actionView.findViewById(R.id.tv_music_atmosphere_cheer);
        for (int i = 0; i < 3; i++) {
            View v = atmospherevs[i];
            final int index = i;
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (View v : atmospherevs) {
                        v.setSelected(false);
                    }
                    view.setSelected(true);
                    AudioEffectManager.INSTANCE.playEffect(index);
                }
            });
        }
    }

    @Override
    public void initView() {
        tabMusicList = getView().findViewById(R.id.iv_music_list);
        tabMusicList.setOnClickListener(this);
        tabMusicAdd = getView().findViewById(R.id.iv_add_music);
        tabMusicAdd.setOnClickListener(this);
        tabMusicControl = getView().findViewById(R.id.iv_music_control);
        tabMusicControl.setOnClickListener(this);
        // vp
        View view = getView().findViewById(R.id.cl_top);
        viewPager = getView().findViewById(R.id.vp_page);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                refresTabState(position, false);
            }
        });
        fragments.add(FragmentMusicList.getInstance(roomId));
        // add listener
        FragmentMusicList.addMusicLisener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresTabState(1, true);
            }
        };
        fragments.add(FragmentMusicAdd.getInstance(roomId));
        fragments.add(FragmentMusicControl.getInstance(roomId));
        viewPager.setAdapter(new FragmentStateAdapter(getChildFragmentManager(), getLifecycle()) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragments.get(position);
            }

            @Override
            public int getItemCount() {
                return fragments.size();
            }
        });

        // 音效
        initActionSnackBar(view);
        atmosphereMusic = getView().findViewById(R.id.iv_atmosphere_music);
        atmosphereMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actionSnackBar.isShown()) {
                    atmosphereMusic.setSelected(false);
                    actionSnackBar.dismiss();
                } else {
                    atmosphereMusic.setSelected(true);
                    actionSnackBar.show();
                }
            }
        });

        MusicManager.get().init(roomId);
        // 默认：postiont == 0
        refresTabState(0, true);
    }

    @Override
    public void dismiss() {
        super.dismiss();
        MusicManager.get().unInit();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        int index = 0;
        if (R.id.iv_music_list == id) {
            index = 0;
        } else if (R.id.iv_add_music == id) {
            index = 1;
        } else if (R.id.iv_music_control == id) {
            index = 2;
        }
        refresTabState(index, true);
    }

    private void refresTabState(int postiont, boolean refreshVP) {
        tabMusicList.setSelected(postiont == 0);
        tabMusicAdd.setSelected(postiont == 1);
        tabMusicControl.setSelected(postiont == 2);
        if (refreshVP) viewPager.setCurrentItem(postiont);
    }
}
