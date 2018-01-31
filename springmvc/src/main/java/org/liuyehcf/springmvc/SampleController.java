package org.liuyehcf.springmvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/")
@Controller
public class SampleController {

    @RequestMapping(value = "/{user}", method = RequestMethod.GET)
    @ResponseBody
    public String printGetMethodRequestParams(@PathVariable(value = "user") String user, @RequestParam(value = "age") Integer age) {
        return "user: " + user + ", age: " + age;
    }
}
