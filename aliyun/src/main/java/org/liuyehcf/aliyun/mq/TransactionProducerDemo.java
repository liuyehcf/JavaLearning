package org.liuyehcf.aliyun.mq;

import com.aliyun.openservices.ons.api.Message;
import com.aliyun.openservices.ons.api.ONSFactory;
import com.aliyun.openservices.ons.api.PropertyKeyConst;
import com.aliyun.openservices.ons.api.SendResult;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionChecker;
import com.aliyun.openservices.ons.api.transaction.LocalTransactionExecuter;
import com.aliyun.openservices.ons.api.transaction.TransactionProducer;
import com.aliyun.openservices.ons.api.transaction.TransactionStatus;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.client.log.ClientLogger;
import org.liuyehcf.aliyun.AccessUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;

/**
 * Created by HCF on 2017/12/12.
 */
public class TransactionProducerDemo {
    private final static Logger log = LoggerFactory.getLogger(TransactionProducerDemo.class); // 用户需要设置自己的log, 记录日志便于排查问题

    public static void main(String[] args) throws InterruptedException {
        final BusinessService businessService = new BusinessService(); // 本地业务Service
        Properties properties = new Properties();
        // 您在控制台创建的Producer ID。注意：事务消息的Producer ID不能与其他类型消息的Producer ID共用
        properties.put(PropertyKeyConst.ProducerId, "PID_org_liuyehcf_mq_test_productor1");
        // 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.AccessKey, AccessUtils.ACCESS_KEY);
        // 阿里云身份验证，在阿里云服务器管理控制台创建
        properties.put(PropertyKeyConst.SecretKey, AccessUtils.ACCESS_SECRET_KEY);
        // 设置 TCP 接入域名（此处以公共云生产环境为例）
        properties.put(PropertyKeyConst.ONSAddr,
                "http://onsaddr-internet.aliyun.com/rocketmq/nsaddr4client-internet");
        TransactionProducer producer = ONSFactory.createTransactionProducer(properties,
                new LocalTransactionCheckerImpl());
        producer.start();
        Message msg = new Message("org_liuyehcf_mq_test_topic1", "test_transaction_tag", "This is a Transaction MQ".getBytes());
        try {
            SendResult sendResult = producer.send(msg, new LocalTransactionExecuter() {
                @Override
                public TransactionStatus execute(Message msg, Object arg) {
                    // 消息ID(有可能消息体一样，但消息ID不一样, 当前消息ID在控制台无法查询)
                    String msgId = msg.getMsgID();
                    // 消息体内容进行crc32, 也可以使用其它的如MD5
                    long crc32Id = HashUtil.crc32Code(msg.getBody());
                    // 消息ID和crc32id主要是用来防止消息重复
                    // 如果业务本身是幂等的, 可以忽略, 否则需要利用msgId或crc32Id来做幂等
                    // 如果要求消息绝对不重复, 推荐做法是对消息体body使用crc32或md5来防止重复消息
                    Object businessServiceArgs = new Object();
                    TransactionStatus transactionStatus = TransactionStatus.Unknow;
                    try {
                        boolean isCommit =
                                businessService.execbusinessService(businessServiceArgs);
                        if (isCommit) {
                            // 本地事务成功、提交消息
                            transactionStatus = TransactionStatus.CommitTransaction;
                        } else {
                            // 本地事务失败、回滚消息
                            transactionStatus = TransactionStatus.RollbackTransaction;
                        }
                    } catch (Exception e) {
                        log.error("Message Id:{}", msgId, e);
                    }
                    System.out.println(msg.getMsgID());
                    log.warn("Message Id:{}transactionStatus:{}", msgId, transactionStatus.name());
                    return transactionStatus;
                }
            }, null);
        } catch (Exception e) {
            // 消息发送失败，需要进行重试处理，可重新发送这条消息或持久化这条数据进行补偿处理
            System.out.println(new Date() + " Send mq message failed. Topic is:" + msg.getTopic());
            e.printStackTrace();
        }
        // demo example 防止进程退出(实际使用不需要这样)
        TimeUnit.MILLISECONDS.sleep(Integer.MAX_VALUE);
    }


    private static final class LocalTransactionCheckerImpl implements LocalTransactionChecker {
        private final static Logger log = ClientLogger.getLog();
        final BusinessService businessService = new BusinessService();

        @Override
        public TransactionStatus check(Message msg) {
            //消息ID(有可能消息体一样，但消息ID不一样, 当前消息属于Half 消息，所以消息ID在控制台无法查询)
            String msgId = msg.getMsgID();
            //消息体内容进行crc32, 也可以使用其它的方法如MD5
            long crc32Id = HashUtil.crc32Code(msg.getBody());
            //消息ID、消息本 crc32Id主要是用来防止消息重复
            //如果业务本身是幂等的, 可以忽略, 否则需要利用msgId或crc32Id来做幂等
            //如果要求消息绝对不重复, 推荐做法是对消息体使用crc32或md5来防止重复消息.
            //业务自己的参数对象, 这里只是一个示例, 实际需要用户根据情况来处理
            Object businessServiceArgs = new Object();
            TransactionStatus transactionStatus = TransactionStatus.Unknow;
            try {
                boolean isCommit = businessService.checkbusinessService(businessServiceArgs);
                if (isCommit) {
                    //本地事务已成功、提交消息
                    transactionStatus = TransactionStatus.CommitTransaction;
                } else {
                    //本地事务已失败、回滚消息
                    transactionStatus = TransactionStatus.RollbackTransaction;
                }
            } catch (Exception e) {
                log.error("Message Id:{}", msgId, e);
            }
            log.warn("Message Id:{}transactionStatus:{}", msgId, transactionStatus.name());
            return transactionStatus;
        }
    }


    private static final class HashUtil {
        public static long crc32Code(byte[] bytes) {
            CRC32 crc32 = new CRC32();
            crc32.update(bytes);
            return crc32.getValue();
        }
    }

    private static final class BusinessService {
        boolean checkbusinessService(Object arg) {
            return true;
        }

        boolean execbusinessService(Object arg) {
            return true;
        }
    }
}