package org.liuyehcf.concurrent.lock;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by liuye on 2017/4/8 0008.
 */
class QNode {
    volatile boolean locked;
}


public class CLHLockDemo {
    AtomicReference<QNode> tail = new AtomicReference<QNode>(new QNode());
    ThreadLocal<QNode> currNode;//在本地变量上自旋

    AtomicInteger order = new AtomicInteger();

    public CLHLockDemo() {
        tail = new AtomicReference<QNode>(new QNode());
        currNode = new ThreadLocal<QNode>() {
            protected QNode initialValue() {
                return new QNode();
            }
        };
    }

    public void lock() {
        QNode curr = this.currNode.get();
        curr.locked = true;

        //将当前节点通过CAS操作加到队列尾，返回原先的队列尾，作为它的前继节点
        QNode prev = tail.getAndSet(curr);

        int myOrder = order.getAndIncrement();

        while (prev.locked) {
            //在本地变量prev上自旋
        }
        System.out.println(Thread.currentThread() + " is hold the lock, order: " + myOrder);
    }

    public void unlock() {
        QNode qnode = currNode.get();
        qnode.locked = false;
        System.out.println(Thread.currentThread() + " is release the lock\n");
    }
}


class CLHLockTask implements Runnable {
    private final CLHLockDemo clhLockDemo = new CLHLockDemo();

    public void run() {
        try {
            clhLockDemo.lock();
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {

        } finally {
            clhLockDemo.unlock();
        }
    }
}

class TestCLHLock {

    public static void main(String[] args) {
        CLHLockTask task = new CLHLockTask();
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 10; i++) {
            exec.execute(task);
        }

        exec.shutdown();
    }
}
