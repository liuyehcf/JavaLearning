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
    private float[] faceProb;

    @JSONField(name = "pose")
    private float[] pose;

    @JSONField(name = "landmark_num")
    private int landmarkNum;

    @JSONField(name = "landmark")
    private float[] landmark;

    @JSONField(name = "iris")
    private float[] iris;

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

    public float[] getFaceProb() {
        return faceProb;
    }

    public void setFaceProb(float[] faceProb) {
        this.faceProb = faceProb;
    }

    public float[] getPose() {
        return pose;
    }

    public void setPose(float[] pose) {
        this.pose = pose;
    }

    public int getLandmarkNum() {
        return landmarkNum;
    }

    public void setLandmarkNum(int landmarkNum) {
        this.landmarkNum = landmarkNum;
    }

    public float[] getLandmark() {
        return landmark;
    }

    public void setLandmark(float[] landmark) {
        this.landmark = landmark;
    }

    public float[] getIris() {
        return iris;
    }

    public void setIris(float[] iris) {
        this.iris = iris;
    }

    @Override
    public String toString() {
        return "errno: " + getErrno() + "\n"
                + "errMsg: " + getErrMsg() + "\n"
                + "requestId: " + getRequestId() + "\n"
                + "faceNum: " + getFaceNum() + "\n"
                + "face+rect: " + Arrays.toString(getFaceRect()) + "\n"
                + "face+prob: " + Arrays.toString(getFaceProb()) + "\n"
                + "pose: " + Arrays.toString(getPose()) + "\n"
                + "landmarkNum: " + getLandmarkNum() + "\n"
                + "landmark: " + Arrays.toString(getLandmark()) + "\n"
                + "iris: " + Arrays.toString(getIris());
    }
}
