package org.liuyehcf.aliyun.face.detection;

import com.alibaba.fastjson.JSON;
import org.liuyehcf.aliyun.AccessUtils;
import org.liuyehcf.aliyun.face.detection.entity.FaceDetectionRequestBodyEntity;

/**
 * Created by Liuye on 2017/12/15.
 */
public class FaceDetectionDemo {

    private static final String URL = "https://dtplus-cn-shanghai.data.aliyuncs.com/face/detect";

    private static final String IMAGE_URL = "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3746075707,1914896074&fm=27&gp=0.jpg";

    private static String createRequestBody() {
        FaceDetectionRequestBodyEntity requestBodyEntity = new FaceDetectionRequestBodyEntity();
        requestBodyEntity.setType(0);
        requestBodyEntity.setImage_url(IMAGE_URL);
        return JSON.toJSONString(requestBodyEntity);
    }

    public static void main(String[] args) throws Exception {
        String responseString = AESDecode.sendPost(
                URL,
                createRequestBody(),
                AccessUtils.ACCESS_KEY,
                AccessUtils.ACCESS_SECRET_KEY);

        System.out.println(responseString);
    }
}



