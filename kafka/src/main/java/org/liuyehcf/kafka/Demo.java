package org.liuyehcf.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.*;

import java.util.Collections;
import java.util.Properties;

/**
 * @author chenfeng.hcf
 * @date 2020/3/26
 */
public class Demo {

    private static final String TOPIC = "TEST_TOPIC";

    public static void main(String[] args) throws Exception {
        String mode = args[0];
        String endpoint = args[1];

        if ("producer".equalsIgnoreCase(mode)) {
            startProducer(endpoint);
        } else {
            startConsumer(endpoint);
        }
    }

    private static void startProducer(String endpoint) throws Exception {
        Properties props = new Properties();
        props.put("bootstrap.servers", endpoint);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        Producer<String, String> kafkaProducer = new KafkaProducer<>(props);

        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            final String key = "key" + i;
            String data = "hello kafka message:" + key;
            kafkaProducer.send(new ProducerRecord<>(TOPIC, key, data), new Callback() {
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    System.out.println("发送key" + key + "成功");
                }
            });
        }
    }

    private static void startConsumer(String endpoint) {
        Properties props = new Properties();
        props.put("bootstrap.servers", endpoint);
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singleton(TOPIC));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("partition = %d,offset = %d, key = %s, value = %s%n", record.partition(), record.offset(), record.key(), record.value());
            }
        }
    }
}
