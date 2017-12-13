package org.liuyehcf.concurrent.locksupport;

import java.util.concurrent.TimeUnit;

/**
 * Created by liuye on 2017/4/8 0008.
 */
public class TestWaitNotify {
    public static void testIsInterrupted() {
        final Object monitor = new Object();

        Thread t = new Thread() {
            @Override
            public void run() {
                System.out.println(currentThread() + " is going to wait");
                synchronized (monitor) {
                    try {
                        monitor.wait();
                        System.out.println(currentThread() + " is notify from wait");
                    } catch (InterruptedException e) {
                        System.out.println(currentThread() + " is interrupted from wait");
                    }
                }
            }
        };

        t.start();

        try {
            TimeUnit.SECONDS.sleep(1);
            System.out.println("try to interrupt " + t);
            t.interrupt();

            TimeUnit.SECONDS.sleep(1);
            System.out.println("try to notify " + t);
            synchronized (monitor) {
                monitor.notify();
            }
        } catch (Exception e) {

        }

    }

    public static void main(String[] args) {
        testIsInterrupted();
    }
}
