package org.liuyehcf.format.markdown.processor;


import org.liuyehcf.format.markdown.context.FormatContext;
import org.liuyehcf.format.markdown.context.LineElement;

import java.util.ListIterator;

/**
 * Created by t-chehe on 7/5/2017.
 */
public interface LineProcessor {
    /**
     * 对行内容进行处理，返回处理后的结果
     *
     * @param context
     * @param lineContentListIterator
     */
    void process(FormatContext context, ListIterator<LineElement> lineContentListIterator);

    /**
     * 清理那些有状态的Processor
     */
    void reset();
}
