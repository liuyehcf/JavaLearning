package org.liuyehcf.aliyun.face.attribution;

import com.alibaba.fastjson.JSON;
import org.liuyehcf.aliyun.AccessUtils;
import org.liuyehcf.aliyun.face.AESDecode;
import org.liuyehcf.aliyun.face.attribution.entity.FaceAttributionRequest;
import org.liuyehcf.aliyun.face.attribution.entity.FaceAttributionResponse;

/**
 * Created by HCF on 2017/12/16.
 */
public class FaceAttributionDemo {
    private static final String URL = "https://dtplus-cn-shanghai.data.aliyuncs.com/face/attribute";

    private static final String IMAGE_URL = "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3746075707,1914896074&fm=27&gp=0.jpg";

    private static String createRequestBody() {
        FaceAttributionRequest requestBody = new FaceAttributionRequest();
        requestBody.setType(0);
        requestBody.setImageUrl(IMAGE_URL);
        String jsonString = JSON.toJSONString(requestBody);
        System.out.println(jsonString);
        return jsonString;
    }

    public static void main(String[] args) throws Exception {
        String responseString = AESDecode.sendPost(
                URL,
                createRequestBody(),
                AccessUtils.ACCESS_KEY,
                AccessUtils.ACCESS_SECRET_KEY);

        FaceAttributionResponse faceDetectionResponse =
                JSON.parseObject(
                        responseString,
                        FaceAttributionResponse.class);

        System.out.println(faceDetectionResponse);
    }
}
