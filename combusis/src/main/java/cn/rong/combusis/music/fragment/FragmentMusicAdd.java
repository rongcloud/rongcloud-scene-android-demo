package cn.rong.combusis.music.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.basis.adapter.interfaces.IAdapte;
import com.basis.adapter.recycle.RcyHolder;
import com.basis.adapter.recycle.RcySAdapter;
import com.basis.mvp.BasePresenter;
import com.basis.net.LoadTag;
import com.basis.ui.BaseFragment;
import com.bcq.refresh.XRecyclerView;
import com.kit.utils.KToast;
import com.kit.utils.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.rong.combusis.R;
import cn.rong.combusis.common.net.IResultBack;
import cn.rong.combusis.common.utils.UIKit;
import cn.rong.combusis.music.IMusic;
import cn.rong.combusis.music.MusicManager;
import cn.rong.combusis.music.domain.MusicBean;
import cn.rong.combusis.sdk.event.wrapper.EToast;

public class FragmentMusicAdd extends BaseFragment {
    private final static int MUSIC_PICK_REQUEST_CODE = 10000;
    private final static ArrayList suportTypes = new ArrayList();

    static {
        suportTypes.addAll(Arrays.asList("audio/x-mpeg",
                "audio/aac",
                "audio/mp4a-latm",
                "audio/x-wav",
                "audio/ogg",
                "audio/3gpp"));
    }

    XRecyclerView rvMusic;
    private String roomId;
    private IAdapte<MusicBean, RcyHolder> adapter;

    public static FragmentMusicAdd getInstance(String roomId) {
        FragmentMusicAdd f = new FragmentMusicAdd();
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
        return R.layout.fragment_add_music;
    }

    @Override
    public void init() {
        roomId = getArguments().getString(UIKit.KEY_BASE);
        Logger.e(TAG, "roomId = " + roomId);
        rvMusic = (XRecyclerView) getView(R.id.rv_list);
        rvMusic.enableLoad(false);
        rvMusic.enableRefresh(false);
        adapter = new RcySAdapter<MusicBean, RcyHolder>(getContext(), R.layout.layout_add_music) {
            @Override
            public void convert(RcyHolder holder, MusicBean musicBean, int position) {
                FragmentMusicAdd.this.convert(holder, musicBean, position);
            }
        };
        adapter.setRefreshView(rvMusic);
        MusicManager.get().addMusicListenre(new IMusic.VRMusicListener() {
            @Override
            public void onMusics(List<MusicBean> sysMusics, List<MusicBean> userMusics) {
                List<MusicBean> list = new ArrayList<>();
                if (null != sysMusics) list.addAll(sysMusics);
                // add 按钮
                MusicBean customerAdd = new MusicBean();
                customerAdd.setType(MusicManager.MUSIC_LOCAL_ADD);
                list.add(customerAdd);
                adapter.setData(list, true);
            }

            @Override
            public void onPlayState(String url) {
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == MUSIC_PICK_REQUEST_CODE && null != data) {
            Uri uri = data.getData();
            Logger.e(TAG, "uri = " + uri);
            if (null != uri) {
                MusicManager.get().addMusicByUri(uri, new LoadTag(getActivity(), "上传中..."), new IResultBack<Boolean>() {
                    @Override
                    public void onResult(Boolean aBoolean) {
                        if (aBoolean) EToast.showToast("音乐添加成功");
                    }
                });
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void convert(RcyHolder holder, MusicBean model, int position) {
        if (model.getType() == MusicManager.MUSIC_LOCAL_ADD) {
            holder.setVisible(R.id.tv_music_author, false);
            holder.setVisible(R.id.tv_music_size, false);
            holder.setText(R.id.tv_music_name, "本地上传");
            holder.setBackgroundResource(R.id.iv_music_icon, R.drawable.ic_add_music_from_local);
            holder.setBackgroundResource(R.id.iv_music_status, R.drawable.ic_add_music_not_add);
            holder.setOnClickListener(R.id.iv_music_status, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, suportTypes);
                    intent.setType("*/*");
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    startActivityForResult(intent, MUSIC_PICK_REQUEST_CODE);
                }
            });
        } else {
            holder.setBackgroundResource(R.id.iv_music_icon, R.drawable.ic_add_music_list_icon);
            holder.setText(R.id.tv_music_name, model.getName());
            holder.setVisible(R.id.tv_music_author, true);
            holder.setText(R.id.tv_music_author, model.getAuthor());
            holder.setVisible(R.id.tv_music_size, true);
            holder.setText(R.id.tv_music_size, model.getSize() + "M");
            holder.setBackgroundResource(R.id.iv_music_status, model.isAddAlready() ? R.drawable.ic_add_music_had_add : R.drawable.ic_add_music_not_add);
            holder.getView(R.id.iv_music_status).setEnabled(!model.isAddAlready());
            holder.setOnClickListener(R.id.iv_music_status, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MusicManager.get().addMusic(model, new IResultBack<Boolean>() {
                        @Override
                        public void onResult(Boolean aBoolean) {
                            KToast.show(aBoolean ? "音乐添加成功" : "音乐添加失败");
                        }
                    });
                }
            });
        }
    }

}
