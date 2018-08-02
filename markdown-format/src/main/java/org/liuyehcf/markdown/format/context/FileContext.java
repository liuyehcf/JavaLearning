package org.liuyehcf.markdown.format.context;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import static org.liuyehcf.markdown.format.constant.StringConstant.CODE_BOUNDARY;
import static org.liuyehcf.markdown.format.log.DefaultLogger.LOGGER;

/**
 * Created by HCF on 2018/1/13.
 */
public interface FileContext {

    static void readFile(BufferedReader reader, File file, LinkedList<LineElement> lineElements) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains(CODE_BOUNDARY)) {
                //cache "```" itself
                lineElements.add(new DefaultLineElement(line, true));

                while ((line = reader.readLine()) != null && !line.contains(CODE_BOUNDARY)) {
                    lineElements.add(new DefaultLineElement(line, true));
                }

                if (line == null) {
                    LOGGER.error("file [{}] contains wrong code format", file);
                    throw new RuntimeException();
                }

                //cache "```" itself
                lineElements.add(new DefaultLineElement(line, true));

            } else {

                lineElements.add(new DefaultLineElement(line, false));
            }
        }
    }

    /**
     * 初始化当前文件上下文
     */
    void initFileContext();

    /**
     * 获取当前File
     */
    File getFile();

    /**
     * 获取当前的LineContext
     * 注意，每次调用都会new一个出来
     */
    LineIterator getLineIterator();

    /**
     * 获取当前文件的属性值
     */
    String getProperty(String key);

    /**
     * 获取根目录的File对象
     */
    File getRootDirectory();

    /**
     * 获取文件目录的File对象
     */
    File getFileDirectory();

    /**
     * 获取图像目录的File对象
     */
    File getImageDirectory();

    /**
     * 是否全部处理完毕
     */
    boolean hasNextFile();

    /**
     * 处理下一个文件
     */
    void moveForward();

    /**
     * 是否包含指定文件
     */
    boolean containsFile(String name);
}
