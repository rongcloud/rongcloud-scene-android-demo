package com.meihu.beauty.glide;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.Headers;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by cxf on 2017/8/9.
 */

public class ImgLoader {
    private static final boolean SKIP_MEMORY_CACHE = false;

    private static Headers sHeaders;

    static {
        sHeaders = new Headers() {
            @Override
            public Map<String, String> getHeaders() {
                return new HashMap<>();
            }
        };
    }

    public static void display(Context context, String url, ImageView imageView) {
        if (context == null || TextUtils.isEmpty(url) || imageView == null) {
            return;
        }
        Glide.with(context).asDrawable().load(new GlideUrl(url, sHeaders)).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    public static void displayWithError(Context context, String url, ImageView imageView, int errorRes) {
        if (context == null || TextUtils.isEmpty(url) || imageView == null) {
            return;
        }
        Glide.with(context).asDrawable().load(new GlideUrl(url, sHeaders)).error(errorRes).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }


    public static void display(Context context, File file, ImageView imageView) {
        if (context == null || imageView == null) {
            return;
        }
        Glide.with(context).asDrawable().load(file).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    public static void display(Context context, int res, ImageView imageView) {
        if (context == null || imageView == null) {
            return;
        }
        Glide.with(context).asDrawable().load(res).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    /**
     * 显示视频封面缩略图
     */
    public static void displayVideoThumb(Context context, String videoPath, ImageView imageView) {
        if (context == null || imageView == null) {
            return;
        }
        Glide.with(context).asDrawable().load(Uri.fromFile(new File(videoPath))).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }


    /**
     * 显示视频封面缩略图
     */
    public static void displayVideoThumb(Context context, File file, ImageView imageView) {
        if (context == null) {
            return;
        }
        Glide.with(context).asDrawable().load(Uri.fromFile(file)).skipMemoryCache(SKIP_MEMORY_CACHE).into(imageView);
    }

    public static void displayDrawable(Context context, String url, final DrawableCallback callback) {
        if (context == null || TextUtils.isEmpty(url)) {
            return;
        }
        Glide.with(context).asDrawable().load(new GlideUrl(url, sHeaders)).skipMemoryCache(SKIP_MEMORY_CACHE).into(new SimpleTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                if (callback != null) {
                    callback.onLoadSuccess(resource);
                }
            }

            @Override
            public void onLoadFailed(@Nullable Drawable errorDrawable) {
                if (callback != null) {
                    callback.onLoadFailed();
                }
            }

        });
    }

    public static void clear(Context context, ImageView imageView) {
        Glide.with(context).clear(imageView);
    }


    public interface DrawableCallback {
        void onLoadSuccess(Drawable drawable);

        void onLoadFailed();
    }


}
