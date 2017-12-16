package org.liuyehcf.http.urlconnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Liuye on 2017/12/15.
 */
public class URLConnectionDemo {
    public static void doGet(String value1, String value2, String operator) {
        try {
            URL url = new URL("http://localhost:8080/compute?value1=" + value1 + "&value2=" + value2);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("operator", operator);
            conn.setDoInput(true);

            conn.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (MalformedURLException e) {
            System.err.println("URL初始化错误");
            e.printStackTrace(System.err);
        } catch (IOException e) {
            System.err.println("数据流错误");
            e.printStackTrace(System.err);
        }
    }

    public static void doPost() {
        try {
            URL url = new URL("http://localhost:8080/login");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");//todo 必须有
            conn.setRequestProperty("Content-Type", "application/json");//todo 必须有
            conn.setDoInput(true);
            conn.setDoOutput(true);//todo 必须有

            conn.connect();

            String requestBody = "{\"name\":\"张三\",\"password\":\"123456789\"}";
            PrintWriter out = new PrintWriter(conn.getOutputStream());
            out.print(requestBody);
            out.flush();//todo 别忘了这句

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (MalformedURLException e) {
            System.err.println("URL初始化错误");
            e.printStackTrace(System.err);
        } catch (IOException e) {
            System.err.println("数据流错误");
            e.printStackTrace(System.err);
        }
    }

    public static void main(String[] args) {
//        doGet("1", "2", "+");
//        doGet("1", "2", "-");
//        doGet("1", "2", "*");
//        doGet("1", "2", "/");
        doPost();
    }
}
