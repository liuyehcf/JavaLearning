import org.junit.Test;
import org.liuyehcf.entity.CrmUser;
import org.liuyehcf.service.CrmUserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liuye on 2017/5/17 0017.
 */
public class TestAnnotationTransaction {
    @Test
    public void testTransactionalScanPath() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("testTransactionalScanPath.xml");

        CrmUserService crmUserService = applicationContext.getBean("crmUserServiceImpl", CrmUserService.class);

        CrmUser crmUser = crmUserService.selectCrmUserById(1L);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
        crmUser.setFirstName(df.format(new Date()));

        crmUserService.updateCrmUserWithAnnotationTransaction(crmUser);
    }


    @Test
    public void testUpdate() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("transactionAnnotationContext.xml");

        CrmUserService crmUserService = applicationContext.getBean("crmUserServiceImpl", CrmUserService.class);

        CrmUser crmUser = crmUserService.selectCrmUserById(1L);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
        crmUser.setFirstName(df.format(new Date()));

        crmUserService.updateCrmUserWithAnnotationTransaction(crmUser);
    }
}
