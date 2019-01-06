package org.liuyehcf.aliyun.mq;

import com.aliyun.openservices.ons.api.*;
import org.liuyehcf.aliyun.AccessUtils;

import java.util.Properties;

/**
 * Created by HCF on 2017/12/12.
 */
public class ConsumerDemo {
    public static void main(String[] args) {
        Properties properties = new Properties();
        // 您在MQ控制台创建的Consumer ID
        properties.put(PropertyKeyConst.ConsumerId, "CID_org_liuyehcf_mq_test_consumer1");
        // 鉴权用AccessKey，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.AccessKey, AccessUtils.ACCESS_KEY);
        // 鉴权用SecretKey，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.SecretKey, AccessUtils.ACCESS_SECRET_KEY);
        // 设置 TCP 接入域名（此处以公共云公网环境接入为例）
        properties.put(PropertyKeyConst.ONSAddr,
                "http://onsaddr-internet.aliyun.com/rocketmq/nsaddr4client-internet");
        Consumer consumer = ONSFactory.createConsumer(properties);
        consumer.subscribe("org_liuyehcf_mq_test_topic1", "*", new MessageListener() {
            public Action consume(Message message, ConsumeContext context) {
                System.out.println("Receive: " + message);
                return Action.CommitMessage;
            }
        });
        consumer.start();
        System.out.println("Consumer Started");
    }
}
