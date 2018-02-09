package org.liuyehcf.markdown.format.hexo.context;

/**
 * Created by HCF on 2018/1/14.
 */
public interface LineIterator {

    /**
     * 是否没有下一行了
     *
     * @return
     */
    boolean isNotFinish();

    /**
     * 获取前一行内容
     *
     * @return
     */
    LineElement getPreviousLineElement();

    /**
     * 获取当前行内容
     *
     * @return
     */
    LineElement getCurrentLineElement();

    /**
     * 获取下一行内容
     *
     * @return
     */
    LineElement getNextLineElement();

    /**
     * 在前一行插入内容
     *
     * @param lineElement
     */
    void insertPrevious(LineElement lineElement);


    /**
     * 在后一行插入内容
     *
     * @param lineElement
     */
    void insertNext(LineElement lineElement);

    /**
     * 移除上一行内容
     */
    void removePreviousLine();

    /**
     * 向前移动，处理下一行内容
     */
    void moveForward();

    /**
     * 向后移动，处理上一行内容
     */
    void moveBackward();
}
