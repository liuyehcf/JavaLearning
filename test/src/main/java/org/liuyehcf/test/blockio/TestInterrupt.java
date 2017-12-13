package org.liuyehcf.test.blockio;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by Liuye on 2017/6/4.
 */
public class TestInterrupt {
    public static void main(String[] args) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] bytes = new byte[10];
                try {
                    System.in.read(bytes);
                } catch (IOException e) {
                    System.out.println(e.getClass().getName());
                }
            }
        });

        t.start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {

        }
    }
}
