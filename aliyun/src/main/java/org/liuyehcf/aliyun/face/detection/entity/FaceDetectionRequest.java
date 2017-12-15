package org.liuyehcf.aliyun.face.detection.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Liuye on 2017/12/15.
 */

public class FaceDetectionRequest {
    private int type;

    @JSONField(name = "image_url")
    private String imageUrl;

    private String content;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
