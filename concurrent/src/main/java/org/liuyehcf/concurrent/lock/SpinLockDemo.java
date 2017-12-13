package org.liuyehcf.concurrent.lock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liuye on 2017/4/8 0008.
 */
public class SpinLockDemo {

    private AtomicInteger state = new AtomicInteger();

    private AtomicInteger order = new AtomicInteger();

    public void lock() {
        int myOrder = order.getAndIncrement();

        for (; ; ) {
            if (state.compareAndSet(0, 1)) {
                System.out.println(Thread.currentThread() + " is hold the lock, order: " + myOrder);
                break;
            }
        }
    }

    public void unlock() {
        if (!state.compareAndSet(1, 0)) {
            throw new RuntimeException();
        }
        System.out.println(Thread.currentThread() + " is release the lock\n");
    }
}

class SpinLockTask implements Runnable {
    private final SpinLockDemo spinLockDemo = new SpinLockDemo();

    public void run() {
        spinLockDemo.lock();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {

        } finally {
            spinLockDemo.unlock();
        }
    }
}

class TestSpinLock {

    public static void main(String[] args) {
        SpinLockTask task = new SpinLockTask();
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            exec.execute(task);
        }

        exec.shutdown();
    }
}
