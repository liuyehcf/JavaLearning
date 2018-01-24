package org.liuyehcf.service.impl;

import org.liuyehcf.dao.CrmUserDAO;
import org.liuyehcf.entity.CrmUser;
import org.liuyehcf.service.CrmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by HCF on 2017/3/31.
 */
@Service("crmUserServiceImpl")
public class CrmUserServiceImpl implements CrmUserService {
    @Autowired
    private CrmUserDAO crmUserDAO;

    public CrmUser selectCrmUserById(Long id) {
        return crmUserDAO.selectCrmUserById(id);
    }

    public int insertCrmUser(CrmUser crmUser) {
        return crmUserDAO.insertCrmUser(crmUser);
    }

    public int updateCrmUser(CrmUser crmUser) {
        return crmUserDAO.updateCrmUser(crmUser);
    }

    public int updateCrmUserWithXmlTransaction(CrmUser crmUser) {
        return crmUserDAO.updateCrmUser(crmUser);
    }

    @Transactional
    public int updateCrmUserWithAnnotationTransaction(CrmUser crmUser) {
        return crmUserDAO.updateCrmUser(crmUser);
    }

    public void nestTransactionWithAnnotationTransaction(CrmUser crmUser) {
        crmUserDAO.updateCrmUser(crmUser);
    }
}
