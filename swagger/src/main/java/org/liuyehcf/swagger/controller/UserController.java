package org.liuyehcf.swagger.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.liuyehcf.swagger.entity.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class UserController {

    private static Map<Integer, User> userMap = new HashMap<>();

    @ApiOperation(value = "GET_USER_API_1", notes = "获取User方式1")
    @RequestMapping(value = "getApi1/{id}", method = RequestMethod.GET)
    @ResponseBody
    public User getUserByIdAndName1(
            @ApiParam(name = "id", value = "用户id", required = true) @PathVariable int id,
            @ApiParam(name = "name", value = "用户名字", required = true) @RequestParam String name) {
        if (userMap.containsKey(id)) {
            User user = userMap.get(id);
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }

    @ApiOperation(value = "GET_USER_API_2", notes = "获取User方式2")
    @RequestMapping(value = "getApi2/{id}", method = RequestMethod.GET)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id", required = true, paramType = "path", dataType = "int"),
            @ApiImplicitParam(name = "name", value = "用户名字", required = true, paramType = "query", dataType = "String")
    })
    @ResponseBody
    public User getUserByIdAndName2(
            @PathVariable int id,
            @RequestParam String name) {
        if (userMap.containsKey(id)) {
            User user = userMap.get(id);
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }

    @ApiOperation(value = "ADD_USER_API_1", notes = "增加User方式1")
    @RequestMapping(value = "/addUser1", method = RequestMethod.POST)
    @ResponseBody
    public String addUser1(
            @ApiParam(name = "user", value = "用户User", required = true) @RequestBody User user) {
        if (userMap.containsKey(user.getId())) {
            return "failure";
        }
        userMap.put(user.getId(), user);
        return "success";
    }

    @ApiOperation(value = "ADD_USER_API_2", notes = "增加User方式2")
    @ApiImplicitParam(name = "user", value = "用户User", required = true, paramType = "body", dataType = "User")
    @RequestMapping(value = "/addUser2", method = RequestMethod.POST)
    @ResponseBody
    public String addUser2(@RequestBody User user) {
        if (userMap.containsKey(user.getId())) {
            return "failure";
        }
        userMap.put(user.getId(), user);
        return "success";
    }

}
