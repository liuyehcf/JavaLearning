package org.liuyehcf.spring.validate.service;

import org.liuyehcf.spring.validate.dto.UserDTO;
import org.springframework.stereotype.Service;

/**
 * @author chenlu
 * @date 2018/7/10
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Override
    public String createUser(UserDTO userDTO) {
        return "createUser success";
    }

    @Override
    public String updateUser(UserDTO userDTO) {
        return "updateUser success";
    }
}
