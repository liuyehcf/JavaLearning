package org.liuyehcf.netty.ws;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;

import java.nio.charset.Charset;

/**
 * @author hechenfeng
 * @date 2018/11/3
 */
public abstract class AbstractWebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {
    @Override
    protected final void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) {
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

        doChannelRead0(ctx, content);
    }

    @Override
    public final void channelActive(ChannelHandlerContext ctx) {
        System.out.println("channelActive");
    }

    @Override
    public final void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("channelInactive");
    }

    @Override
    public final void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.channel().close();
        cause.printStackTrace();
    }

    abstract void doChannelRead0(ChannelHandlerContext ctx, String content);
}
