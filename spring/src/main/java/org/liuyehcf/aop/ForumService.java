package org.liuyehcf.aop;

import java.util.concurrent.TimeUnit;

/**
 * Created by liuye on 2017/5/17 0017.
 */
public interface ForumService {
    void removeTopic(int topicId);

    void removeForum(int forumId);
}
