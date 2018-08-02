package org.liuyehcf.flowalbe.service;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.liuyehcf.flowalbe.config.Const;
import org.liuyehcf.flowalbe.model.Commodity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

/**
 * @author chenlu
 * @date 2018/7/26
 */
@Service
public class SampleProcessService {

    private static final Logger logger = LoggerFactory.getLogger(SampleProcessService.class);

    private static final String BPMN_FILE_PATH = "process/sample.bpmn20.xml";

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private TaskService taskService;

    public void deploy() {
        Deployment deployment = repositoryService.createDeployment()
                .addClasspathResource(BPMN_FILE_PATH)
                .deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();

        logger.warn("Deploy process success! process name is: {}", processDefinition.getName());
    }

    public void start() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(Const.PROCESS_ID);

        logger.warn("Start process success! process instanceId is: {}", processInstance.getId());
    }

    public void login(String name, String password) {
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(Const.CANDIDATE_GROUP_CUSTOMER).list();

        if (tasks == null || tasks.size() != 1) {
            throw new RuntimeException();
        }

        Task task = tasks.get(0);

        taskService.setVariable(task.getId(), Const.VARIABLE_NAME, name);
        taskService.setVariable(task.getId(), Const.VARIABLE_PASSWORD, password);

        taskService.complete(task.getId());
    }

    public void shopping() {
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup(Const.CANDIDATE_GROUP_SHOPPING).list();

        if (tasks == null || tasks.size() != 1) {
            throw new RuntimeException();
        }

        Task task = tasks.get(0);

        Scanner scanner = new Scanner(System.in);
        int order;
        int money = 0;

        while (true) {
            printCommodity();
            try {
                order = Integer.parseInt(scanner.next());
                if (Objects.equals(order, 0)) {
                    break;
                }

                String name = Commodity.ORDER_MAP.get(order);
                money += Commodity.PRODUCT_MAP.get(name);

                System.out.println("The total amount you spend is: " + money);
            } catch (Exception e) {
                System.err.println("Please type integer");
            }
        }

        taskService.setVariable(task.getId(), Const.VARIABLE_MONEY, money);

        taskService.complete(task.getId());
    }

    private void printCommodity() {
        for (Map.Entry<Integer, String> entry : Commodity.ORDER_MAP.entrySet()) {
            int order = entry.getKey();
            String name = entry.getValue();
            int price = Commodity.PRODUCT_MAP.get(name);

            System.out.format("%-4d%-20s%-6d\n", order, name, price);
        }

        System.out.format("%-4d%-20s\n", 0, "Quit");
    }

    public static void main(String[] args) {
        new SampleProcessService().printCommodity();
    }
}
