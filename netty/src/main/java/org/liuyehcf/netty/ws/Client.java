package org.liuyehcf.netty.ws;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * @author hechenfeng
 * @date 2018/11/3
 */
public class Client {
    public static void main(String[] args) throws Exception {
        final URI webSocketURI = getUri();

        final WebSocketClientHandler webSocketClientHandler = new WebSocketClientHandler(
                WebSocketClientHandshakerFactory.newHandshaker(
                        webSocketURI, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));

        final EventLoopGroup group = new NioEventLoopGroup();
        final Bootstrap boot = new Bootstrap();
        boot.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
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
                .option(ChannelOption.SO_BACKLOG, 1024);

        final Channel channel = boot.connect(webSocketURI.getHost(), webSocketURI.getPort()).sync().channel();
        webSocketClientHandler.handshakeFuture().sync();

        channel.writeAndFlush(new TextWebSocketFrame("Hello, I'm client"));

        TimeUnit.SECONDS.sleep(1);
        System.exit(0);
    }

    private static URI getUri() {
        try {
            return new URI("ws://localhost:8866");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private static final class ClientHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
        @Override
        @SuppressWarnings("all")
        protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
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

            System.out.println("client receive message: " + content);
        }
    }
}
