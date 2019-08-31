package org.liuyehcf.netty.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.util.ReferenceCountUtil;

import javax.net.ssl.KeyManagerFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author hechenfeng
 * @date 2019/8/29
 */
public class SslConverter {

    private static final String STORE_TYPE = "PKCS12";
    private static final String KEY_STORE_PASSWORD = "123456";
    private static final String KEY_PASSWORD = KEY_STORE_PASSWORD;

    private static KeyManagerFactory keyManagerFactory;

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            4, 4,
            60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(128), new ThreadPoolExecutor.AbortPolicy());

    static {
        try {
            keyManagerFactory = initKeyManagerFactory();
        } catch (Exception e) {
            throw new Error();
        }
    }

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) throws Exception {
        EmbeddedChannel clientChannel = new EmbeddedChannel();

        clientChannel.pipeline()
                .addLast(SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build().newHandler(clientChannel.alloc()));


        EmbeddedChannel serverChannel = new EmbeddedChannel();
        serverChannel.pipeline()
                .addLast(SslContextBuilder.forServer(keyManagerFactory).build().newHandler(serverChannel.alloc()));


        clientChannel.writeOutbound(Unpooled.wrappedBuffer("hello server, I'm client".getBytes()));

        EXECUTOR.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] bytes = readOutbound(clientChannel);

                if (bytes.length > 0) {
                    serverChannel.writeInbound(Unpooled.wrappedBuffer(bytes));
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        EXECUTOR.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] bytes = readOutbound(serverChannel);

                if (bytes.length > 0) {
                    clientChannel.writeInbound(Unpooled.wrappedBuffer(bytes));
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        EXECUTOR.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] bytes = readInbound(serverChannel);

                if (bytes.length > 0) {
                    System.out.println("server receive: " + new String(bytes));

                    serverChannel.writeOutbound(Unpooled.wrappedBuffer("hello client, I'm server".getBytes()));
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        EXECUTOR.execute(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                byte[] bytes = readInbound(clientChannel);

                if (bytes.length > 0) {
                    System.out.println("client receive: " + new String(bytes));
                }

                try {
                    TimeUnit.MILLISECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        TimeUnit.DAYS.sleep(1);
    }

    private static KeyManagerFactory initKeyManagerFactory() throws Exception {
        InputStream keyStoreStream = ClassLoader.getSystemClassLoader().getResourceAsStream("liuyehcf_server_ks");

        // keyStore
        KeyStore keyStore = KeyStore.getInstance(STORE_TYPE);
        keyStore.load(keyStoreStream, KEY_STORE_PASSWORD.toCharArray());

        // keyManagerFactory
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, KEY_PASSWORD.toCharArray());

        return keyManagerFactory;
    }


    private static byte[] readOutbound(EmbeddedChannel ch) {
        ByteBuf byteBuf;
        ByteBuf cache = Unpooled.buffer();
        try {
            while ((byteBuf = ch.readOutbound()) != null) {
                try {
                    cache.writeBytes(byteBuf);
                } finally {
                    ReferenceCountUtil.release(byteBuf);
                }
            }

            byte[] totalBytes = new byte[cache.readableBytes()];
            cache.readBytes(totalBytes);

            return totalBytes;
        } finally {
            ReferenceCountUtil.release(cache);
        }
    }

    private static byte[] readInbound(EmbeddedChannel ch) {
        ByteBuf byteBuf;
        ByteBuf cache = Unpooled.buffer();
        try {
            while ((byteBuf = ch.readInbound()) != null) {
                try {
                    cache.writeBytes(byteBuf);
                } finally {
                    ReferenceCountUtil.release(byteBuf);
                }
            }

            byte[] totalBytes = new byte[cache.readableBytes()];
            cache.readBytes(totalBytes);

            return totalBytes;
        } finally {
            ReferenceCountUtil.release(cache);
        }
    }
}