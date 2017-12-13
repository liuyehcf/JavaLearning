package org.liuyehcf.test.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * Created by Liuye on 2017/6/4.
 */
public class TestSocketChannelInterrupt {
    public static void main(String[] args) {
        Thread server = new Thread(new Runnable() {
            @Override
            public void run() {
                initServerSocketChannel();
            }
        });

        server.start();

        Thread client = new Thread(new Runnable() {
            @Override
            public void run() {
                initClientSocketChannel();
            }
        });

        client.start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {

        }

        client.interrupt();

        server.interrupt();
    }

    private static void initServerSocketChannel() {
        ServerSocketChannel serverSocketChannel = null;
        try {
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.bind(new InetSocketAddress("localhost", 9999));
        } catch (IOException e) {

        }

        try {
            while (true)
                serverSocketChannel.accept();
        } catch (IOException e) {
            System.out.println(e.getClass().getName());
        }

    }

    private static void initClientSocketChannel() {
        SocketChannel socketChannel = null;

        try {
            socketChannel = SocketChannel.open();
            socketChannel.connect(new InetSocketAddress("localhost", 9999));
        } catch (IOException e) {
        }

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        try {
            socketChannel.read(byteBuffer);
        } catch (IOException e) {
            System.out.println(e.getClass().getName());
        }
    }
}
