package org.liuyehcf.http.raw.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import org.liuyehcf.http.raw.HttpRequestBuilder;
import org.liuyehcf.http.raw.netty.handler.EchoInBoundHandler;
import org.liuyehcf.http.raw.netty.handler.EchoOutBoundHandler;

import java.net.URI;

/**
 * Created by HCF on 2017/12/16.
 */
public class NettyHttpRequestDemo {
    public static void main(String[] args) {
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 客户端接收到的是HttpResponse响应，所以要使用HttpResponseDecoder进行解码
                            socketChannel.pipeline().addLast(new HttpResponseDecoder());
                            // 客户端发送的是HttpRequest，所以要使用HttpRequestEncoder进行编码
                            socketChannel.pipeline().addLast(new HttpRequestEncoder());
                            socketChannel.pipeline().addLast(new EchoInBoundHandler());
                            socketChannel.pipeline().addLast(new EchoOutBoundHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect("localhost", 8080).sync();

            Channel channel = channelFuture.channel();

            // todo 需要启动String Boot模块中的web应用作为服务端
            URI uri = new URI("http://127.0.0.1:8080/home");
            DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET,
                    uri.toASCIIString());

            // 构建http请求
            request.headers().set(HttpHeaders.Names.HOST, 8080);
            request.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
            request.headers().set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());

            channel.writeAndFlush(request);

            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {

        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
