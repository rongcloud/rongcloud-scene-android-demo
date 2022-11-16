package cn.rongcloud.music;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.basis.utils.ImageLoader;

import cn.rongcloud.musiccontrolkit.RCMusicControlEngine;
import cn.rongcloud.musiccontrolkit.bean.Music;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author gyn
 * @date 2021/12/21
 */
public class MusicMiniView extends ConstraintLayout {

    private TextView tvName;
    private CircleImageView civCover;
    private boolean isShowing = false;
    private RotateAnimation rotateAnimation;

    public MusicMiniView(@NonNull Context context) {
        this(context, null);
    }

    public MusicMiniView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_music_mini, this);
        initView();
    }


    private void initView() {
        tvName = (TextView) findViewById(R.id.tv_name);
        civCover = (CircleImageView) findViewById(R.id.civ_cover);
        setAlpha(0);
        setVisibility(INVISIBLE);
        Context context = getContext();
        if (context instanceof LifecycleOwner) {
            RCMusicControlEngine.getInstance().playingObserve().observe((LifecycleOwner) context, new Observer<Boolean>() {
                @Override
                public void onChanged(Boolean aBoolean) {
                    if (aBoolean) {
                        Music music = RCMusicControlEngine.getInstance().getPlayingMusic();
                        if (music != null) {
                            StringBuilder name = new StringBuilder(music.getMusicName());
                            if (!TextUtils.isEmpty(music.getAuthor())) {
                                name.append("-").append(music.getAuthor());
                            }
                            show(name.toString(), music.getCoverUrl());
                        }
                    } else {
                        dismiss();
                    }
                }
            });
        }
        rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(3000);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());

    }

    public void setOnMusicClickListener(OnClickListener l) {
        civCover.setOnClickListener(l);
    }

    public void show(String name, String url) {
        isShowing = true;
        tvName.setText(name);
        ImageLoader.loadUrl(civCover, url, R.drawable.ic_music_mini);
        setVisibility(VISIBLE);
        civCover.clearAnimation();
        this.animate().alpha(1).setDuration(300).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                civCover.startAnimation(rotateAnimation);
            }
        }).start();
    }

    public void dismiss() {
        if (isShowing) {
            isShowing = false;
            this.animate().alpha(0).setDuration(300).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    civCover.clearAnimation();
                    setVisibility(INVISIBLE);
                }
            }).start();
        }
    }
}
