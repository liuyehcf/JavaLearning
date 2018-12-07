package org.liuyehcf.ssl;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;

public class SSLServer extends Thread {

    private static final String KEY_STORE_PATH = System.getProperty("user.home") + File.separator + "liuyehcf_server_ks";
    private static final String KEY_STORE_PASSWORD = "123456";
    private static final String PRIVATE_PASSWORD = "234567";

    private Socket socket;

    private SSLServer(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter writer = new PrintWriter(socket.getOutputStream());

            String data = reader.readLine();
            writer.println(data);
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.ssl.trustStore", KEY_STORE_PATH);

        KeyStore ks = KeyStore.getInstance("jceks");
        ks.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());
        KeyManagerFactory kf = KeyManagerFactory.getInstance("SunX509");
        kf.init(ks, PRIVATE_PASSWORD.toCharArray());

        SSLContext context = SSLContext.getInstance("TLS");
        context.init(kf.getKeyManagers(), null, null);
        ServerSocketFactory factory = context.getServerSocketFactory();
        ServerSocket socket = factory.createServerSocket(8443);
        ((SSLServerSocket) socket).setNeedClientAuth(false);

        while (!Thread.currentThread().isInterrupted()) {
            new SSLServer(socket.accept()).start();
        }
    }
}
