package com.basis.net.oklib.api.body;


import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class BitmapBody implements IBody {
    private Bitmap body;
    private String name;

    public BitmapBody(String name, Bitmap body) {
        this.name = name;
        this.body = body;
    }

    public String name() {
        return null == name ? System.nanoTime() + "bitmap.jpg" : name;
    }

    public RequestBody body() {
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return MediaType.parse("image/png");
            }

            @Override
            public void writeTo(BufferedSink bufferedSink) throws IOException {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(1024 * 8);
                body.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                bufferedSink.write(baos.toByteArray());
            }
        };
    }

}
