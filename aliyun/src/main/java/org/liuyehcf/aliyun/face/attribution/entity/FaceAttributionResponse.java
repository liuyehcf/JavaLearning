package org.liuyehcf.aliyun.face.attribution.entity;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Arrays;

/**
 * Created by HCF on 2017/12/16.
 */
public class FaceAttributionResponse {
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

    @JSONField(name = "gender")
    private int[] gender;

    @JSONField(name = "age")
    private int[] age;

    @JSONField(name = "expression")
    private int[] expression;

    @JSONField(name = "glass")
    private int[] glass;

    @JSONField(name = "dense_fea_len")
    private int denseFeaLen;

    @JSONField(name = "dense_fea")
    private double[] denseFea;

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

    public int[] getGender() {
        return gender;
    }

    public void setGender(int[] gender) {
        this.gender = gender;
    }

    public int[] getAge() {
        return age;
    }

    public void setAge(int[] age) {
        this.age = age;
    }

    public int[] getExpression() {
        return expression;
    }

    public void setExpression(int[] expression) {
        this.expression = expression;
    }

    public int[] getGlass() {
        return glass;
    }

    public void setGlass(int[] glass) {
        this.glass = glass;
    }

    public int getDenseFeaLen() {
        return denseFeaLen;
    }

    public void setDenseFeaLen(int denseFeaLen) {
        this.denseFeaLen = denseFeaLen;
    }

    public double[] getDenseFea() {
        return denseFea;
    }

    public void setDenseFea(double[] denseFea) {
        this.denseFea = denseFea;
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
                + "iris: " + Arrays.toString(getIris()) + "\n"
                + "gender: " + Arrays.toString(getGender()) + "\n"
                + "age: " + Arrays.toString(getAge()) + "\n"
                + "expression: " + Arrays.toString(getExpression()) + "\n"
                + "glass: " + Arrays.toString(getGlass()) + "\n"
                + "dense_fea_len: " + getDenseFeaLen() + "\n"
                + "denseFea: " + Arrays.toString(getDenseFea());
    }
}
