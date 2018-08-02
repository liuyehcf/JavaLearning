package org.liuyehcf.flowalbe.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chenlu
 * @date 2018/7/26
 */
public abstract class BaseDelegate implements JavaDelegate {
    private static final Logger logger = LoggerFactory.getLogger(BaseDelegate.class);

    @Override
    public final void execute(DelegateExecution execution) {
        logger.info("{} start", getClass().getSimpleName());

        doExecute(execution);

        logger.info("{} end", getClass().getSimpleName());
    }

    abstract void doExecute(DelegateExecution execution);
}
