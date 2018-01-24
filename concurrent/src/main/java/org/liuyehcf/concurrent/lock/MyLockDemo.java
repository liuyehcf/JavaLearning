package org.liuyehcf.concurrent.lock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Created by liuye on 2017/4/12 0012.
 */
public class MyLockDemo {

    private final Sync sync = new Sync();

    public void lock() {
        sync.acquire(1);
        System.out.println(System.nanoTime() + ", " + Thread.currentThread() + " is hold the lock");
    }

    public void unlock() {
        System.out.println(System.nanoTime() + ", " + Thread.currentThread() + " is release the lock\n");
        sync.release(1);
    }

    private static final class Sync extends AbstractQueuedSynchronizer {

        private final AtomicInteger resources = new AtomicInteger();

        @Override
        protected boolean tryAcquire(int arg) {
            return resources.compareAndSet(0, arg);
        }

        @Override
        protected boolean tryRelease(int arg) {
            resources.set(0);
            return true;
        }
    }
}


class MyLockTask implements Runnable {
    private final MyLockDemo myLockDemo = new MyLockDemo();

    public void run() {
        myLockDemo.lock();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {

        } finally {
            myLockDemo.unlock();
        }
    }
}

class TestMyLock {

    public static void main(String[] args) {
        MyLockTask task = new MyLockTask();
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            exec.execute(task);
        }

        exec.shutdown();
    }
}

