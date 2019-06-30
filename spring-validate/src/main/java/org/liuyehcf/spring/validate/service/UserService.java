package org.liuyehcf.spring.validate.service;

import org.liuyehcf.spring.validate.dto.UserDTO;
import org.liuyehcf.spring.validate.group.Create;
import org.liuyehcf.spring.validate.group.Update;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

/**
 * @author hechenfeng
 * @date 2018/7/10
 */
@Validated
public interface UserService {
    @Validated(Create.class)
    String createUser(@Valid UserDTO userDTO);

    @Validated(Update.class)
    String updateUser(@Valid UserDTO userDTO);
}
