package org.liuyehcf.flowalbe.element;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.delegate.TaskListener;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author hechenfeng
 * @date 2018/8/18
 */
@Component
@Scope(scopeName = "prototype")
@Slf4j
public class DemoListener implements TaskListener, ExecutionListener {
    @Override
    public void notify(DelegateExecution execution) {
        FlowElement currentFlowElement = execution.getCurrentFlowElement();
        log.info("ExecutionListener is trigger. elementId={}", currentFlowElement.getId());
    }

    @Override
    public void notify(DelegateTask delegateTask) {
        String taskName = delegateTask.getName();
        String assignee = delegateTask.getAssignee();
        Set<IdentityLink> candidates = delegateTask.getCandidates();

        List<CandidateInfo> candidateInfoList = candidates.stream().map(CandidateInfo::new).collect(Collectors.toList());
        log.info("TaskListener is trigger. taskName={}; assignee={}; candidateInfoList={}", taskName, assignee, JSON.toJSON(candidateInfoList));
    }

    private static final class CandidateInfo {
        private final String groupId;
        private final String userId;

        private CandidateInfo(IdentityLink identityLink) {
            this.groupId = identityLink.getGroupId();
            this.userId = identityLink.getUserId();
        }

        public String getGroupId() {
            return groupId;
        }

        public String getUserId() {
            return userId;
        }
    }

}
