package org.liuyehcf.aliyun.face.detection;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.liuyehcf.aliyun.AccessUtils;
import org.liuyehcf.aliyun.face.AESDecode;
import org.liuyehcf.aliyun.face.detection.entity.FaceDetectionRequest;
import org.liuyehcf.aliyun.face.detection.entity.FaceDetectionResponse;

/**
 * Created by Liuye on 2017/12/15.
 */
public class FaceDetectionDemo {

    private static final String URL = "https://dtplus-cn-shanghai.data.aliyuncs.com/face/detect";

    private static final String IMAGE_URL = "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3746075707,1914896074&fm=27&gp=0.jpg";

    private static String createRequestBody() {
        FaceDetectionRequest requestBody = new FaceDetectionRequest();
        requestBody.setType(0);
        requestBody.setImageUrl(IMAGE_URL);
        String jsonString=JSON.toJSONString(requestBody);
        System.out.println(jsonString);
        return jsonString;
    }

    public static void main(String[] args) throws Exception {
        String responseString = AESDecode.sendPost(
                URL,
                createRequestBody(),
                AccessUtils.ACCESS_KEY,
                AccessUtils.ACCESS_SECRET_KEY);

        FaceDetectionResponse faceDetectionResponse =
                JSON.parseObject(
                        responseString,
                        new TypeReference<FaceDetectionResponse>() {
                        });

        System.out.println(faceDetectionResponse);
    }
}



