package com.basis.net.oklib.api.body;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * file 类型 参数
 */
public class FileBody implements IBody {
    private File body;
    private String mediaType;

    public FileBody(String mediaType, File body) {
        this.mediaType = mediaType;
        this.body = body;
    }

    public String name() {
        return null == body ? "" : body.getName();
    }

    public RequestBody body() {
        return RequestBody.create(body, MediaType.parse(mediaType));
    }

}
