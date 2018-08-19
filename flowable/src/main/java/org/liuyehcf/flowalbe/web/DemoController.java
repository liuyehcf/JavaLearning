package org.liuyehcf.flowalbe.web;

import org.liuyehcf.flowalbe.service.DemoService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author hechenfeng
 * @date 2018/7/25
 */
@RestController
@RequestMapping("/")
public class DemoController {

    @Resource
    private DemoService demoService;

    @RequestMapping("/process/deploy")
    @ResponseBody
    public String deployProcess() {
        String processDefinitionId = demoService.deployProcess();
        return "Deploy Succeeded, processDefinitionId=" + processDefinitionId + "\n";
    }

    @RequestMapping("/process/start")
    @ResponseBody
    public String startProcess(@RequestParam String processDefinitionId) {
        String processInstanceId = demoService.startProcess(processDefinitionId);
        return "Start Succeeded, processInstance=" + processInstanceId + "\n";
    }

    @RequestMapping("/userTask/completeByAssignee")
    @ResponseBody
    public String completeUserTaskByAssignee(@RequestParam String assignee) {
        return demoService.completeUserTaskByAssignee(assignee) + "\n";
    }

    @RequestMapping("/userTask/completeByCandidateUser")
    @ResponseBody
    public String completeUserTask(@RequestParam String candidateUser) {
        return demoService.completeUserTaskByCandidateUser(candidateUser) + "\n";
    }
}
