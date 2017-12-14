package org.liuyehcf.aliyun.smsg;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * Created by Liuye on 2017/12/13.
 */
public class SendShortMessageDemo {
    //产品名称:云通信短信API产品,开发者无需替换
    private final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    private final String domain = "dysmsapi.aliyuncs.com";

    //短信签名-可在短信控制台中找到
    private final String signName = "贺辰枫";
    //短信模板-可在短信控制台中找到
    private final String templateCode = "SMS_117295305";

    //手机号
    private final String[] phoneNumbers = {"18601925625", "13456900808", "13805761625", "18958170178","18600166393"};

    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    private final String accessKeyId = "LTAIROQDd1PQvWnk";
    private final String accessKeySecret = "3QdEITYZ8G6mtdjySaytP8QmBxlexT";

    private final int LENGTH_OF_VERIFICATION_CODE = 6;

    private IAcsClient acsClient = null;

    private Random random = new Random();

    private SendSmsResponse sendSmsTo(String phoneNumber) {
        setSystemProperties();

        initIAcsClient();

        SendSmsRequest request = createSendSmsRequest(phoneNumber);

        SendSmsResponse sendSmsResponse = null;
        try {
            sendSmsResponse = acsClient.getAcsResponse(request);
        } catch (ClientException exception) {
            System.err.println("获取SendSmsResponse失败");
            throw new RuntimeException(exception);
        }

        return sendSmsResponse;
    }

    private void setSystemProperties() {
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
    }

    private void initIAcsClient() {
        if (acsClient == null) {
            try {
                //初始化acsClient,暂不支持region化
                IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
                DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
                acsClient = new DefaultAcsClient(profile);
            } catch (ClientException exception) {
                System.err.println("初始化IAcsClient失败");
                throw new RuntimeException(exception);
            }
        }
    }

    private SendSmsRequest createSendSmsRequest(String phoneNumber) {
        //组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(phoneNumber);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName(signName);
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(templateCode);

        String code = getRandomVerificationCode();

        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        //request.setTemplateParam("{\"code\":\"" + code + "\", \"message\":\"" + content + "\"}");
        request.setTemplateParam("{\"code\":\"" + code + "\"}");

        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        request.setOutId("yourOutId");

        return request;
    }

    private String getRandomVerificationCode() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < LENGTH_OF_VERIFICATION_CODE; i++) {
            sb.append(random.nextInt(10));
        }

        return sb.toString();
    }


    private QuerySendDetailsResponse querySendDetails(String phoneNumber, String bizId) {

        setSystemProperties();

        initIAcsClient();

        QuerySendDetailsRequest request = createQueryRequest(phoneNumber, bizId);

        QuerySendDetailsResponse querySendDetailsResponse = null;
        try {
            querySendDetailsResponse = acsClient.getAcsResponse(request);
        } catch (ClientException exception) {
            System.err.println("获取QuerySendDetailsResponse失败");
            throw new RuntimeException(exception);
        }
        return querySendDetailsResponse;
    }

    private QuerySendDetailsRequest createQueryRequest(String phoneNumber, String bizId) {
        //组装请求对象
        QuerySendDetailsRequest request = new QuerySendDetailsRequest();
        //必填-号码
        request.setPhoneNumber(phoneNumber);
        //可选-流水号
        request.setBizId(bizId);
        //必填-发送日期 支持30天内记录查询，格式yyyyMMdd
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
        request.setSendDate(ft.format(new Date()));
        //必填-页大小
        request.setPageSize(10L);
        //必填-当前页码从1开始计数
        request.setCurrentPage(1L);

        return request;
    }

    public void sendSmsPipeLine() {

        for (String phoneNumber : phoneNumbers) {
            //发短信
            SendSmsResponse response = sendSmsTo(phoneNumber);
            System.out.println("短信接口返回的数据----------------");
            System.out.println("Code=" + response.getCode());
            System.out.println("Message=" + response.getMessage());
            System.out.println("RequestId=" + response.getRequestId());
            System.out.println("BizId=" + response.getBizId());

            try {
                Thread.sleep(3000L);
            } catch (InterruptedException e) {
                // ignore when interrupted
            }

            //查明细
            if (response.getCode() != null && response.getCode().equals("OK")) {
                QuerySendDetailsResponse querySendDetailsResponse = querySendDetails(phoneNumber, response.getBizId());
                System.out.println("短信明细查询接口返回数据----------------");
                System.out.println("Code=" + querySendDetailsResponse.getCode());
                System.out.println("Message=" + querySendDetailsResponse.getMessage());
                int i = 0;
                for (QuerySendDetailsResponse.SmsSendDetailDTO smsSendDetailDTO : querySendDetailsResponse.getSmsSendDetailDTOs()) {
                    System.out.println("SmsSendDetailDTO[" + i + "]:");
                    System.out.println("Content=" + smsSendDetailDTO.getContent());
                    System.out.println("ErrCode=" + smsSendDetailDTO.getErrCode());
                    System.out.println("OutId=" + smsSendDetailDTO.getOutId());
                    System.out.println("PhoneNum=" + smsSendDetailDTO.getPhoneNum());
                    System.out.println("ReceiveDate=" + smsSendDetailDTO.getReceiveDate());
                    System.out.println("SendDate=" + smsSendDetailDTO.getSendDate());
                    System.out.println("SendStatus=" + smsSendDetailDTO.getSendStatus());
                    System.out.println("Template=" + smsSendDetailDTO.getTemplateCode());
                }
                System.out.println("TotalCount=" + querySendDetailsResponse.getTotalCount());
                System.out.println("RequestId=" + querySendDetailsResponse.getRequestId());
            }

            System.out.println("\n\n<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n\n");
        }
    }

    public static void main(String[] args) throws ClientException, InterruptedException {
        new SendShortMessageDemo().sendSmsPipeLine();
    }
}
