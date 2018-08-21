package org.liuyehcf.flowable.element;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.common.api.delegate.Expression;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author hechenfeng
 * @date 2018/8/17
 */
@Component
@Scope(scopeName = "prototype")
@Slf4j
public class DemoServiceTask implements JavaDelegate {

    private Expression field1;

    private Expression field2;

    public void setField1(Expression field1) {
        this.field1 = field1;
    }

    @Override
    public void execute(DelegateExecution execution) {
        if (field1 == null) {
            log.error("Filed injection failed. fieldName={}", "field1");
        } else {
            log.info("Filed injection succeeded. fieldName={}", "field1");
        }

        if (field2 == null) {
            log.error("Filed injection failed. fieldName={}", "field2");
        } else {
            log.info("Filed injection succeeded. fieldName={}", "field2");
        }
    }
}
