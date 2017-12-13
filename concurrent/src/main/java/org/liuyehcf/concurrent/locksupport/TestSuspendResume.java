package org.liuyehcf.concurrent.locksupport;

import java.util.concurrent.TimeUnit;

/**
 * Created by liuye on 2017/4/8 0008.
 */
public class TestSuspendResume {
    public static void testIsInterrupted() {
        Thread t = new Thread() {
            @Override
            public void run() {
                System.out.println(currentThread() + " is going to suspend");
                Thread.currentThread().suspend();
                System.out.println(currentThread() + " is resumed from suspend");
            }
        };

        t.start();

        try {
            TimeUnit.SECONDS.sleep(1);
            System.out.println("try to interrupt " + t);
            t.interrupt();

            TimeUnit.SECONDS.sleep(5);
            System.out.println("try to resume " + t);
            t.resume();
        } catch (Exception e) {

        }

    }

    public static void main(String[] args){
        testIsInterrupted();
    }
}
