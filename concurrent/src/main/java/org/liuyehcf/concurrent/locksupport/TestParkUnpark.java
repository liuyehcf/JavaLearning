package org.liuyehcf.concurrent.locksupport;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liuye on 2017/4/8 0008.
 */
public class TestParkUnpark {
    public static void testDeadLock() {
        final ReentrantLock lock = new ReentrantLock();
        ExecutorService exec = Executors.newCachedThreadPool();
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    lock.lock();
                    LockSupport.park();
                } finally {
                    lock.unlock();
                }

            }
        };
        t.start();


        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {

        }

        try {
            System.out.println("主线程尝试获取锁");
            lock.lock();
            LockSupport.unpark(t);
        } finally {
            System.out.println("主线程释放锁");
            lock.unlock();
        }
    }


    public static void testIsInterrupted() {

        Thread t = new Thread() {
            @Override
            public void run() {
                System.out.println(currentThread() + " is going to park");
                    LockSupport.park();
                    System.out.println(currentThread() +
                            (currentThread().isInterrupted()?
                                    "is interrupted from park":
                                    "is unpark from park"));
                System.out.println();
            }
        };

        t.start();

        try {
            TimeUnit.SECONDS.sleep(1);
            System.out.println("try to interrupt " + t);
            t.interrupt();

            TimeUnit.SECONDS.sleep(5);
            System.out.println("try to unpark " + t);
            LockSupport.unpark(t);
        } catch (Exception e) {

        }
    }

    public static void main(String[] args) {
        testIsInterrupted();

    }
}
