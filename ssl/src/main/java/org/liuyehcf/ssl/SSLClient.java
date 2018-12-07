package org.liuyehcf.ssl;

import javax.net.SocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.Socket;
import java.security.KeyStore;

public class SSLClient {
    private static final String KEY_STORE_PATH = System.getProperty("user.home") + File.separator + "liuyehcf_client_ks";
    private static final String KEY_STORE_PASSWORD = "345678";
    private static final String KEY_PASSWORD = "456789";

    public static void main(String[] args) throws Exception {
        // Set the key store to use for validating the server cert.
        System.setProperty("javax.net.ssl.trustStore", KEY_STORE_PATH);
        System.setProperty("javax.net.debug", "ssl,handshake");
        SSLClient client = new SSLClient();
        Socket s = client.clientWithoutCert();

        PrintWriter writer = new PrintWriter(s.getOutputStream());
        BufferedReader reader = new BufferedReader(new InputStreamReader(s.getInputStream()));
        writer.println("hello");
        writer.flush();
        System.out.println(reader.readLine());
        s.close();
    }

    private Socket clientWithoutCert() throws Exception {
        SocketFactory sf = SSLSocketFactory.getDefault();
        return sf.createSocket("localhost", 8443);
    }

    private Socket clientWithCert() throws Exception {
        KeyStore ks = KeyStore.getInstance("jceks");

        ks.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());
        KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509");
        kf.init(ks, KEY_PASSWORD.toCharArray());

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(kf.getKeyManagers(), null, null);
        SocketFactory factory = context.getSocketFactory();
        return factory.createSocket("localhost", 8443);
    }
}
