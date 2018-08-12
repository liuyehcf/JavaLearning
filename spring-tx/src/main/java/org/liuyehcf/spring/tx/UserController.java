package org.liuyehcf.spring.tx;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author hechenfeng
 * @date 2018/8/11
 */
@RestController
@RequestMapping("/")
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping("/insert")
    @ResponseBody
    public String insert(@RequestParam("name") String name, @RequestParam("age") Integer age, @RequestParam("ex") Boolean ex) {
        try {
            userService.insert(name, age, ex);
            return "SUCCESS";
        } catch (Throwable e) {
            e.printStackTrace();
            return "FAILURE";
        }
    }

    @RequestMapping("/check_health")
    @ResponseBody
    public String checkHealth() {
        return "OK";
    }
}
