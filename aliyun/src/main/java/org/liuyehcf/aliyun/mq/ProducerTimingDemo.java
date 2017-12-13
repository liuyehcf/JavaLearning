package org.liuyehcf.aliyun.mq;

import com.aliyun.openservices.ons.api.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by HCF on 2017/12/12.
 */
public class ProducerTimingDemo {
    public static void main(String[] args) {
        Properties properties = new Properties();
        //您在 MQ 控制台创建的Producer ID
        properties.put(PropertyKeyConst.ProducerId, "PID_org_liuyehcf_mq_test_productor1");
        // 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.AccessKey, "LTAIROQDd1PQvWnk");
        // 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.SecretKey, "3QdEITYZ8G6mtdjySaytP8QmBxlexT");
        // 设置 TCP 接入域名（此处以公共云生产环境为例）
        properties.put(PropertyKeyConst.ONSAddr,
                "http://onsaddr-internet.aliyun.com/rocketmq/nsaddr4client-internet");
        Producer producer = ONSFactory.createProducer(properties);
        // 在发送消息前，必须调用start方法来启动Producer，只需调用一次即可。
        producer.start();
        Message msg = new Message( //
                // Message所属的Topic
                "org_liuyehcf_mq_test_topic1",
                // Message Tag 可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在MQ服务器过滤
                "test_timing_tag",
                // Message Body 可以是任何二进制形式的数据， MQ不做任何干预，需要Producer与Consumer协商好一致的序列化和反序列化方式
                "This is a Timing MQ".getBytes());
        // 设置代表消息的业务关键属性，请尽可能全局唯一
        // 以方便您在无法正常收到消息情况下，可通过MQ控制台查询消息并补发。
        // 注意：不设置也不会影响消息正常收发
        msg.setKey("test_timing_key");
        /**
         * 定时消息投递，设置投递的具体时间戳，单位毫秒例如2016-03-07 16:21:00投递
         */
        try {
            long timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2017-12-12 15:00:00").getTime();
            msg.setStartDeliverTime(timeStamp);
            // 发送消息，只要不抛异常就是成功
            SendResult sendResult = producer.send(msg);
            System.out.println("Message Id:" + sendResult.getMessageId());
        }
        catch (Exception e) {
            // 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理
            System.out.println(new Date() + " Send mq message failed. Topic is:" + msg.getTopic());
            e.printStackTrace();
        }
        // 在应用退出前，销毁 Producer 对象
        // 注意：如果不销毁也没有问题
        producer.shutdown();
    }
}
