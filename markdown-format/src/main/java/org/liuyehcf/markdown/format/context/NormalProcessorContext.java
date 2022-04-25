package org.liuyehcf.markdown.format.context;

import org.liuyehcf.markdown.format.processor.impl.*;

/**
 * @author hechenfeng
 * @date 2018/8/2
 */
public class NormalProcessorContext extends AbstractProcessorContext {
    @Override
    void initProcessors() {
        addProcessor(new LocalAddressCheckProcessor());
        addProcessor(new IndexProcessor());
        addProcessor(new InnerLinkCheckProcessor());
        addProcessor(new RedundantEmptyProcessor());
        addProcessor(new RemoveControlCharacterProcessor());
        addProcessor(new ResourceLinkProcessor());
        addProcessor(new SubItemProcessor());
        addProcessor(new TableProcessor());
    }
}
