package org.liuyehcf.aliyun.face.verification;

import com.alibaba.fastjson.JSON;
import org.liuyehcf.aliyun.AccessUtils;
import org.liuyehcf.aliyun.face.AESDecode;
import org.liuyehcf.aliyun.face.Base64EncoderUtils;
import org.liuyehcf.aliyun.face.verification.entity.FaceVerificationRequest;
import org.liuyehcf.aliyun.face.verification.entity.FaceVerificationResponse;


/**
 * Created by Liuye on 2017/12/15.
 */
public class FaceVerificationDemo {

    public static void main(String[] args) throws Exception {
        new UrlFaceVerification().verify();
        new ContentFaceVerification().verify();
    }

    private static abstract class FaceVerificationTemplate {
        private static final String URL = "https://dtplus-cn-shanghai.data.aliyuncs.com/face/verify";

        public final void verify() {
            try {
                String responseString = AESDecode.sendPost(
                        URL,
                        createRequestBody(),
                        AccessUtils.ACCESS_KEY,
                        AccessUtils.ACCESS_SECRET_KEY);

                FaceVerificationResponse faceDetectionResponse =
                        JSON.parseObject(responseString, FaceVerificationResponse.class);

                System.out.println(faceDetectionResponse);
            } catch (Exception e) {
                e.printStackTrace(System.out);
                System.err.println("AESDecode.sendPost goes wrong");
            }
        }

        protected abstract String createRequestBody();
    }

    private static final class UrlFaceVerification extends FaceVerificationTemplate {
        private static final String IMAGE_URL_1 = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1513400832568&di=3a2d09a8bf1259a424795a82b705c402&imgtype=0&src=http%3A%2F%2Fp4.gexing.com%2Fshaitu%2F20130406%2F1927%2F5160069583de6.jpg";

        private static final String IMAGE_URL_2 = "https://ss0.bdstatic.com/94oJfD_bAAcT8t7mm9GUKT-xh_/timg?image&quality=100&size=b4000_4000&sec=1513390511&di=72aa27214e3b842180acc365158407a0&src=http://i1.hdslb.com/bfs/archive/772ee24d26d86ef372f8d168d9610e2cbf55446a.jpg";

        @Override
        protected String createRequestBody() {
            FaceVerificationRequest requestBody = new FaceVerificationRequest();
            requestBody.setType(0);
            requestBody.setImageUrl1(IMAGE_URL_1);
            requestBody.setImageUrl2(IMAGE_URL_2);
            String jsonString = JSON.toJSONString(requestBody);
            System.out.println(jsonString);
            return jsonString;
        }
    }

    private static final class ContentFaceVerification extends FaceVerificationTemplate {
        private static final String PATH_1 = "aliyun/src/main/resources/faceIdentification/verification_1.jpeg";
        private static final String PATH_2 = "aliyun/src/main/resources/faceIdentification/verification_2.jpeg";

        @Override
        protected String createRequestBody() {
            FaceVerificationRequest requestBody = new FaceVerificationRequest();
            requestBody.setType(1);
            requestBody.setContent1(Base64EncoderUtils.encodeWithPath(PATH_1));
            requestBody.setContent2(Base64EncoderUtils.encodeWithPath(PATH_2));
            return JSON.toJSONString(requestBody);
        }
    }
}
