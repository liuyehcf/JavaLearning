package org.liuyehcf.spring.tx;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author hechenfeng
 * @date 2018/8/11
 */
@Service
public class UserService {

    @Resource
    private UserDAO userDAO;

    @Transactional
    public int insert(String name, Integer age, Boolean ex) {
        UserDO userDO = new UserDO();
        userDO.setName(name);
        userDO.setAge(age);

        int res = userDAO.insert(userDO);

        if (ex) {
            throw new RuntimeException();
        }

        return res;
    }
}
