package org.liuyehcf.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;

public class JdbcMySQLDemo {
    public static void main(String[] args) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");

        Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/mybatis", "root", "hcflh19930101");

        System.out.println(connection);
    }
}
