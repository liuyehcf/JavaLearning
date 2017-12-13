package org.liuyehcf.service;

import org.liuyehcf.entity.CrmUser;

/**
 * Created by HCF on 2017/3/31.
 */
public interface CrmUserService {

    CrmUser selectCrmUserById(Long id);

    int insertCrmUser(CrmUser crmUser);

    int updateCrmUser(CrmUser crmUser);

    /**
     * 利用XML配置声明式事务
     * @param crmUser
     * @return
     */
    int updateCrmUserWithXmlTransaction(CrmUser crmUser);

    /**
     * 利用注解配置声明式事务
     * @param crmUser
     * @return
     */
    int updateCrmUserWithAnnotationTransaction(CrmUser crmUser);

    /**
     * 嵌套事务测试
     */
    void nestTransactionWithAnnotationTransaction(CrmUser crmUser);
}

