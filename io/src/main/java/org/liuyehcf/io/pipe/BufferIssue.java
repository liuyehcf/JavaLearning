package org.liuyehcf.io.pipe;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.Charset;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BufferIssue {

    private static final ExecutorService THREAD_POOL = Executors.newCachedThreadPool();

    public static void main(String[] args) throws Exception {
        final int writeSize = 2048;
        final int bufferSize = 1024;
        final PipedInputStream pipedInputStream = new PipedInputStream(bufferSize);
        final PipedOutputStream pipedOutputStream = new PipedOutputStream();
        pipedInputStream.connect(pipedOutputStream);

        // non-blocking scanner
        final Thread scanThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        final int available = pipedInputStream.available();
                        if (available > 0) {
                            // do IO operation in other thread
                            THREAD_POOL.execute(() -> {
                                final byte[] bytes = new byte[available];
                                try {
                                    int actualBytes = pipedInputStream.read(bytes);

                                    System.out.println(new String(bytes, 0, actualBytes, Charset.defaultCharset()));

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } catch (IOException e) {
                        break;
                    }

                    // sleep for a while
                    TimeUnit.MILLISECONDS.sleep(1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        scanThread.start();

        final Thread networkDataThread = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (scanner.next() != null) {
                try {
                    pipedOutputStream.write(getString(writeSize).getBytes());
                    pipedOutputStream.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        networkDataThread.start();

        scanThread.join();
        networkDataThread.join();
    }

    private static String getString(int length) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append("a");
        }
        return sb.toString();
    }
}
