package org.liuyehcf.concurrent.threadlocal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by liuye on 2017/4/8 0008.
 */

class Something{
    private static int count=0;
    public final int id=count++;
}

public class TestThreadLocal {

    public ThreadLocal<Something> local;

    public TestThreadLocal(){
        local=new ThreadLocal<Something>(){
            @Override
            protected Something initialValue() {
                return new Something();
            }
        };
    }

    public static void main(String[] args){
        Runnable task=new Runnable() {
            private final TestThreadLocal testThreadLocal=new TestThreadLocal();

            public void run() {
                System.out.println(testThreadLocal.local.get().id+", "+testThreadLocal.local.get());
            }
        };

        ExecutorService exec= Executors.newCachedThreadPool();
        for(int i=0;i<10;i++){
            exec.execute(task);
        }
    }
}
