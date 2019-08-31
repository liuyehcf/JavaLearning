package org.liuyehcf.netty.ssl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.ReferenceCountUtil;

public abstract class AbstractSslConverter {

    protected final EmbeddedChannel channel;

    protected AbstractSslConverter(EmbeddedChannel channel) {
        this.channel = channel;
    }

    public void writeOutbound(byte[] bytes, InboundConsumer inboundConsumer, OutboundConsumer outboundConsumer) {
        channel.writeOutbound(Unpooled.wrappedBuffer(bytes));
        channel.flushOutbound();

        if (inboundConsumer != null) {
            byte[] inboundData = readInbound();
            if (inboundData != null) {
                inboundConsumer.consumeInbound(inboundData);
            }
        }

        if (outboundConsumer != null) {
            byte[] outboundData = readOutbound();
            if (outboundData != null) {
                outboundConsumer.consumeOutbound(outboundData);
            }
        }
    }

    public void writeInbound(byte[] bytes, InboundConsumer inboundConsumer, OutboundConsumer outboundConsumer) {
        channel.writeInbound(Unpooled.wrappedBuffer(bytes));
        channel.flushInbound();

        if (inboundConsumer != null) {
            byte[] inboundData = readInbound();
            if (inboundData != null) {
                inboundConsumer.consumeInbound(inboundData);
            }
        }

        if (outboundConsumer != null) {
            byte[] outboundData = readOutbound();
            if (outboundData != null) {
                outboundConsumer.consumeOutbound(outboundData);
            }
        }
    }

    public byte[] readInbound() {
        ByteBuf byteBuf;
        ByteBuf cache = Unpooled.buffer();
        try {
            while ((byteBuf = channel.readInbound()) != null) {
                try {
                    cache.writeBytes(byteBuf);
                } finally {
                    ReferenceCountUtil.release(byteBuf);
                }
            }

            int readableBytes = cache.readableBytes();
            if (readableBytes == 0) {
                return null;
            }

            byte[] totalBytes = new byte[readableBytes];
            cache.readBytes(totalBytes);

            return totalBytes;
        } finally {
            ReferenceCountUtil.release(cache);
        }
    }

    public byte[] readOutbound() {
        ByteBuf byteBuf;
        ByteBuf cache = Unpooled.buffer();
        try {
            while ((byteBuf = channel.readOutbound()) != null) {
                try {
                    cache.writeBytes(byteBuf);
                } finally {
                    ReferenceCountUtil.release(byteBuf);
                }
            }

            int readableBytes = cache.readableBytes();
            if (readableBytes == 0) {
                return null;
            }

            byte[] totalBytes = new byte[readableBytes];
            cache.readBytes(totalBytes);

            return totalBytes;
        } finally {
            ReferenceCountUtil.release(cache);
        }
    }

    public void close() {
        channel.close();
    }

    @FunctionalInterface
    public interface InboundConsumer {

        /**
         * consume bytes
         *
         * @param bytes data
         */
        void consumeInbound(byte[] bytes);
    }

    @FunctionalInterface
    public interface OutboundConsumer {

        /**
         * consume bytes
         *
         * @param bytes data
         */
        void consumeOutbound(byte[] bytes);
    }
}
