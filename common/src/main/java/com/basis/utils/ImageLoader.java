package com.basis.utils;

import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.File;

public class ImageLoader {
    public static void loadUrl(@NonNull ImageView to, String url, @DrawableRes int def) {
        loadUrl(to, url, def, Size.S_0);
    }

    /**
     * @param to   target imageView
     * @param url  URL
     * @param size Size
     */

    public static void loadUrl(ImageView to, String url, @DrawableRes int def, Size size) {
        loadUri(to, TextUtils.isEmpty(url) ? null : Uri.parse(url), def, size);
    }

    /**
     * 加载 本地路径文件
     *
     * @param localPath
     * @param to
     */
    public static void loadLocal(@NonNull ImageView to, String localPath, @DrawableRes int def) {
        loadLocal(to, localPath, def, Size.S_0);
    }


    public static void loadLocal(ImageView to, String localPath, @DrawableRes int def, Size size) {
        loadUri(to, TextUtils.isEmpty(localPath) ? null : Uri.fromFile(new File(localPath)), def, size);
    }

    public static void loadUri(@NonNull ImageView to, @NonNull Uri uri, @DrawableRes int def) {
        loadUri(to, uri, def, Size.S_0);
    }

    /**
     * @param to   target imageView
     * @param uri  Uri
     * @param size size
     */
    public static void loadUri(final ImageView to, Uri uri, @DrawableRes int def, Size size) {
        if (null == to) return;
        int[] sizes = size.size();
        if (null == uri) {
            Glide.with(to.getContext())
                    .load(def)
                    .override(sizes[0], sizes[1])
                    .into(to);
        } else {
            Glide.with(to.getContext())
                    .load(uri)
                    .placeholder(def)
                    .error(def)
                    .override(sizes[0], sizes[1])
                    .into(to);
        }

    }

    public enum Size {
        S_100(100),
        S_150(150),
        S_200(200),
        S_250(250),
        S_0(Target.SIZE_ORIGINAL);

        private final int w;
        private final int h;

        Size(int s) {
            this.w = s;
            this.h = s;
        }

        Size(int w, int h, int w1) {
            this.w = w;
            this.h = h;
        }

        public int[] size() {
            return new int[]{w, h};
        }
    }
}
