package org.liuyehcf.mybatis;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public abstract class TestTemplate {
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
