package org.liuyehcf.markdown.format.context;

import org.liuyehcf.markdown.format.processor.impl.*;

/**
 * Created by HCF on 2018/1/14.
 */
public class HexoProcessorContext extends AbstractProcessorContext {
    @Override
    void initProcessors() {
        addProcessor(new TitleProcessor());
        addProcessor(new ImageAddressCheckProcessor());
        addProcessor(new IndexProcessor());
        addProcessor(new InnerLinkCheckProcessor());
        addProcessor(new LatexFormulaWrapperProcessor());
        addProcessor(new RedundantEmptyProcessor());
        addProcessor(new RemoveControlCharacterProcessor());
        addProcessor(new ResourceLinkProcessor());
        addProcessor(new SubItemProcessor());
        addProcessor(new TableProcessor());
    }
}
