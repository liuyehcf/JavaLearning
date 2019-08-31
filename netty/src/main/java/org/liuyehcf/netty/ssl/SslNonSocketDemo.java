package org.liuyehcf.netty.ssl;


import java.util.concurrent.*;

/**
 * @author hechenfeng
 * @date 2019/8/29
 */
@SuppressWarnings("all")
public class SslNonSocketDemo {

    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(3);

    public static void main(String[] args) throws Exception {
        BlockingQueue<byte[]> clientToServerPipe = new ArrayBlockingQueue<>(1024);
        BlockingQueue<byte[]> serverToClientPipe = new ArrayBlockingQueue<>(1024);
        BlockingQueue<byte[]> serverReceiveSignal = new SynchronousQueue<>();

        SslClientConverter client = SslClientConverter.create();
        SslServerConverter server = SslServerConverter.create();

        byte[] greetFromClientToServer = "Hello, I'm client!".getBytes();
        byte[] greetFromServerToClient = "Hello, I'm server!".getBytes();

        AbstractSslConverter.InboundConsumer clientInboundConsumer = (inboundBytes) -> {
            System.out.println("receive message from server: " + new String(inboundBytes));
        };
        AbstractSslConverter.OutboundConsumer clientOutboundConsumer = (outboundBytes) -> {
            try {
                clientToServerPipe.put(outboundBytes);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        client.writeOutbound(greetFromClientToServer, clientInboundConsumer, clientOutboundConsumer);

        // 模拟IO事件，client端接受来自服务端的数据
        EXECUTOR.execute(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    byte[] bytes = serverToClientPipe.take();

                    client.writeInbound(bytes, clientInboundConsumer, clientOutboundConsumer);
                }
            } catch (InterruptedException e) {
                System.out.println("client pipe loop finished");
            }
        });

        AbstractSslConverter.InboundConsumer serverInboundConsumer = (inboundBytes) -> {
            System.out.println("receive message from client: " + new String(inboundBytes));
            try {
                serverReceiveSignal.put(greetFromServerToClient);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        AbstractSslConverter.OutboundConsumer serverOutboundConsumer = (outboundBytes) -> {
            try {
                serverToClientPipe.put(outboundBytes);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        // 模拟IO事件，server端接受来自client端的数据
        EXECUTOR.execute(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    byte[] bytes = clientToServerPipe.take();

                    server.writeInbound(bytes, serverInboundConsumer, serverOutboundConsumer);
                }
            } catch (InterruptedException e) {
                System.out.println("server pipe loop finished");
            }
        });

        // 当server端接收到client的消息后，回复客户端
        EXECUTOR.execute(() -> {
            try {
                byte[] bytes = serverReceiveSignal.take();
                server.writeOutbound(bytes, serverInboundConsumer, serverOutboundConsumer);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        TimeUnit.SECONDS.sleep(1);

        System.out.println("finished");
        EXECUTOR.shutdownNow();
    }
}