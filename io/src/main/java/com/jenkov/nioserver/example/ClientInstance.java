package com.jenkov.nioserver.example;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Created by Liuye on 2017/5/28.
 */
public class ClientInstance {
    public static void main(String[] args) {
        SocketChannel socketChannel = null;
        try {
            socketChannel = SocketChannel.open();

            socketChannel.connect(new InetSocketAddress("10.210.0.139", 9999));

            if (socketChannel.isConnected()) {
                System.out.println("Connect successful");

                String request = "GET / HTTP/1.1\r\n\r\n";

                socketChannel.write(ByteBuffer.wrap(request.getBytes()));

                ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

                int readBytes;
                StringBuilder sb = new StringBuilder();
                if ((readBytes = socketChannel.read(byteBuffer)) != -1) {
                    System.out.println("readBytes: " + readBytes);
                    byteBuffer.flip();

                    while (byteBuffer.remaining() > 0) {
                        sb.append((char) byteBuffer.get());
                    }

                    byteBuffer.clear();
                }

                System.out.println(sb);

                System.out.println("Client finishing");
            } else {
                System.out.println("Connect failed");
            }

        } catch (IOException e) {
            e.printStackTrace(System.out);
        } finally {
            try {
                if (socketChannel != null)
                    socketChannel.close();
            } catch (IOException e) {

            }
        }
    }
}
