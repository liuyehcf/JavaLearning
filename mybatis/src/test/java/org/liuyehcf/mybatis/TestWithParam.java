package org.liuyehcf.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

public class TestWithParam {
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
                mapper.insertWithParam(crmUserDO);
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

                mapper.updateWithParam(crmUserDO);

                System.out.println(mapper.selectByIdWithParam(id));
            }
        }.execute();
    }


    @Test
    public void select() {
        new TestTemplate() {
            @Override
            protected void doExecute(SqlSession sqlSession) throws Exception {
                CrmUserDAO mapper = sqlSession.getMapper(CrmUserDAO.class);

                CrmUserDO crmUserDO = mapper.selectByIdWithParam(1L);

                System.out.println(crmUserDO);
            }
        }.execute();
    }
}
