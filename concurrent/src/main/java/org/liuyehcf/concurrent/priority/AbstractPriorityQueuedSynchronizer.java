package org.liuyehcf.concurrent.priority;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * @author hechenfeng
 * @date 2018/5/30
 */
public abstract class AbstractPriorityQueuedSynchronizer extends AbstractQueuedSynchronizer {

    private static final Method addWaiter;
    private static final Method setHeadAndPropagate;
    private static final Method selfInterrupt;
    private static final Method shouldParkAfterFailedAcquire;
    private static final Method parkAndCheckInterrupt;
    private static final Method cancelAcquire;
    private static final Field head;

    private static final Class Node;
    private static final Field SHARED;
    private static final Method predecessor;
    private static final Field next;

    static {
        Class classNode = null;
        for (Class clazz : AbstractQueuedSynchronizer.class.getDeclaredClasses()) {
            if ("Node".equals(clazz.getSimpleName())) {
                classNode = clazz;
            }
        }
        if (classNode == null) {
            throw new Error("can't find java.util.concurrent.locks.AbstractQueuedSynchronizer$Node");
        }

        Node = classNode;

        try {
            addWaiter = AbstractQueuedSynchronizer.class.getDeclaredMethod("addWaiter", Node);
            addWaiter.setAccessible(true);

            setHeadAndPropagate = AbstractQueuedSynchronizer.class.getDeclaredMethod("setHeadAndPropagate", Node, int.class);
            setHeadAndPropagate.setAccessible(true);

            selfInterrupt = AbstractQueuedSynchronizer.class.getDeclaredMethod("selfInterrupt");
            selfInterrupt.setAccessible(true);

            shouldParkAfterFailedAcquire = AbstractQueuedSynchronizer.class.getDeclaredMethod("shouldParkAfterFailedAcquire", Node, Node);
            shouldParkAfterFailedAcquire.setAccessible(true);

            parkAndCheckInterrupt = AbstractQueuedSynchronizer.class.getDeclaredMethod("parkAndCheckInterrupt");
            parkAndCheckInterrupt.setAccessible(true);

            cancelAcquire = AbstractQueuedSynchronizer.class.getDeclaredMethod("cancelAcquire", Node);
            cancelAcquire.setAccessible(true);

            head = AbstractQueuedSynchronizer.class.getDeclaredField("head");
            head.setAccessible(true);

            SHARED = Node.getDeclaredField("SHARED");
            SHARED.setAccessible(true);

            predecessor = Node.getDeclaredMethod("predecessor");
            predecessor.setAccessible(true);

            next = Node.getDeclaredField("next");
            next.setAccessible(true);

        } catch (NoSuchMethodException | NoSuchFieldException e) {
            throw new Error(e);
        }
    }

    public static void main(String[] args) {
        System.out.println();
    }

    /**
     * 需要埋入钩子方法{@link #onAcquireDirect}
     *
     * @param arg
     */
    public final void acquireSharedPriority(int arg) {
        if (tryAcquireShared(arg) < 0) {
            doAcquireSharedPriority(arg);
        } else {
            onAcquireDirect();
        }
    }

    protected abstract void onAcquireDirect();

    /**
     * 添加钩子方法{@link #onEnq()}，需要用反射重写该方法
     *
     * @see java.util.concurrent.locks.AbstractQueuedSynchronizer#doAcquireShared
     */
    private void doAcquireSharedPriority(int arg) {
        try {
            final Object node = addWaiter.invoke(this, SHARED.get(null));
            onEnq();
            boolean failed = true;
            try {
                boolean interrupted = false;
                for (; ; ) {
                    final Object p = predecessor.invoke(node);
                    if (p == head.get(this)) {
                        int r = tryAcquireShared(arg);
                        if (r >= 0) {
                            setHeadAndPropagate.invoke(this, node, r);
                            next.set(p, null);
                            if (interrupted)
                                selfInterrupt.invoke(this);
                            failed = false;
                            return;
                        }
                    }
                    if ((boolean) shouldParkAfterFailedAcquire.invoke(this, p, node) &&
                            (boolean) parkAndCheckInterrupt.invoke(this))
                        interrupted = true;
                }
            } finally {
                if (failed)
                    cancelAcquire.invoke(this, node);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract void onEnq();
}
