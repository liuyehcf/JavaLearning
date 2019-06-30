package org.liuyehcf.netty.ws;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.*;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractWebSocketHandler extends SimpleChannelInboundHandler<WebSocketFrame> {

    private final List<byte[]> fragmentCache = new ArrayList<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame webSocketFrame) {
        byte[] curFragmentBytes;
        if (webSocketFrame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) webSocketFrame;
            curFragmentBytes = textWebSocketFrame.text().getBytes();
        } else if (webSocketFrame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame binaryWebSocketFrame = (BinaryWebSocketFrame) webSocketFrame;
            ByteBuf content = binaryWebSocketFrame.content();
            curFragmentBytes = new byte[content.readableBytes()];
            content.getBytes(0, curFragmentBytes);
        } else if (webSocketFrame instanceof ContinuationWebSocketFrame) {
            ContinuationWebSocketFrame continuationWebSocketFrame = (ContinuationWebSocketFrame) webSocketFrame;
            ByteBuf content = continuationWebSocketFrame.content();
            curFragmentBytes = new byte[content.readableBytes()];
            content.getBytes(0, curFragmentBytes);
        } else if (webSocketFrame instanceof PingWebSocketFrame) {
            return;
        } else if (webSocketFrame instanceof PongWebSocketFrame) {
            return;
        } else if (webSocketFrame instanceof CloseWebSocketFrame) {
            ctx.channel().close();
            return;
        } else {
            throw new UnsupportedOperationException("unsupported WebSocketFrame's type. type='" + webSocketFrame.getClass() + "'");
        }

        byte[] frameBytes;

        if (webSocketFrame.isFinalFragment() && fragmentCache.isEmpty()) {
            frameBytes = curFragmentBytes;
        } else if (webSocketFrame.isFinalFragment()) {
            int allLength = 0;
            for (byte[] bytes : fragmentCache) {
                allLength += bytes.length;
            }
            allLength += curFragmentBytes.length;

            frameBytes = new byte[allLength];
            int startPos = 0;
            for (byte[] fragmentBytes : fragmentCache) {
                System.arraycopy(fragmentBytes, 0, frameBytes, startPos, fragmentBytes.length);
                startPos += fragmentBytes.length;
            }

            System.arraycopy(curFragmentBytes, 0, frameBytes, startPos, curFragmentBytes.length);

            fragmentCache.clear();
        } else {
            fragmentCache.add(curFragmentBytes);
            return;
        }

        doChannelRead0(ctx, frameBytes);
    }

    protected abstract void doChannelRead0(ChannelHandlerContext ctx, byte[] bytes);
}
