package org.liuyehcf.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by HCF on 2017/12/15.
 */
public class DefaultParameters {
    public static void main(String[] args) {
        try {
            URL url = new URL("http://localhost:8080/home");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            System.out.println("Default RequestMethod: " + conn.getRequestMethod());
            System.out.println("Default doInput: " + conn.getDoInput());
            System.out.println("Default doOutput: " + conn.getDoOutput());

        } catch (MalformedURLException e) {
            System.err.println("URL初始化错误");
            e.printStackTrace(System.err);
        } catch (IOException e) {
            System.err.println("数据流错误");
            e.printStackTrace(System.err);
        }
    }
}
