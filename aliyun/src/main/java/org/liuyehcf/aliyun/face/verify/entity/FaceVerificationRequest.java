package org.liuyehcf.aliyun.face.verify.entity;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * Created by Liuye on 2017/12/15.
 */
public class FaceVerificationRequest {
    @JSONField(name = "type")
    private int type;

    @JSONField(name = "image_url_1")
    private String imageUrl1;

    @JSONField(name = "content_1")
    private String content1;

    @JSONField(name = "image_url_2")
    private String imageUrl2;

    @JSONField(name = "content_2")
    private String content2;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImageUrl1() {
        return imageUrl1;
    }

    public void setImageUrl1(String imageUrl1) {
        this.imageUrl1 = imageUrl1;
    }

    public String getContent1() {
        return content1;
    }

    public void setContent1(String content1) {
        this.content1 = content1;
    }

    public String getImageUrl2() {
        return imageUrl2;
    }

    public void setImageUrl2(String imageUrl2) {
        this.imageUrl2 = imageUrl2;
    }

    public String getContent2() {
        return content2;
    }

    public void setContent2(String content2) {
        this.content2 = content2;
    }
}
