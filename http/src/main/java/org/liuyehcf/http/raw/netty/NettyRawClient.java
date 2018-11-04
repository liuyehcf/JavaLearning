package org.liuyehcf.http.raw.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.ReferenceCountUtil;
import org.liuyehcf.http.raw.HttpRequestBuilder;

import java.nio.charset.Charset;

public class NettyRawClient {
    public static void main(String[] args) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(new ClientHandler());
                        }
                    });

            // 需要启动String Boot模块中的web应用作为服务端
            ChannelFuture channelFuture = bootstrap.connect("localhost", 8080).sync();

            Channel channel = channelFuture.channel();


            String requestContent = buildRequest();

            System.out.print("\n\n>>>>>>>>>>>>>>>>HTTP REQUEST<<<<<<<<<<<<<<<<\n\n");
            System.out.println(requestContent);

            channel.writeAndFlush(Unpooled.wrappedBuffer(requestContent.getBytes()));

            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

    private static String buildRequest() {
        return HttpRequestBuilder.builder()
                .method("GET")
                .url("http://127.0.0.1:8080/home")
                .addHeader("Host", "8080")
                .addHeader("Connection", "close")
                .build();
    }

    private static final class ClientHandler extends ChannelInboundHandlerAdapter {
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) {
            ByteBuf byteBuf = (ByteBuf) msg;
            try {
                int readableBytes = byteBuf.readableBytes();
                byte[] bytes = new byte[readableBytes];
                byteBuf.readBytes(bytes);
                System.out.println(new String(bytes, Charset.defaultCharset()));
            } finally {
                ReferenceCountUtil.release(msg);
            }
        }
    }
}
