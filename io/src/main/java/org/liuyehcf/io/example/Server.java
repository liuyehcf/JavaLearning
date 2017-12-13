package org.liuyehcf.io.example;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by Liuye on 2017/5/27.
 */
public class Server {
    public static void main(String[] args) {
        server();
    }

    public static void server() {
        ServerSocket serverSocket = null;
        InputStream in = null;
        try {
            serverSocket = new ServerSocket(8080);
            int receiveMsgSize = 0;
            byte[] receiveBuf = new byte[1024];
            while (true) {
                Socket clientSocket = serverSocket.accept();
                SocketAddress clientAddress = clientSocket.getRemoteSocketAddress();
                System.out.println("Handling client at " + clientAddress);
                in = clientSocket.getInputStream();
                while ((receiveMsgSize = in.read(receiveBuf)) != -1) {
                    byte[] temp = new byte[receiveMsgSize];
                    System.arraycopy(receiveBuf, 0, temp, 0, receiveMsgSize);
                    System.out.println(new String(temp));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
