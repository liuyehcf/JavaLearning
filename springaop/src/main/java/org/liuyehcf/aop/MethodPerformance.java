package org.liuyehcf.aop;

/**
 * Created by liuye on 2017/5/17 0017.
 */
public class MethodPerformance {
    private long begin;
    private long end;
    private String serviceMethod;

    public MethodPerformance(String serviceMethod){
        this.serviceMethod=serviceMethod;
        this.begin=System.currentTimeMillis();
    }

    public void printPerformance(){
        end=System.currentTimeMillis();
        long elapse=end-begin;
        System.out.println(serviceMethod+"costs "+elapse+"ms");
    }
}
