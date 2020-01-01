package org.liuyehcf.jmh.serialize;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.HashMap;
import java.util.Map;
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
    }

    public static void main(String[] args) throws Exception {
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
}
