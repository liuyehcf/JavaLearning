package org.liuyehcf.markdown.format.hexo.context;

import java.io.File;

/**
 * Created by HCF on 2018/1/13.
 */
public interface FileContext {

    /**
     * 获取当前File
     *
     * @return
     */
    File getCurrentFile();

    /**
     * 获取当前的LineContext
     * 注意，每次调用都会new一个出来
     *
     * @return
     */
    LineIterator getLineIteratorOfCurrentFile();

    /**
     * 获取根目录的File对象
     *
     * @return
     */
    File getRootDirectory();

    /**
     * 获取文件目录的File对象
     *
     * @return
     */
    File getFileDirectory();

    /**
     * 获取图像目录的File对象
     *
     * @return
     */
    File getImageDirectory();

    /**
     * 是否全部处理完毕
     *
     * @return
     */
    boolean hasNextFile();

    /**
     * 处理下一个文件
     */
    void moveForward();

    /**
     * 是否包含该文件
     *
     * @param name
     * @return
     */
    boolean containsFile(String name);
}
