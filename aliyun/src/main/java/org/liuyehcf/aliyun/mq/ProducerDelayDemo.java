package org.liuyehcf.aliyun.mq;

import com.aliyun.openservices.ons.api.*;

import java.util.Date;
import java.util.Properties;

/**
 * Created by HCF on 2017/12/12.
 */
public class ProducerDelayDemo {
    public static void main(String[] args) {
        Properties properties = new Properties();
        // 您在控制台创建的 Producer ID
        properties.put(PropertyKeyConst.ProducerId, "PID_org_liuyehcf_mq_test_productor1");
        // AccessKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.AccessKey, "LTAIROQDd1PQvWnk");
        // SecretKey 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.SecretKey, "3QdEITYZ8G6mtdjySaytP8QmBxlexT");
        // 设置 TCP 接入域名（此处以公共云生产环境为例）
        properties.put(PropertyKeyConst.ONSAddr,
                "http://onsaddr-internet.aliyun.com/rocketmq/nsaddr4client-internet");
        Producer producer = ONSFactory.createProducer(properties);
        // 在发送消息前，必须调用 start 方法来启动 Producer，只需调用一次即可。
        producer.start();
        Message msg = new Message( //
                // 您在控制台创建的Topic
                "org_liuyehcf_mq_test_topic1",
                // Message Tag, 可理解为Gmail中的标签，对消息进行再归类，方便Consumer指定过滤条件在MQ服务器过滤
                "test_delay_tag",
                // Message Body 可以是任何二进制形式的数据， MQ 不做任何干预，需要 Producer 与 Consumer 协商好一致的序列化和反序列化方式
                "This is a Delay MQ".getBytes());
        // 设置代表消息的业务关键属性，请尽可能全局唯一。
        // 以方便您在无法正常收到消息情况下，可通过 MQ 控制台查询消息并补发。
        // 注意：不设置也不会影响消息正常收发
        msg.setKey("test_delay_key");
        // 延时时间单位为毫秒（ms），指定一个时刻，在这个时刻之后才能被消费，这个例子表示 3秒 后才能被消费
        long delayTime = 3000;
        msg.setStartDeliverTime(System.currentTimeMillis() + delayTime);
        try {
            SendResult sendResult = producer.send(msg);
            // 同步发送消息，只要不抛异常就是成功
            if (sendResult != null) {
                System.out.println(new Date() + " Send mq message success. Topic is:" + msg.getTopic() + " msgId is: " + sendResult.getMessageId());
            }
        } catch (Exception e) {
            // 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理
            System.out.println(new Date() + " Send mq message failed. Topic is:" + msg.getTopic());
            e.printStackTrace();
        }
        // 在应用退出前，销毁Producer对象<br>
        // 注意：如果不销毁也没有问题
        producer.shutdown();
    }
}
