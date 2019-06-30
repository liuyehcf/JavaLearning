package org.liuyehcf.flowable.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.liuyehcf.flowable.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {TestApplication.class})
public class DemoTest {

    @Autowired
    private DemoService demoService;

    private static void sleep(int second) {
        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    @Test
    public void test() {
        String processDefinition = demoService.deployProcess();
        log.info("deployProcess succeeded. processDefinition={}", processDefinition);

        String processInstanceId = demoService.startProcess(processDefinition);
        log.info("startProcess succeeded. processInstanceId={}", processInstanceId);

        sleep(1);
        String message;

        message = demoService.completeUserTaskByAssignee("tom");
        log.info("completeUserTaskByAssignee. message={}", message);

        sleep(1);

        message = demoService.completeUserTaskByCandidateUser("bob");
        log.info("completeUserTaskByCandidateUser. message={}", message);

        sleep(1);

        message = demoService.completeUserTaskByCandidateUser("lucy");
        log.info("completeUserTaskByCandidateUser. message={}", message);
    }
}
