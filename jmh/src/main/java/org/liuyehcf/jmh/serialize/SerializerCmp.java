package org.liuyehcf.jmh.serialize;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author chenfeng.hcf
 * @date 2019/10/15
 */
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@Threads(5)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SerializerCmp {

    private static final Map<String, String> MAP_SIZE_1 = new HashMap<>();
    private static final Map<String, String> MAP_SIZE_10 = new HashMap<>();
    private static final Map<String, String> MAP_SIZE_100 = new HashMap<>();
    private static final Map<String, Object> MAP_MQ;

    static {
        for (int i = 0; i < 1; i++) {
            MAP_SIZE_1.put(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        }

        for (int i = 0; i < 10; i++) {
            MAP_SIZE_10.put(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        }

        for (int i = 0; i < 100; i++) {
            MAP_SIZE_100.put(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        }

        MAP_MQ = JSON.parseObject("{\n" +
                "    \"context\":{\n" +
                "        \"sceneId\":\"68b300df96654843882d183a5db9c9e1\"\n" +
                "    },\n" +
                "    \"source\":\"DEVICE_EVENT\",\n" +
                "    \"sourceEvent\":{\n" +
                "        \"groupIdList\":[\n" +
                "\n" +
                "        ],\n" +
                "        \"categoryKey\":\"Camera\",\n" +
                "        \"eventType\":\"alert\",\n" +
                "        \"batchId\":\"8a79c90dee4d4b15b643f21d4e17f63d\",\n" +
                "        \"gmtCreate\":1566305419227,\n" +
                "        \"productKey\":\"a1VWNHhGOdx\",\n" +
                "        \"deviceName\":\"VB9YDUpaXfwzOdsR4Rf1\",\n" +
                "        \"eventCode\":\"AlarmEvent\",\n" +
                "        \"iotId\":\"VB9YDUpaXfwzOdsR4Rf1000101\",\n" +
                "        \"namespace\":\"ICA\",\n" +
                "        \"tenantId\":\"62878D0DFA3D4E32AF6357870FB0E1E9\",\n" +
                "        \"eventName\":\"侦测报警\",\n" +
                "        \"thingType\":\"DEVICE\",\n" +
                "        \"time\":1566305419225,\n" +
                "        \"value\":{\n" +
                "            \"AlarmPicID\":\"1110741847246\",\n" +
                "            \"AlarmType\":1\n" +
                "        }\n" +
                "    },\n" +
                "    \"timestamp\":1566305433432\n" +
                "}");
    }

    public static void test() {
        Map<String, Object> clonedMap = CloneUtils.kryoClone(MAP_MQ);

        if (!(Objects.equals(
                JSON.toJSONString(clonedMap, SerializerFeature.SortField, SerializerFeature.MapSortField),
                JSON.toJSONString(MAP_MQ, SerializerFeature.SortField, SerializerFeature.MapSortField)
        ))) {
            throw new RuntimeException();
        }
    }

    public static void main(String[] args) throws Exception {
        test();
        new SerializerCmp().mq_kryoClone();

        Options opt = new OptionsBuilder()
                .include(SerializerCmp.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

    @Benchmark
    public void size_1_shallowClone() {
        CloneUtils.shallowClone(MAP_SIZE_1);
    }

    @Benchmark
    public void size_1_bean() {
        BeanUtils.clone(MAP_SIZE_1);
    }

    @Benchmark
    public void size_1_jsonClone() {
        CloneUtils.jsonClone(MAP_SIZE_1);
    }

    @Benchmark
    public void size_1_kryoClone() {
        CloneUtils.kryoClone(MAP_SIZE_1);
    }

    @Benchmark
    public void size_1_hessianClone() {
        CloneUtils.hessianClone(MAP_SIZE_1);
    }

    @Benchmark
    public void size_1_javaClone() {
        CloneUtils.javaClone(MAP_SIZE_1);
    }

    @Benchmark
    public void size_10_shallowClone() {
        CloneUtils.shallowClone(MAP_SIZE_10);
    }

    @Benchmark
    public void size_10_bean() {
        BeanUtils.clone(MAP_SIZE_10);
    }

    @Benchmark
    public void size_10_jsonClone() {
        CloneUtils.jsonClone(MAP_SIZE_10);
    }

    @Benchmark
    public void size_10_kryoClone() {
        CloneUtils.kryoClone(MAP_SIZE_10);
    }

    @Benchmark
    public void size_10_hessianClone() {
        CloneUtils.hessianClone(MAP_SIZE_10);
    }

    @Benchmark
    public void size_10_javaClone() {
        CloneUtils.javaClone(MAP_SIZE_10);
    }

    @Benchmark
    public void size_100_shallowClone() {
        CloneUtils.shallowClone(MAP_SIZE_100);
    }

    @Benchmark
    public void size_100_bean() {
        BeanUtils.clone(MAP_SIZE_100);
    }

    @Benchmark
    public void size_100_jsonClone() {
        CloneUtils.jsonClone(MAP_SIZE_100);
    }

    @Benchmark
    public void size_100_kryoClone() {
        CloneUtils.kryoClone(MAP_SIZE_100);
    }

    @Benchmark
    public void size_100_hessianClone() {
        CloneUtils.hessianClone(MAP_SIZE_100);
    }

    @Benchmark
    public void size_100_javaClone() {
        CloneUtils.javaClone(MAP_SIZE_100);
    }

    @Benchmark
    public void mq_shallowClone() {
        CloneUtils.shallowClone(MAP_MQ);
    }

    @Benchmark
    public void mq_bean() {
        BeanUtils.clone(MAP_MQ);
    }

    @Benchmark
    public void mq_jsonClone() {
        CloneUtils.jsonClone(MAP_MQ);
    }

    @Benchmark
    public void mq_kryoClone() {
        CloneUtils.kryoClone(MAP_MQ);
    }

    @Benchmark
    public void mq_hessianClone() {
        CloneUtils.hessianClone(MAP_MQ);
    }

    @Benchmark
    public void mq_javaClone() {
        CloneUtils.javaClone(MAP_MQ);
    }
}
