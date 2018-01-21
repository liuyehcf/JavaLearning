package org.liuyehcf.swagger.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/base")
public class SampleController {

    @ApiOperation(value = "GET DEMO", notes = "GET NOTES")
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @ResponseBody
    public String getDemo(
            @ApiParam(name = "param1", value = "参数1", required = true) @RequestParam String param1,
            @ApiParam(name = "param2", value = "参数2", required = true) @RequestParam String param2) {
        return "sampleGet: " + param1 + "," + param2;
    }


    @ApiOperation(value = "POST DEMO", notes = "POST NOTES")
    @ApiImplicitParam(name = "param", value = "参数1", required = true, dataType = "String")
    @RequestMapping(value = "/post", method = RequestMethod.POST)
    @ResponseBody
    public String postDemo(@RequestBody String param) {
        return "samplePost: " + param;
    }

}
