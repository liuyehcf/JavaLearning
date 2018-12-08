package org.liuyehcf.netty.ws;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.util.concurrent.TimeUnit;

/**
 * @author hechenfeng
 * @date 2018/11/3
 */
public class Server {

    private static final String HOST = "localhost";
    private static final int PORT = 8866;
    private static final boolean OPEN_SSL = true;

    private static final String KEY_STORE_PATH = System.getProperty("user.home") + File.separator + "liuyehcf_server_ks";
    private static final String STORE_TYPE = "PKCS12";
    private static final String PROTOCOL = "TLS";
    private static final String KEY_STORE_PASSWORD = "123456";
    private static final String KEY_PASSWORD = KEY_STORE_PASSWORD;

    public static void main(String[] args) throws Exception {
        final EventLoopGroup boss = new NioEventLoopGroup();
        final EventLoopGroup worker = new NioEventLoopGroup();

        final ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS));
                        if (OPEN_SSL) {
                            pipeline.addLast(createSslHandlerUsingRawApi());
//                            pipeline.addLast(createSslHandlerUsingNetty(pipeline));
                        }
                        pipeline.addLast(new HttpServerCodec());
                        pipeline.addLast(new HttpObjectAggregator(65535));
                        pipeline.addLast(new ChunkedWriteHandler());
                        pipeline.addLast(new WebSocketServerCompressionHandler());
                        pipeline.addLast(new WebSocketServerProtocolHandler("/", null, true));
                        pipeline.addLast(new ServerHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 1024)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_REUSEADDR, true);

        final ChannelFuture future = bootstrap.bind(PORT).sync();
        System.out.println("server start ...... ");

        future.channel().closeFuture().sync();
    }

    private static ChannelHandler createSslHandlerUsingRawApi() throws Exception {
        // keyStore
        KeyStore keyStore = KeyStore.getInstance(STORE_TYPE);
        keyStore.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());

        // keyManagerFactory
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, KEY_PASSWORD.toCharArray());

        // trustManagerFactory
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);

        // sslContext
        SSLContext sslContext = SSLContext.getInstance(PROTOCOL);
        sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);

        SSLEngine sslEngine = sslContext.createSSLEngine();
        sslEngine.setUseClientMode(false);
        return new SslHandler(sslEngine);
    }

    private static ChannelHandler createSslHandlerUsingNetty(ChannelPipeline pipeline) throws Exception {
        // keyStore
        KeyStore keyStore = KeyStore.getInstance(STORE_TYPE);
        keyStore.load(new FileInputStream(KEY_STORE_PATH), KEY_STORE_PASSWORD.toCharArray());

        // keyManagerFactory
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, KEY_PASSWORD.toCharArray());

        return SslContextBuilder.forServer(keyManagerFactory).build()
                .newHandler(pipeline.channel().alloc(), HOST, PORT);
    }

    private static final class ServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
        @Override
        @SuppressWarnings("all")
        protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) {
            final String content;
            if (msg instanceof BinaryWebSocketFrame) {
                BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) msg;
                ByteBuf byteBuf = binaryWebSocketFrame.content();
                byte[] bytes = new byte[byteBuf.readableBytes()];
                byteBuf.getBytes(0, bytes);
                content = new String(bytes, Charset.defaultCharset());
            } else if (msg instanceof TextWebSocketFrame) {
                content = ((TextWebSocketFrame) msg).text();
            } else if (msg instanceof PongWebSocketFrame) {
                content = "Pong";
            } else if (msg instanceof ContinuationWebSocketFrame) {
                content = "Continue";
            } else if (msg instanceof PingWebSocketFrame) {
                content = "Ping";
            } else if (msg instanceof CloseWebSocketFrame) {
                content = "Close";
                ctx.close();
            } else {
                throw new RuntimeException();
            }

            System.out.println("server receive message: " + content);

            ctx.channel().writeAndFlush(new BinaryWebSocketFrame(Unpooled.wrappedBuffer("Hi, I'm Server".getBytes())));
        }
    }
}
