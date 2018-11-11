package org.liuyehcf.netty.ws;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

/**
 * @author hechenfeng
 * @date 2018/11/3
 */
public class Client {
    public static void main(String[] args) {
        URI webSocketURI = getUri();
        HttpHeaders httpHeaders = new DefaultHttpHeaders();
        WebSocketClientHandshaker handShaker = WebSocketClientHandshakerFactory.newHandshaker(webSocketURI, WebSocketVersion.V13, null, true, httpHeaders);

        WebSocketClientHandler webSocketClientHandler = new WebSocketClientHandler(handShaker);

        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap boot = new Bootstrap();
        boot.group(group)
                .handler(new LoggingHandler(LogLevel.INFO))
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    protected void initChannel(SocketChannel socketChannel) {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new HttpClientCodec());
                        pipeline.addLast(new HttpObjectAggregator(65535));
                        pipeline.addLast(new ChunkedWriteHandler());
                        pipeline.addLast(WebSocketClientCompressionHandler.INSTANCE);
                        pipeline.addLast(webSocketClientHandler);
                        pipeline.addLast(new ClientHandler());
                    }
                })
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_BACKLOG, 1024 * 1024 * 10);

        try {
            final Channel channel = boot.connect(webSocketURI.getHost(), webSocketURI.getPort()).sync().channel();
            handShaker.handshake(channel);
            webSocketClientHandler.handshakeFuture().sync();

            channel.writeAndFlush(new TextWebSocketFrame("Hello, I'm client"));

            TimeUnit.SECONDS.sleep(2);
            System.exit(0);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static URI getUri() {
        try {
            return new URI("ws://localhost:8866");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class ClientHandler extends AbstractWebSocketHandler {
        @Override
        void doChannelRead0(ChannelHandlerContext ctx, String content) {
            System.out.println("client receive message: " + content);
        }
    }
}
