package com.jenkov.nioserver;

import org.junit.Test;

import java.nio.ByteBuffer;


/**
 * Created by jjenkov on 18-10-2015.
 */
public class MessageTest {


    @Test
    public void testWriteToMessage() {
        MessageBuffer messageBuffer = new MessageBuffer();

        Message message = messageBuffer.getMessage();
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024);

        fill(byteBuffer, 4096);

        int written = message.writeToMessage(byteBuffer);
        assertEquals(4096, written);
        assertEquals(4096, message.length);
        assertSame(messageBuffer.smallMessageBuffer, message.sharedArray);

        fill(byteBuffer, 124 * 1024);
        written = message.writeToMessage(byteBuffer);
        assertEquals(124 * 1024, written);
        assertEquals(128 * 1024, message.length);
        assertSame(messageBuffer.mediumMessageBuffer, message.sharedArray);

        fill(byteBuffer, (1024 - 128) * 1024);
        written = message.writeToMessage(byteBuffer);
        assertEquals(896 * 1024, written);
        assertEquals(1024 * 1024, message.length);
        assertSame(messageBuffer.largeMessageBuffer, message.sharedArray);

        fill(byteBuffer, 1);
        written = message.writeToMessage(byteBuffer);
        assertEquals(-1, written);

    }

    private void fill(ByteBuffer byteBuffer, int length) {
        byteBuffer.clear();
        for (int i = 0; i < length; i++) {
            byteBuffer.put((byte) (i % 128));
        }
        byteBuffer.flip();
    }

    private void assertEquals(int i, int j) {
        if (i != j) {
            throw new RuntimeException();
        }
    }

    private void assertSame(byte[] bytes1, byte[] bytes2) {
        if (bytes1 == null || bytes2 == null || bytes1.length != bytes2.length) {
            throw new RuntimeException();
        }
        for (int i = 0; i < bytes1.length; i++) {
            if (bytes1[i] != bytes2[i]) {
                throw new RuntimeException();
            }
        }
    }
}
