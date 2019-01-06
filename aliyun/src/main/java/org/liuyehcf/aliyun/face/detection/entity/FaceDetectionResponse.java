package org.liuyehcf.aliyun.face.detection.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Arrays;

/**
 * Created by Liuye on 2017/12/15.
 */
public class FaceDetectionResponse {
    @JSONField(name = "errno")
    private int errno;

    @JSONField(name = "err_msg")
    private String errMsg;

    @JSONField(name = "request_id")
    private String requestId;

    @JSONField(name = "face_num")
    private int faceNum;

    @JSONField(name = "face_rect")
    private int[] faceRect;

    @JSONField(name = "face_prob")
    private double[] faceProb;

    @JSONField(name = "pose")
    private double[] pose;

    @JSONField(name = "landmark_num")
    private int landmarkNum;

    @JSONField(name = "landmark")
    private double[] landmark;

    @JSONField(name = "iris")
    private double[] iris;

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

    public int getFaceNum() {
        return faceNum;
    }

    public void setFaceNum(int faceNum) {
        this.faceNum = faceNum;
    }

    public int[] getFaceRect() {
        return faceRect;
    }

    public void setFaceRect(int[] faceRect) {
        this.faceRect = faceRect;
    }

    public double[] getFaceProb() {
        return faceProb;
    }

    public void setFaceProb(double[] faceProb) {
        this.faceProb = faceProb;
    }

    public double[] getPose() {
        return pose;
    }

    public void setPose(double[] pose) {
        this.pose = pose;
    }

    public int getLandmarkNum() {
        return landmarkNum;
    }

    public void setLandmarkNum(int landmarkNum) {
        this.landmarkNum = landmarkNum;
    }

    public double[] getLandmark() {
        return landmark;
    }

    public void setLandmark(double[] landmark) {
        this.landmark = landmark;
    }

    public double[] getIris() {
        return iris;
    }

    public void setIris(double[] iris) {
        this.iris = iris;
    }

    @Override
    public String toString() {
        return "errno: " + getErrno() + "\n"
                + "err_msg: " + getErrMsg() + "\n"
                + "request_id: " + getRequestId() + "\n"
                + "face_num: " + getFaceNum() + "\n"
                + "face_rect: " + Arrays.toString(getFaceRect()) + "\n"
                + "face_prob: " + Arrays.toString(getFaceProb()) + "\n"
                + "pose: " + Arrays.toString(getPose()) + "\n"
                + "landmark_num: " + getLandmarkNum() + "\n"
                + "landmark: " + Arrays.toString(getLandmark()) + "\n"
                + "iris: " + Arrays.toString(getIris());
    }
}
