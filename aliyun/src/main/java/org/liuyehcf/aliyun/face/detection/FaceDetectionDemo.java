package org.liuyehcf.aliyun.face.detection;

import com.alibaba.fastjson.JSON;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.liuyehcf.aliyun.face.detection.entity.FaceDetectionRequestBodyEntity;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by Liuye on 2017/12/15.
 */
public abstract class FaceDetectionDemo {

    private final String URL = "https://dtplus-cn-shanghai.data.aliyuncs.com/face/detectWithContent";

    public static void detectWithUrl() throws IOException {
        new UrlDetection().detect();
    }

    public static void detectWithContent() throws IOException {
        new ContentDetection().detect();
    }

    public void detect() throws IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost hp = new HttpPost(URL);

            FaceDetectionRequestBodyEntity requestBodyEntity = getRequestBodyEntity();

            String requestJson = JSON.toJSONString(requestBodyEntity);

            StringEntity se = new StringEntity(requestJson, ContentType.create("application/json", "UTF-8"));
            hp.setEntity(se);

            CloseableHttpResponse response = null;
            try {
                response = httpclient.execute(hp);

                System.out.println(response.getStatusLine());
                HttpEntity responseEntity = response.getEntity();

                // do something useful with the response body
                // and ensure it is fully consumed
                // EntityUtils.consume(responseEntity);

                String responseJson = EntityUtils.toString(responseEntity);

                System.out.println(responseJson);

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
    }

    abstract protected FaceDetectionRequestBodyEntity getRequestBodyEntity();

    private static final class UrlDetection extends FaceDetectionDemo {
        private final String IMAGE_URL = "https://image.baidu.com/search/detail?ct=503316480&z=0&ipn=d&word=%E6%AF%95%E4%B8%9A%E7%85%A7&hs=2&pn=5&spn=0&di=32076968130&pi=0&rn=1&tn=baiduimagedetail&is=0%2C0&ie=utf-8&oe=utf-8&cl=2&lm=-1&cs=557942977%2C1861119941&os=1413633855%2C1938280489&simid=0%2C0&adpicid=0&lpn=0&ln=30&fr=ala&fm=&sme=&cg=&bdtype=13&oriquery=%E6%AF%95%E4%B8%9A%E7%85%A7&objurl=http%3A%2F%2Fimgsrc.baidu.com%2Fimgad%2Fpic%2Fitem%2Fe1fe9925bc315c60351acef087b1cb1349547725.jpg&fromurl=ippr_z2C%24qAzdH3FAzdH3Fooo_z%26e3Bq7wg3tg2_z%26e3Bv54AzdH3Ft42k7yAzdH3Fri8dbm-ranl0a_z%26e3Bip4s&gsm=0";

        @Override
        protected FaceDetectionRequestBodyEntity getRequestBodyEntity() {
            FaceDetectionRequestBodyEntity requestBodyEntity = new FaceDetectionRequestBodyEntity();
            requestBodyEntity.setType(0);
            requestBodyEntity.setImage_url(IMAGE_URL);
            return requestBodyEntity;
        }
    }

    private static final class ContentDetection extends FaceDetectionDemo {
        private final File IMAGE_FILE = new File("aliyun/target/classes/faceIdentification/faceDetection/testPic1.jpg");

        @Override
        protected FaceDetectionRequestBodyEntity getRequestBodyEntity() {
            FaceDetectionRequestBodyEntity requestBodyEntity = new FaceDetectionRequestBodyEntity();


            requestBodyEntity.setType(1);
            requestBodyEntity.setContent(
                    new String(
                            Base64.encodeBase64(
                                    getBytesFromImage(IMAGE_FILE))));
            return requestBodyEntity;
        }

        private byte[] getBytesFromImage(File file) {
            if (file.length() > Integer.MAX_VALUE) {
                System.err.println("图片太大");
                throw new RuntimeException();
            }
            byte[] bytes = new byte[(int) file.length()];

            try {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(
                        new FileInputStream(file));
                bufferedInputStream.read(bytes);
                return bytes;
            } catch (IOException e) {
                System.err.printf("打开图片文件失败");
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        detectWithUrl();

        System.out.println("\n\n\n<<<<<<<<<<<<<<<<<<<<<\n\n\n");

        detectWithContent();
    }
}



