package org.liuyehcf.concurrent.priority;

import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 分组优先级内存栅栏
 *
 * @author hechenfeng
 * @date 2018/5/28
 */
public class GroupPriorityBarrier {

    /**
     * 允许自旋时间
     */
    private static final long SPIN_NANO_TIME = 5000L;

    /**
     * sleep时间
     */
    private static final long SLEEP_MILLISECONDS_TIME = 20;

    /**
     * namespace -> Syn实例的map
     */
    private final Map<String, Syn> synMap;

    /**
     * 可重用的barrier
     */
    private final CyclicBarrier barrier;

    /**
     * 命名空间缓存
     */
    private final ThreadLocal<String> localNamespace = new ThreadLocal<>();

    /**
     * 优先级缓存
     */
    private final ThreadLocal<Integer> localPriority = new ThreadLocal<>();


    public GroupPriorityBarrier(int size) {
        synMap = new ConcurrentHashMap<>();
        barrier = new CyclicBarrier(size);
    }

    public static void main(String[] args) {
        int namespaceNum = 50;
        int threadNum = 1000;
        int sleepTime = 2000;
        int priorityNum = 10;

        String[] namespaces = new String[namespaceNum];
        for (int i = 0; i < namespaceNum; i++) {
            namespaces[i] = UUID.randomUUID().toString();
        }

        GroupPriorityBarrier groupPriorityBarrier = new GroupPriorityBarrier(threadNum);
        Random random = new Random();
        AtomicInteger cnt = new AtomicInteger();
        List<Thread> threads = new ArrayList<>();
        Map<String, List<Integer>> accessOrderMap = new ConcurrentHashMap<>();

        for (int i = 0; i < namespaceNum; i++) {
            accessOrderMap.put(namespaces[i], new ArrayList<>());
        }

        for (int i = 0; i < threadNum; i++) {
            Thread t = new Thread(
                    () -> {
                        int priority = random.nextInt(priorityNum) + 1;
                        String namespace = namespaces[random.nextInt(namespaces.length)];
                        try {
                            TimeUnit.MILLISECONDS.sleep(random.nextInt(sleepTime));

                            groupPriorityBarrier.enter(namespace, priority);

                            System.out.println("(" + namespace + ", " + priority + ")" + " enter ");
                            System.out.flush();

                            TimeUnit.MILLISECONDS.sleep(random.nextInt(sleepTime));
                        } catch (InterruptedException e) {
                            // ignore
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            System.out.println("(" + namespace + ", " + priority + ")" + " exit ");
                            System.out.flush();
                            accessOrderMap.get(namespace).add(priority);

                            groupPriorityBarrier.exit();

                            cnt.incrementAndGet();
                        }
                    }, "Thread - " + i
            );
            threads.add(t);
            t.start();
        }

        /*
         * Join
         */
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            // ignore
        }

        System.out.println(cnt);
        System.out.flush();

        /*
         * 校验访问顺序是否一致
         */
        for (Map.Entry<String, List<Integer>> entry : accessOrderMap.entrySet()) {
            List<Integer> origin = entry.getValue();
            List<Integer> copied = new ArrayList<>(origin);

            Collections.sort(copied);
            if (!origin.equals(copied)) {
                throw new RuntimeException();
            }
        }
    }

    public void enter(String namespace, int priority) {
        /*
         * 注册命名空间以及优先级
         */
        register(namespace, priority);

        /*
         * 等待所有线程都执行完register后通过该阻塞点
         */
        sync();

        /*
         * 初始化每个Syn
         */
        initSyn(namespace);

        /*
         * 等待所有线程都执行完initSyn后通过该阻塞点
         */
        sync();

        /*
         * 加锁的具体语义
         */
        acquire(namespace, priority);
    }

    private void sync() {
        for (; ; ) {
            try {
                barrier.await();
                return;
            } catch (InterruptedException e) {
                // ignore
            } catch (BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void initSyn(String namespace) {
        Syn syn = synMap.get(namespace);

        if (syn.tryInit()) {
            /*
             * 优先级最高，只需要1个资源
             */
            int start = Syn.ORIGIN_STATE;

            List<Integer> priorities = new ArrayList<>(syn.priorityCnt.keySet());
            Collections.sort(priorities);

            for (int priority : priorities) {
                int cnt = syn.priorityCnt.get(priority);

                syn.priorityStates.put(priority, start);

                /*
                 * 下一个优先级所需的资源数为 上一个优先级所需资源数量 + 上一个优先级的数量
                 */
                start += cnt;
            }
        }
    }

    private void acquire(String namespace, int priority) {
        Syn syn = synMap.get(namespace);

        /*
         * 保证线程入队次序与priority严格一致
         * 搜索enqControl关键字，查看呼应逻辑
         */
        long deadline = System.nanoTime() + SPIN_NANO_TIME;
        while (syn.enqControl.get() < syn.priorityStates.get(priority)) {
            /*
             * 允许一定时间的自旋，若超时则阻塞一会
             * 一般而言，线程数量10以下，不会进入sleep阶段
             */
            long nanosTimeout = System.nanoTime();
            if (nanosTimeout > deadline) {
                try {
                    TimeUnit.MILLISECONDS.sleep(SLEEP_MILLISECONDS_TIME);
                } catch (InterruptedException e) {
                    //ignore
                }
                deadline = System.nanoTime() + SPIN_NANO_TIME;
            }
        }

        /*
         * 交由AQS管控
         */
        syn.acquireSharedPriority(syn.priorityStates.get(priority));
    }

    public void exit() {
        String namespace = localNamespace.get();
        int priority = localPriority.get();
        Syn syn = synMap.get(namespace);
        syn.releaseShared(syn.priorityStates.get(priority));
    }

    private void register(String namespace, int priority) {
        localNamespace.set(namespace);
        localPriority.set(priority);

        synMap.putIfAbsent(namespace, new Syn());
        Syn syn = synMap.get(namespace);
        try {
            syn.acquire(1);
            syn.unsafeRegister(priority);
        } finally {
            syn.release(1);
        }
    }

    private static final class Syn extends AbstractPriorityQueuedSynchronizer {
        private static final int UNINITIALIZED = 0;
        private static final int INITIALIZED = 1;
        private static final int ORIGIN_STATE = 1;
        private static final int LOCKED_STATE = 0;
        /**
         * priority -> 数量的映射
         */
        private final Map<Integer, Integer> priorityCnt = new HashMap<>();
        /**
         * priority -> 所需资源数的映射
         */
        private final Map<Integer, Integer> priorityStates = new HashMap<>();
        /**
         * 初始化控制，保证一个Sync的初始化只由一个线程完成
         */
        private AtomicInteger initControl = new AtomicInteger(UNINITIALIZED);
        /**
         * 控制不同priority的线程进入阻塞队列的顺序
         */
        private AtomicInteger enqControl = new AtomicInteger(1);

        private Syn() {
            setState(ORIGIN_STATE);
        }

        /**
         * 该方法必须在可重入锁的保护下进行，非线程安全方法
         */
        private void unsafeRegister(Integer priority) {
            if (!priorityCnt.containsKey(priority)) {
                priorityCnt.put(priority, 0);
            }
            priorityCnt.put(priority, priorityCnt.get(priority) + 1);
        }

        private boolean tryInit() {
            return initControl.compareAndSet(UNINITIALIZED, INITIALIZED);
        }

        /**
         * 目前仅用于register中，仅在初始化阶段能起到加锁效果
         *
         * @see GroupPriorityBarrier#register(String, int)
         */
        @Override
        protected boolean tryAcquire(int arg) {
            return compareAndSetState(ORIGIN_STATE, LOCKED_STATE);
        }

        /**
         * 目前仅用于register中
         *
         * @see GroupPriorityBarrier#register(String, int)
         */
        @Override
        protected boolean tryRelease(int arg) {
            return compareAndSetState(LOCKED_STATE, ORIGIN_STATE);
        }

        @Override
        protected int tryAcquireShared(int arg) {
            if (getState() >= arg) {
                return 1;
            } else {
                return -1;
            }
        }

        @Override
        protected boolean tryReleaseShared(int arg) {
            for (; ; ) {
                int c = getState();
                if (compareAndSetState(c, c + 1)) {
                    return true;
                }
            }
        }

        @Override
        protected void onAcquireDirect() {
            /*
             * 保证线程入队次序与priority严格一致
             * 搜索enqControl关键字，查看呼应逻辑
             */
            enqControl.incrementAndGet();
        }

        @Override
        protected void onEnq() {
            /*
             * 保证线程入队次序与priority严格一致
             * 搜索enqControl关键字，查看呼应逻辑
             */
            enqControl.incrementAndGet();
        }
    }
}
