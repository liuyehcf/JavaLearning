package org.liuyehcf.concurrent.lock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by liuye on 2017/4/10 0010.
 */
public class TicketLockDemo {

    private AtomicInteger serviceNum = new AtomicInteger();//当前服务号

    private AtomicInteger ticketNum = new AtomicInteger();//排队号

    public int lock() {
        //排队前拿个号
        int myTicketNum = ticketNum.getAndIncrement();

        while (serviceNum.get() != myTicketNum) {

        }

        System.out.println(Thread.currentThread() + " is hold the lock, order: " + myTicketNum);

        return myTicketNum;
    }

    public void unlock(int myTicket) {
        int next = myTicket + 1;
        if (!serviceNum.compareAndSet(myTicket, next)) {
            throw new RuntimeException();
        }
        System.out.println(Thread.currentThread() + " is release the lock\n");
    }
}


class TicketLockTask implements Runnable {
    private final TicketLockDemo ticketLockDemo = new TicketLockDemo();

    public void run() {
        int myTicket = ticketLockDemo.lock();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {

        } finally {
            ticketLockDemo.unlock(myTicket);
        }
    }
}

class TestTicketLock {

    public static void main(String[] args) {
        TicketLockTask task = new TicketLockTask();
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            exec.execute(task);
        }

        exec.shutdown();
    }
}