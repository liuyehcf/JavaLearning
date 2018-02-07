package org.liuyehcf.mybatis;

import org.apache.ibatis.session.SqlSession;
import org.junit.Test;

import java.util.List;

public class TestWithoutParam {

    @Test
    public void select() {
        new TestTemplate() {
            @Override
            protected void doExecute(SqlSession sqlSession) throws Exception {
                CrmUserDAO mapper = sqlSession.getMapper(CrmUserDAO.class);

                CrmUserDO crmUserDO = mapper.selectById(ID);

                System.out.println(crmUserDO);
            }
        }.execute();
    }


    @Test
    public void insert() {
        new TestTemplate() {
            @Override
            protected void doExecute(SqlSession sqlSession) throws Exception {
                CrmUserDAO mapper = sqlSession.getMapper(CrmUserDAO.class);

                CrmUserDO crmUserDO = new CrmUserDO();
                crmUserDO.setFirstName(DEFAULT_FIRST_NAME);
                crmUserDO.setLastName(DEFAULT_LAST_NAME);
                crmUserDO.setAge(DEFAULT_AGE);
                crmUserDO.setSex(DEFAULT_SEX);

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

                crmUserDO.setId(ID);
                crmUserDO.setLastName(MODIFIED_LAST_NAME);
                crmUserDO.setFirstName(MODIFIED_FIRST_NAME);
                crmUserDO.setSex(MODIFIED_SEX);

                mapper.update(crmUserDO);

                System.out.println(mapper.selectById(ID));
            }
        }.execute();
    }

    @Test
    public void selectByFirstName() {
        new TestTemplate() {
            @Override
            protected void doExecute(SqlSession sqlSession) throws Exception {
                CrmUserDAO mapper = sqlSession.getMapper(CrmUserDAO.class);

                List<CrmUserDO> crmUserDOS = mapper.selectByFirstName(MODIFIED_FIRST_NAME);

                System.out.println(crmUserDOS.size());
            }
        }.execute();
    }


    @Test
    public void selectByFirstNameAndLastName() {
        new TestTemplate() {
            @Override
            protected void doExecute(SqlSession sqlSession) throws Exception {
                CrmUserDAO mapper = sqlSession.getMapper(CrmUserDAO.class);

                List<CrmUserDO> crmUserDOS;

                crmUserDOS = mapper.selectByFirstNameAndLastName(MODIFIED_FIRST_NAME, null);
                System.out.println(crmUserDOS.size());

                crmUserDOS = mapper.selectByFirstNameAndLastName(null, MODIFIED_LAST_NAME);
                System.out.println(crmUserDOS.size());

                crmUserDOS = mapper.selectByFirstNameAndLastName(MODIFIED_FIRST_NAME, MODIFIED_LAST_NAME);
                System.out.println(crmUserDOS.size());
            }
        }.execute();
    }
}
