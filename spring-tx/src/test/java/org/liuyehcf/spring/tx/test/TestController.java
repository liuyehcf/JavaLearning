package org.liuyehcf.spring.tx.test;

import org.junit.Test;
import org.liuyehcf.spring.tx.UserController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author hechenfeng
 * @date 2018/8/26
 */
public class TestController extends BaseConfig {

    @Autowired
    private UserController userController;

    @Test
    @Transactional
    public void test() {
        userController.insert("liuye", 10, false);
    }
}
