package org.liuyehcf.netty.tcp;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @author hechenfeng
 * @date 2018/10/19
 */
public class Client {

    public static void main(String[] args) {
        EventLoopGroup worker = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(worker);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel socketChannel) throws Exception {
                    //获取管道
                    ChannelPipeline pipeline = socketChannel.pipeline();
                    //处理类
                    pipeline.addLast(new ClientHandler());
                }
            });

            //发起异步连接操作
            ChannelFuture future = bootstrap.connect(new InetSocketAddress("127.0.0.1", 8866)).sync();

            //等待客户端链路关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            //优雅的退出，释放NIO线程组
            worker.shutdownGracefully();
        }
    }

    private static final class ClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) {
            int readableBytes = msg.readableBytes();
            byte[] bytes = new byte[readableBytes];
            msg.readBytes(bytes);

            System.out.println(new String(bytes));
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            ctx.channel().writeAndFlush(Unpooled.wrappedBuffer("Hello, I am client!".getBytes()));
            System.out.println("channelActive");
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) {
            System.out.println("channelInactive");
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            ctx.channel().close();
            cause.printStackTrace();
        }
    }
}