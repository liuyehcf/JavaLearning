package org.liuyehcf.flowalbe.delegate;

import org.flowable.engine.delegate.DelegateExecution;
import org.liuyehcf.flowalbe.config.Const;

/**
 * @author chenlu
 * @date 2018/7/26
 */
public class UserAuthentication extends BaseDelegate {

    @Override
    public void doExecute(DelegateExecution execution) {
        String name = (String) execution.getVariable(Const.VARIABLE_NAME);
        String password = (String) execution.getVariable(Const.VARIABLE_PASSWORD);

        execution.setVariable(Const.VARIABLE_APPROVED, auth(name, password));
    }

    private boolean auth(String name, String password) {
        return name != null && name.equals(password);
    }
}
