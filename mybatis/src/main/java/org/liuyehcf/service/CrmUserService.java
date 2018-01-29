package org.liuyehcf.service;

import org.liuyehcf.dataobject.CrmUserDO;

/**
 * Created by HCF on 2017/3/31.
 */
public interface CrmUserService {

    CrmUserDO selectCrmUserById(Long id);

    int insertCrmUser(CrmUserDO crmUser);

    int updateCrmUser(CrmUserDO crmUser);

    /**
     * 利用XML配置声明式事务
     *
     * @param crmUser
     * @return
     */
    int updateCrmUserWithXmlTransaction(CrmUserDO crmUser);

    /**
     * 利用注解配置声明式事务
     *
     * @param crmUser
     * @return
     */
    int updateCrmUserWithAnnotationTransaction(CrmUserDO crmUser);

    /**
     * 嵌套事务测试
     */
    void nestTransactionWithAnnotationTransaction(CrmUserDO crmUser);
}

