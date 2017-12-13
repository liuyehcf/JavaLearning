import org.liuyehcf.entity.CrmUser;
import org.liuyehcf.service.CrmUserService;
import org.junit.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liuye on 2017/5/16 0016.
 */
public class TestXmlTransaction {

    @Test
    public void testTransaction() {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("transactionXmlContext.xml");

        CrmUserService crmUserService = applicationContext.getBean("crmUserServiceImpl", CrmUserService.class);

        CrmUser crmUser = crmUserService.selectCrmUserById(1L);

        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");//设置日期格式
        crmUser.setFirstName(df.format(new Date()));

        crmUserService.updateCrmUserWithXmlTransaction(crmUser);
    }
}
