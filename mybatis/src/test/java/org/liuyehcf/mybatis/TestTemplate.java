package org.liuyehcf.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public abstract class TestTemplate {

    static final String DEFAULT_FIRST_NAME = "default_first_name";
    static final String DEFAULT_LAST_NAME = "default_last_name";
    static final Integer DEFAULT_AGE = 100;
    static final Boolean DEFAULT_SEX = true;

    static final String MODIFIED_FIRST_NAME = "modified_first_name";
    static final String MODIFIED_LAST_NAME = "modified_last_name";
    static final Boolean MODIFIED_SEX = false;

    static final Long ID = 1L;

    public void execute() {
        String resource = "mybatis-config.xml";
        InputStream inputStream;
        try {
            inputStream = Resources.getResourceAsStream(resource);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
            return;
        }

        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        SqlSession sqlSession = null;
        try {
            // 打开SqlSession会话
            sqlSession = sqlSessionFactory.openSession();

            doExecute(sqlSession);

            sqlSession.commit();
        } catch (Exception e) {
            if (sqlSession != null) {
                sqlSession.rollback();
            }
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            // 在finally中保证资源被顺利关闭
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    protected abstract void doExecute(SqlSession sqlSession) throws Exception;
}
