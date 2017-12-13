package org.liuyehcf.nio;

import org.junit.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * Created by Liuye on 2017/5/29.
 */
public class TestNIO {

    private static final int _1M=1000000;

    @Test
    public void testFileChannel() {
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream("H:\\testNio.txt");

            FileChannel fileChannel=fileInputStream.getChannel();
            ByteBuffer buffer= ByteBuffer.allocate(100*_1M);

            int readNum=fileChannel.read(buffer);
            System.out.println(readNum);

        } catch (IOException e) {

        }
    }
}
