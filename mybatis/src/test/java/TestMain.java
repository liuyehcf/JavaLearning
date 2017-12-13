import org.liuyehcf.entity.CrmUser;
import org.liuyehcf.service.CrmUserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.junit.*;

/**
 * Created by liuye on 2017/4/10 0010.
 */
public class TestMain {

    @Test
    public void testInsert(){
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("transactionAnnotationContext.xml");

        CrmUserService crmUserService=applicationContext.getBean("crmUserServiceImpl",CrmUserService.class);

        CrmUser crmUser=new CrmUser();
        crmUser.setFirstName("L");
        crmUser.setLastName("H");

        crmUser.setAge(24);
        crmUser.setSex(false);

        crmUserService.insertCrmUser(crmUser);
    }

    @Test
    public void testSelect(){
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("transactionAnnotationContext.xml");

        CrmUserService crmUserService=applicationContext.getBean("crmUserServiceImpl",CrmUserService.class);

        CrmUser crmUser=crmUserService.selectCrmUserById(1L);

        System.out.println(crmUser);
    }

    @Test
    public void testUpdate(){
        ApplicationContext applicationContext=new ClassPathXmlApplicationContext("transactionAnnotationContext.xml");

        CrmUserService crmUserService=applicationContext.getBean("crmUserServiceImpl",CrmUserService.class);

        CrmUser crmUser=crmUserService.selectCrmUserById(1L);

        crmUser.setFirstName("HHH");
        //crmUser.setSex(null);

        crmUserService.updateCrmUser(crmUser);
    }

}
