package org.liuyehcf.markdown.format.processor;

import org.liuyehcf.markdown.format.context.FileContext;
import org.liuyehcf.markdown.format.context.LineIterator;

public abstract class AbstractFileProcessor implements FileProcessor {
    @Override
    public final void process(FileContext fileContext) {
        // 前置处理
        beforeProcess(fileContext);

        // 获取文件行迭代器
        LineIterator iterator = fileContext.getLineIterator();

        while (iterator.isNotFinish()) {

            // 行处理逻辑，交由子类实现
            doProcess(fileContext, iterator);

            // 前进一行
            iterator.moveForward();
        }

        // 后置处理
        afterProcess(fileContext);
    }

    /**
     * 行处理逻辑
     *
     * @param fileContext
     * @param iterator
     */
    protected abstract void doProcess(FileContext fileContext, LineIterator iterator);

    /**
     * 提供一个前置钩子方法
     * 选择性覆盖
     *
     * @param fileContext
     */
    protected void beforeProcess(FileContext fileContext) {

    }

    /**
     * 提供一个后置钩子方法
     * 选择性覆盖
     *
     * @param fileContext
     */
    protected void afterProcess(FileContext fileContext) {

    }
}
