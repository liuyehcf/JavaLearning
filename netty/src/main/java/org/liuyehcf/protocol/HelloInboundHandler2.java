package org.liuyehcf.protocol;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * Created by HCF on 2017/12/5.
 */
public class HelloInboundHandler2 extends ChannelInboundHandlerAdapter { // (1)

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println(promptMessage() + "<channelRegistered>");
        ctx.fireChannelRegistered();
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        System.out.println(promptMessage() + "<channelUnregistered>");
        ctx.fireChannelUnregistered();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(promptMessage() + "<channelActive>");
        ctx.fireChannelActive();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println(promptMessage() + "<channelInactive>");
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(promptMessage() + "<channelRead>");
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        System.out.println(promptMessage() + "<channelReadComplete>");
        ctx.fireChannelReadComplete();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println(promptMessage() + "<userEventTriggered>");
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        System.out.println(promptMessage() + "<channelWritabilityChanged>");
        ctx.fireChannelWritabilityChanged();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(promptMessage() + "<exceptionCaught>");
        ctx.fireExceptionCaught(cause);
    }
    private String promptMessage() {
        return "[Inbound-Hello2] : ";
    }
}
