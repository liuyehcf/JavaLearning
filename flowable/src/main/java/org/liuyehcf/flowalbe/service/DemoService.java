package org.liuyehcf.flowalbe.service;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author hechenfeng
 * @date 2018/7/26
 */
@Service
@Slf4j
public class DemoService {

    private static final String BPMN_FILE_PATH = "process/sample.bpmn20.xml";

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    public String deployProcess() {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(BPMN_FILE_PATH)
                .deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();

        log.info("Deploy process success! processDefinition={}",
                processDefinition.getId(),
                processDefinition.getName());

        return processDefinition.getId();
    }

    public String startProcess(String processDefinitionId) {

        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId);

        log.info("Start process success! processDefinitionId={}; processInstanceId={}",
                processDefinitionId,
                processInstance.getId());

        return processInstance.getId();
    }

    public String completeUserTaskByAssignee(String assignee) {
        List<Task> taskList = taskService.createTaskQuery().taskAssignee(assignee).list();
        return completeTasks(assignee, taskList);
    }

    public String completeUserTaskByCandidateUser(String candidateUser) {
        List<Task> taskList = taskService.createTaskQuery().taskCandidateUser(candidateUser).list();
        return completeTasks(candidateUser, taskList);
    }

    private String completeTasks(String user, List<Task> taskList) {
        if (CollectionUtils.isEmpty(taskList)) {
            return "user [" + user + "] has no task todo";
        }

        StringBuilder sb = new StringBuilder();

        for (Task task : taskList) {
            String taskId = task.getId();
            taskService.complete(taskId);
            sb.append("task[")
                    .append(taskId)
                    .append("] is complete by ")
                    .append(user)
                    .append('\n');
        }

        return sb.toString();
    }
}
