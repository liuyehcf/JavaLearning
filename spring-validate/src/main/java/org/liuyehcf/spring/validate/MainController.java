package org.liuyehcf.spring.validate;

import org.liuyehcf.spring.validate.dto.UserDTO;
import org.liuyehcf.spring.validate.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * @author chenlu
 * @date 2018/7/10
 */
@Controller
@RequestMapping("/")
public class MainController {

    @Resource
    private UserService userService;

    @RequestMapping("/create")
    @ResponseBody
    public String createUser(@RequestBody UserDTO userDTO) {
        try {
            return userService.createUser(userDTO);
        } catch (ConstraintViolationException e) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
                sb.append(constraintViolation.getMessage())
                        .append(";");
            }
            if (sb.length() != 0) {
                sb.setLength(sb.length() - 1);
            }
            return sb.toString();
        }
    }

    @RequestMapping("/update")
    @ResponseBody
    public String updateUser(@RequestBody UserDTO userDTO) {
        try {

            return userService.updateUser(userDTO);
        } catch (ConstraintViolationException e) {
            StringBuilder sb = new StringBuilder();
            for (ConstraintViolation<?> constraintViolation : e.getConstraintViolations()) {
                sb.append(constraintViolation.getMessage())
                        .append(";");
            }
            if (sb.length() != 0) {
                sb.setLength(sb.length() - 1);
            }
            return sb.toString();
        }
    }
}
