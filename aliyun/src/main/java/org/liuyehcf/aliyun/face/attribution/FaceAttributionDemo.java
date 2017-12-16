package org.liuyehcf.aliyun.face.attribution;

import com.alibaba.fastjson.JSON;
import org.liuyehcf.aliyun.AccessUtils;
import org.liuyehcf.aliyun.face.AESDecode;
import org.liuyehcf.aliyun.face.Base64EncoderUtils;
import org.liuyehcf.aliyun.face.attribution.entity.FaceAttributionRequest;
import org.liuyehcf.aliyun.face.attribution.entity.FaceAttributionResponse;

/**
 * Created by HCF on 2017/12/16.
 */
public class FaceAttributionDemo {


    private static abstract class FaceAttributionTemplate {
        private static final String URL = "https://dtplus-cn-shanghai.data.aliyuncs.com/face/attribute";

        public final void attribute() {
            try {
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
            } catch (Exception e) {
                e.printStackTrace(System.out);
                System.err.println("AESDecode.sendPost goes wrong");
            }
        }

        protected abstract String createRequestBody();

    }

    private static final class UrlFaceAttribution extends FaceAttributionTemplate {
        private static final String IMAGE_URL = "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3746075707,1914896074&fm=27&gp=0.jpg";

        @Override
        protected String createRequestBody() {
            FaceAttributionRequest requestBody = new FaceAttributionRequest();
            requestBody.setType(0);
            requestBody.setImageUrl(IMAGE_URL);
            String jsonString = JSON.toJSONString(requestBody);
            System.out.println(jsonString);
            return jsonString;
        }
    }

    private static final class ContentFaceAttribution extends FaceAttributionTemplate {
        private static final String PATH = "aliyun/src/main/resources/faceIdentification/detection_1.jpeg";

        @Override
        protected String createRequestBody() {
            FaceAttributionRequest requestBody = new FaceAttributionRequest();
            requestBody.setType(1);
            requestBody.setContent(Base64EncoderUtils.encodeWithPath(PATH));
            return JSON.toJSONString(requestBody);
        }
    }

    public static void main(String[] args) throws Exception {
        new UrlFaceAttribution().attribute();
        new ContentFaceAttribution().attribute();
    }
}
