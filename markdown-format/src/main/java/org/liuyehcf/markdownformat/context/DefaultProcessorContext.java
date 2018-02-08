package org.liuyehcf.markdownformat.context;

import org.liuyehcf.markdownformat.processor.FileProcessor;
import org.liuyehcf.markdownformat.processor.PostFileProcessor;
import org.liuyehcf.markdownformat.processor.PreFileProcessor;
import org.liuyehcf.markdownformat.processor.impl.*;

import java.io.*;
import java.util.*;

import static org.liuyehcf.markdownformat.context.ProcessorEnum.POST_PROCESSOR_INDEX;
import static org.liuyehcf.markdownformat.context.ProcessorEnum.PRE_PROCESSOR_INDEX;
import static org.liuyehcf.markdownformat.log.CommonLogger.DEFAULT_LOGGER;

/**
 * Created by HCF on 2018/1/14.
 */
public class DefaultProcessorContext implements ProcessorContext {

    private Map<ProcessorEnum, List<FileProcessor>> processors;

    public DefaultProcessorContext() {
        initProcessors();
    }

    private void initProcessors() {
        processors = new HashMap<>();
        for (ProcessorEnum processorEnum : ProcessorEnum.values()) {
            processors.put(processorEnum, new ArrayList<>());
        }

        addProcessor(new CodeProcessor());
        addProcessor(new ImageAddressCheckProcessor());
        addProcessor(new IndexProcessor());
        addProcessor(new InnerLinkCheckProcessor());
        addProcessor(new RedundantEmptyProcessor());
        addProcessor(new RemoveControlCharacterProcessor());
        addProcessor(new ResourceLinkProcessor());
        addProcessor(new SubItemProcessor());
        addProcessor(new TableProcessor());

        processors = Collections.unmodifiableMap(processors);
    }

    private void addProcessor(FileProcessor processor) {
        if (processor instanceof PreFileProcessor) {
            processors.get(PRE_PROCESSOR_INDEX).add(processor);
        } else if (processor instanceof PostFileProcessor) {
            processors.get(POST_PROCESSOR_INDEX).add(processor);
        } else {
            processors.get(POST_PROCESSOR_INDEX).add(processor);
        }
    }


    @Override
    public FileProcessor nextProcessor() {
        return null;
    }

    @Override
    public boolean hasNextProcessor() {
        return false;
    }

    @Override
    public void process(FileContext fileContext) {
        for (ProcessorEnum processorEnum : ProcessorEnum.values()) {
            for (FileProcessor fileProcessor : processors.get(processorEnum)) {
                fileProcessor.process(fileContext);
            }
        }

        try {
            reWriteIfModify(fileContext);
        } catch (IOException e) {

        }
    }

    private void reWriteIfModify(FileContext fileContext) throws IOException {
        if (isModify(fileContext)) {
            reWrite(fileContext);
        }
    }

    private boolean isModify(FileContext fileContext) throws IOException {
        List<String> originContents = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(fileContext.getCurrentFile()));

        String line;

        while ((line = reader.readLine()) != null) {
            originContents.add(line);
        }

        reader.close();

        Iterator<String> originIterator = originContents.iterator();
        LineIterator curIterator = fileContext.getLineIteratorOfCurrentFile();

        while (originIterator.hasNext()
                && curIterator.isNotFinish()) {

            String originLine = originIterator.next();
            String currentLine = curIterator.getCurrentLineElement().getContent();

            if (!originLine.equals(currentLine)) {
                return true;
            }

            curIterator.moveForward();
        }

        return originIterator.hasNext() || curIterator.isNotFinish();
    }

    private void reWrite(FileContext fileContext) throws IOException {
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileContext.getCurrentFile()));

        LineIterator lineIterator = fileContext.getLineIteratorOfCurrentFile();

        while (lineIterator.isNotFinish()) {
            bufferedWriter.write(lineIterator.getCurrentLineElement().getContent() + "\n");
            lineIterator.moveForward();
        }

        bufferedWriter.close();

        DEFAULT_LOGGER.info("rewrite file '{}'", fileContext.getCurrentFile());
    }
}
