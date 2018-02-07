package org.liuyehcf.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class TestWithoutParam {
    @Test
    public void insert() {
        new TestTemplate() {
            @Override
            protected void doExecute(SqlSession sqlSession) throws Exception {
                CrmUserDAO mapper = sqlSession.getMapper(CrmUserDAO.class);

                CrmUserDO crmUserDO = new CrmUserDO();
                crmUserDO.setFirstName("六");
                crmUserDO.setLastName("小");
                crmUserDO.setAge(25);
                crmUserDO.setSex(true);

                System.out.println("before insert: " + crmUserDO);
                mapper.insert(crmUserDO);
                System.out.println("after insert: " + crmUserDO);
            }
        }.execute();
    }

    @Test
    public void update() {
        new TestTemplate() {
            @Override
            protected void doExecute(SqlSession sqlSession) throws Exception {
                CrmUserDAO mapper = sqlSession.getMapper(CrmUserDAO.class);

                CrmUserDO crmUserDO = new CrmUserDO();
                Long id = 1L;
                crmUserDO.setId(id);
                crmUserDO.setLastName("七");
                crmUserDO.setFirstName("大");
                crmUserDO.setSex(false);

                mapper.update(crmUserDO);

                System.out.println(mapper.selectById(id));
            }
        }.execute();
    }

    @Test
    public void selectByFirstName() {
        new TestTemplate() {
            @Override
            protected void doExecute(SqlSession sqlSession) throws Exception {
                CrmUserDAO mapper = sqlSession.getMapper(CrmUserDAO.class);

                List<CrmUserDO> crmUserDOS = mapper.selectByFirstName("六");

                System.out.println(crmUserDOS);
            }
        }.execute();
    }
}
