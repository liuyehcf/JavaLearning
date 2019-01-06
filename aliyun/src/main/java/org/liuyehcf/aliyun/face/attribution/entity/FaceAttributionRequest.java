package org.liuyehcf.aliyun.face.attribution.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by HCF on 2017/12/16.
 */
public class FaceAttributionRequest {
    @JSONField(name = "type")
    private int type;

    @JSONField(name = "image_url")
    private String imageUrl;

    @JSONField(name = "content")
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
