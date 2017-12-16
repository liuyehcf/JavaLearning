package org.liuyehcf.aliyun.face.verify.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Arrays;

/**
 * Created by Liuye on 2017/12/15.
 */
public class FaceVerificationResponse {
    @JSONField(name = "errno")
    private int errno;

    @JSONField(name = "err_msg")
    private String errMsg;

    @JSONField(name = "request_id")
    private String requestId;

    @JSONField(name = "confidence")
    private double confidence;

    @JSONField(name = "thresholds")
    private double[] thresholds;

    @JSONField(name = "rectA")
    private int[] rectA;

    @JSONField(name = "rectB")
    private int[] rectB;


    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    public double[] getThresholds() {
        return thresholds;
    }

    public void setThresholds(double[] thresholds) {
        this.thresholds = thresholds;
    }

    public int[] getRectA() {
        return rectA;
    }

    public void setRectA(int[] rectA) {
        this.rectA = rectA;
    }

    public int[] getRectB() {
        return rectB;
    }

    public void setRectB(int[] rectB) {
        this.rectB = rectB;
    }

    @Override
    public String toString() {
        return "errno :" + getErrno() + "\n"
                + "err_msg :" + getErrMsg() + "\n"
                + "request_id :" + getRequestId() + "\n"
                + "confidence :" + getConfidence() + "\n"
                + "thresholds :" + Arrays.toString(getThresholds()) + "\n"
                + "rectA :" + Arrays.toString(getRectA()) + "\n"
                + "rectB :" + Arrays.toString(getRectB()) + "\n";
    }
}
