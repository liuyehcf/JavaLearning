package org.liuyehcf.aliyun.face.detection.entity;

/**
 * Created by Liuye on 2017/12/15.
 */

public class FaceDetectionRequestBodyEntity {
    private int type;

    private String image_url;

    private String content;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
