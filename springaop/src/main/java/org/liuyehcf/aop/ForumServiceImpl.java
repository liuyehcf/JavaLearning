package org.liuyehcf.aop;

import java.util.concurrent.TimeUnit;

/**
 * Created by liuye on 2017/5/17 0017.
 */
public class ForumServiceImpl implements  ForumService {
    public void removeTopic(int topicId) {
        System.out.println("模拟删除Topic记录:"+topicId);

        try{
            TimeUnit.SECONDS.sleep(1);
        }catch(InterruptedException e){
            e.printStackTrace(System.out);
        }
    }

    public void removeForum(int forumId){
        System.out.println("模拟删除Forum记录:"+forumId);

        try{
            TimeUnit.SECONDS.sleep(1);
        }catch(InterruptedException e){
            e.printStackTrace(System.out);
        }
    };
}
