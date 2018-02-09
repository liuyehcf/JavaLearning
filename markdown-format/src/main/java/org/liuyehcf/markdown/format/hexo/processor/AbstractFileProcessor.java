package org.liuyehcf.markdown.format.hexo.processor;

import org.liuyehcf.markdown.format.hexo.context.FileContext;
import org.liuyehcf.markdown.format.hexo.context.LineIterator;

public abstract class AbstractFileProcessor implements FileProcessor {
    @Override
    public final void process(FileContext fileContext) {
        // 获取文件行迭代器
        LineIterator iterator = fileContext.getLineIterator();

        while (iterator.isNotFinish()) {

            // 行处理逻辑，交由子类实现
            doProcess(fileContext, iterator);

            // 前进一行
            iterator.moveForward();
        }

        // 钩子方法
        reset();
    }

    // 行处理逻辑
    protected abstract void doProcess(FileContext fileContext, LineIterator iterator);

    // 有些处理器需要重置自身状态，提供一个钩子方法
    protected void reset() {
        // do nothing
    }
}
