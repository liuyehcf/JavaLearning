import org.junit.*;

import java.sql.*;

/**
 * Created by liuye on 2017/5/15 0015.
 */
public class TestRawJDBC {
    @Test
    public void testInsert() {
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/mybatis";
            String user = "root";
            String password = "hcflh19930101";
            conn = DriverManager.getConnection(url, user, password);

            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            Statement stmt = conn.createStatement();

            int rows = stmt.executeUpdate("INSERT INTO crm_user(first_name, last_name, age, sex) " +
                    "VALUES('H','WS',50,1)");

            //throw new SQLException();

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace(System.out);
            try {
                conn.rollback();
            } catch (SQLException e2) {

            }
        }
    }

    @Test
    public void testSavePoint() {
        Connection conn = null;
        Savepoint svpt = null;
        try {
            String url = "jdbc:mysql://localhost:3306/mybatis";
            String user = "root";
            String password = "hcflh19930101";
            conn = DriverManager.getConnection(url, user, password);

            conn.setAutoCommit(false);
            conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            Statement stmt = conn.createStatement();

            int rows = stmt.executeUpdate("INSERT INTO crm_user(first_name, last_name, age, sex) " +
                    "VALUES('Y','C',25,1)");


            svpt = conn.setSavepoint("savePoint1");

            rows = stmt.executeUpdate("INSERT INTO crm_user(first_name, last_name, age, sex) " +
                    "VALUES('H','YW',25,1)");


            conn.rollback(svpt);

            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace(System.out);

        }
    }
}
