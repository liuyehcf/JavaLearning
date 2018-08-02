package org.liuyehcf.flowalbe.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.liuyehcf.flowalbe.config.Const;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author chenlu
 * @date 2018/7/26
 */
public class Checkout extends BaseDelegate {

    private static final Logger logger = LoggerFactory.getLogger(Checkout.class);

    @Override
    public void doExecute(DelegateExecution execution) {
        String name = (String) execution.getVariable(Const.VARIABLE_NAME);
        Integer money = (Integer) execution.getVariable(Const.VARIABLE_MONEY);

        logger.warn("尊敬的顾客 {} 您好，您总共消费 {} 元", name, money);
    }
}
