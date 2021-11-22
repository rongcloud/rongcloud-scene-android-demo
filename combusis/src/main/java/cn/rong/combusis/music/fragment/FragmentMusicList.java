package cn.rong.combusis.music.fragment;

import android.os.Bundle;
import android.view.View;

import com.basis.adapter.recycle.RcyHolder;
import com.basis.adapter.recycle.RcySAdapter;
import com.basis.mvp.BasePresenter;
import com.basis.ui.BaseFragment;
import com.bcq.refresh.XRecyclerView;
import com.kit.utils.KToast;
import com.kit.utils.Logger;

import java.util.List;

import cn.rong.combusis.R;
import cn.rong.combusis.common.net.IResultBack;
import cn.rong.combusis.common.utils.UIKit;
import cn.rong.combusis.music.IMusic;
import cn.rong.combusis.music.MusicManager;
import cn.rong.combusis.music.domain.MusicBean;

public class FragmentMusicList extends BaseFragment {
    public static View.OnClickListener addMusicLisener;
    XRecyclerView rvMusic;
    View addMusic, noneInfo;
    private String roomId;
    private RcySAdapter adapter;

    public static FragmentMusicList getInstance(String roomId) {
        FragmentMusicList f = new FragmentMusicList();
        Bundle b = new Bundle();
        b.putString(UIKit.KEY_BASE, roomId);
        f.setArguments(b);
        return f;
    }

    @Override
    public BasePresenter createPresent() {
        return null;
    }

    @Override
    public void initListener() {
    }

    @Override
    public int setLayoutId() {
        return R.layout.fragment_music_list;
    }

    @Override
    public void init() {
        roomId = getArguments().getString(UIKit.KEY_BASE);
        Logger.e(TAG, "roomId = " + roomId);
        rvMusic = (XRecyclerView) getView(R.id.rv_list);
        noneInfo = getView(R.id.group_add_music);
        addMusic = getView(R.id.btn_add_music);
        addMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != addMusicLisener) addMusicLisener.onClick(view);
            }
        });
        rvMusic.enableLoad(false);
        rvMusic.enableRefresh(false);
        adapter = new RcySAdapter<MusicBean, RcyHolder>(getContext(), R.layout.layout_musics_play_item) {
            @Override
            public void convert(RcyHolder holder, MusicBean musicBean, int position) {
                FragmentMusicList.this.convert(holder, musicBean, position);
            }
        };
        adapter.setRefreshView(rvMusic);
        // 设置音乐监听
        MusicManager.get().addMusicListenre(new IMusic.VRMusicListener() {
            @Override
            public void onMusics(List<MusicBean> sysMusics, List<MusicBean> userMusics) {
                if (null == userMusics || userMusics.isEmpty()) {
                    noneInfo.setVisibility(View.VISIBLE);
                    rvMusic.setVisibility(View.GONE);
                } else {
                    noneInfo.setVisibility(View.GONE);
                    rvMusic.setVisibility(View.VISIBLE);
                    adapter.setData(userMusics, true);
                }
            }

            @Override
            public void onPlayState(String url) {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void convert(RcyHolder holder, MusicBean model, int position) {
        holder.setImageDrawable(R.id.iv_music_play_icon, UIKit.getResources().getDrawable(model.isPlaying()
                ? R.drawable.ic_music_pause : R.drawable.ic_music_play));
        holder.setText(R.id.tv_music_name, model.getName());
        holder.setText(R.id.tv_music_author, model.getAuthor());
        holder.setText(R.id.tv_music_size, model.getSize() + "M");
        holder.setSelected(R.id.tv_music_name, model.isPlaying());
        holder.setSelected(R.id.tv_music_size, model.isPlaying());
        holder.setSelected(R.id.tv_music_author, model.isPlaying());
        //播放状态
        holder.setVisible(R.id.iv_music_top, !model.isPlaying());
        holder.setVisible(R.id.iv_music_delete, !model.isPlaying());
        holder.setVisible(R.id.mpv_music_playing, model.isPlaying());

        holder.setOnClickListener(R.id.iv_music_play_icon, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicManager.get().switchMusicPlayState(model);
            }
        });
        holder.setOnClickListener(R.id.iv_music_delete, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicManager.get().deleteMusic(model, new IResultBack<Boolean>() {
                    @Override
                    public void onResult(Boolean aBoolean) {
                        KToast.show(aBoolean ? "移除音乐成功" : "移除音乐失败");
                    }
                });
            }
        });
        holder.setOnClickListener(R.id.iv_music_top, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MusicManager.get().moveTop(model, new IResultBack<Boolean>() {
                    @Override
                    public void onResult(Boolean aBoolean) {
                        KToast.show(aBoolean ? "音乐置顶成功" : "音乐置顶失败");
                    }
                });
            }
        });
    }

}
