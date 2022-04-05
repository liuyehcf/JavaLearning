package org.liuyehcf.jmh.serialize;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author hechenfeng
 * @date 2022/4/5
 */
public class VisibilityVerification {

    @Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
    @Fork(1)
    @Threads(1)
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public static class VolatileBenchmark {
        private static AtomicLong atomicValue = new AtomicLong(0);
        private static volatile long volatileValue = 0;
        private static long cnt = 0;
        private static long tmp = 0;

        public static void main(String[] args) throws Exception {
            Options opt = new OptionsBuilder()
                    .include(VolatileBenchmark.class.getSimpleName())
                    .build();

            new Runner(opt).run();
        }

        @Benchmark
        public void atomicRead() {
            tmp = atomicValue.get();
        }

        @Benchmark
        public void atomicWrite() {
            atomicValue.set(cnt++);
        }

        @Benchmark
        public void volatileRead() {
            tmp = volatileValue;
        }

        @Benchmark
        public void volatileWrite() {
            volatileValue = cnt++;
        }
    }

    private static final class ReadTest {
        public static void main(String[] args) throws InterruptedException {
            testAtomic();
            testVolatile();
        }

        private static final long size = 1000000000;
        private static AtomicLong atomicValue = new AtomicLong(0);
        private static volatile long volatileValue = 0;

        private static void testAtomic() throws InterruptedException {
            Thread writeThread = new Thread(() -> {
                for (long i = 0; i < size; i++) {
                    atomicValue.set(i);
                }
            });

            Thread readThread = new Thread(() -> {
                long prev_value = 0;
                long diff_cnt = 0;
                long cur_value;
                for (long i = 0; i < size; i++) {
                    cur_value = atomicValue.get();
                    if (cur_value != prev_value) {
                        diff_cnt++;
                    }
                    prev_value = cur_value;
                }
                System.out.println("atomic, β=" + (diff_cnt) / (double) size);
            });
            writeThread.start();
            readThread.start();
            writeThread.join();
            readThread.join();
        }

        private static void testVolatile() throws InterruptedException {
            Thread writeThread = new Thread(() -> {
                for (long i = 0; i < size; i++) {
                    volatileValue = i;
                }
            });

            Thread readThread = new Thread(() -> {
                long prev_value = 0;
                long diff_cnt = 0;
                long cur_value;
                for (long i = 0; i < size; i++) {
                    cur_value = volatileValue;
                    if (cur_value != prev_value) {
                        diff_cnt++;
                    }
                    prev_value = cur_value;
                }
                System.out.println("volatile, β=" + (diff_cnt) / (double) size);
            });
            writeThread.start();
            readThread.start();
            writeThread.join();
            readThread.join();
        }
    }

}
