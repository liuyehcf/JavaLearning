package org.liuyehcf.markdown.format.context;

import org.liuyehcf.markdown.format.log.DefaultLogger;
import org.liuyehcf.markdown.format.processor.FileProcessor;
import org.liuyehcf.markdown.format.processor.PostFileProcessor;
import org.liuyehcf.markdown.format.processor.PreFileProcessor;

import java.io.*;
import java.util.*;

/**
 * @author chenlu
 * @date 2018/8/2
 */
public abstract class AbstractProcessorContext implements ProcessorContext {
    private Map<ProcessorEnum, List<FileProcessor>> processors;

    AbstractProcessorContext() {
        init();
    }

    private void init() {
        processors = new HashMap<>();
        for (ProcessorEnum processorEnum : ProcessorEnum.values()) {
            processors.put(processorEnum, new ArrayList<>());
        }

        initProcessors();

        processors = Collections.unmodifiableMap(processors);
    }

    abstract void initProcessors();

    void addProcessor(FileProcessor processor) {
        if (processor instanceof PreFileProcessor) {
            processors.get(ProcessorEnum.PRE_PROCESSOR_INDEX).add(processor);
        } else if (processor instanceof PostFileProcessor) {
            processors.get(ProcessorEnum.POST_PROCESSOR_INDEX).add(processor);
        } else {
            processors.get(ProcessorEnum.POST_PROCESSOR_INDEX).add(processor);
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
            //ignore
        }
    }

    private void reWriteIfModify(FileContext fileContext) throws IOException {
        if (isModify(fileContext)) {
            reWrite(fileContext);
        }
    }

    private boolean isModify(FileContext fileContext) throws IOException {
        List<String> originContents = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(fileContext.getFile()));

        String line;

        while ((line = reader.readLine()) != null) {
            originContents.add(line);
        }

        reader.close();

        Iterator<String> originIterator = originContents.iterator();
        LineIterator curIterator = fileContext.getLineIterator();

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
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileContext.getFile()));

        LineIterator lineIterator = fileContext.getLineIterator();

        while (lineIterator.isNotFinish()) {
            bufferedWriter.write(lineIterator.getCurrentLineElement().getContent() + "\n");
            lineIterator.moveForward();
        }

        bufferedWriter.close();

        DefaultLogger.LOGGER.info("rewrite file '{}'", fileContext.getFile());
    }
}
