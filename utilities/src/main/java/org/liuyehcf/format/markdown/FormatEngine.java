package org.liuyehcf.format.markdown;

import org.liuyehcf.format.markdown.context.FormatContext;
import org.liuyehcf.format.markdown.context.LineElement;
import org.liuyehcf.format.markdown.processor.LineProcessor;
import org.liuyehcf.format.markdown.processor.impl.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by t-chehe on 7/3/2017.
 */
public class FormatEngine {

    private List<LineProcessor> processors;

    private FormatContext formatContext;

    private List<String> originalFileContent;

    public FormatEngine(boolean indentation, File[] files, String sourceDir) {
        processors = new ArrayList<>();

        processors.add(new IndexProcessor(indentation));
        processors.add(new ResourceLinkProcessor());
        processors.add(new SubItemProcessor());
        processors.add(new InnerLinkProcessor());
        processors.add(new RedundantEmptyLineProcessor());
        processors.add(new CodeProcessor());
        processors.add(new ImageAddressCheckProcess());

        formatContext = new FormatContext(files, sourceDir);
    }

    public static void main(String[] args) {

        if (args == null || args.length != 2)
            throw new RuntimeException("Wrong param num");

        File baseDir = new File(args[0] + "/_posts");
        String param = args[1];

        if (!baseDir.isDirectory())
            throw new RuntimeException("Please type correct base path");

        if (!param.equalsIgnoreCase("true") && !param.equalsIgnoreCase("false")) {
            throw new RuntimeException("Please type correct param");
        }

        File[] files = baseDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".md");
            }
        });

        FormatEngine engine = new FormatEngine(param.equalsIgnoreCase("true"), files, args[0]);

        for (File file : files) {
            engine.format(file);
        }

        System.out.println("finished");
    }

    public void format(File inputFile) {

        resetFormatEngineWithFile(inputFile);

        try {

            getOriginalContentOfFile(inputFile);

            setUpFormatContextOfFile(inputFile);

            doFormat();

            reWriteIfChanged(inputFile);

        } catch (IOException e) {
            System.err.println("can not find the file");
        }
    }

    private void resetFormatEngineWithFile(File inputFile) {
        formatContext.setFile(inputFile);

        formatContext.clear();

        resetProcessors();
    }

    private void resetProcessors() {
        for (LineProcessor processor : processors) {
            processor.reset();
        }
    }

    private List<String> getOriginalContentOfFile(File inputFile) {
        originalFileContent = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));

            String line;

            while ((line = reader.readLine()) != null) {
                originalFileContent.add(line);
            }

        } catch (IOException e) {

        }

        return originalFileContent;
    }

    private void setUpFormatContextOfFile(File inputFile) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), "UTF-8"));
        String line;
        //首先跳过所有代码段，代码段内的内容不做任何修改
        while ((line = reader.readLine()) != null) {
            if (line.contains("```")) {
                //cache "```" itself
                formatContext.add(new LineElement(line, true));

                while ((line = reader.readLine()) != null && !line.contains("```")) {
                    formatContext.add(new LineElement(line, true));
                }

                if (line == null) {
                    throw new RuntimeException("``` No pairs appear");
                }

                //cache "```" itself
                formatContext.add(new LineElement(line, true));

            } else {

                formatContext.add(new LineElement(line, false));
            }
        }
    }

    private void doFormat() {
        ListIterator<LineElement> lineContentListIterator = formatContext.getIterator();

        while (lineContentListIterator.hasNext()) {
            for (LineProcessor processor : processors) {
                processor.process(formatContext, lineContentListIterator);
            }

            lineContentListIterator.next();
        }
    }

    private void reWriteIfChanged(File outputFile) throws IOException {
        if (isChanged()) {
            reWrite(outputFile);
        }
    }

    private boolean isChanged() {
        Iterator<LineElement> curContentIterator = formatContext.getIterator();
        Iterator<String> originalContentIterator = originalFileContent.iterator();

        while (curContentIterator.hasNext()
                && originalContentIterator.hasNext()) {
            String originalContent = originalContentIterator.next();
            String curContent = curContentIterator.next().getContent();

            if (!originalContent.equals(curContent)) return true;
        }

        return curContentIterator.hasNext()
                || originalContentIterator.hasNext();
    }

    private void reWrite(File outputFile) throws IOException {
        System.out.println("==> ReWrite File " + outputFile.getName());

        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8"));

        ListIterator<LineElement> lineContentListIterator = formatContext.getIterator();

        while (lineContentListIterator.hasNext()) {
            writer.write(lineContentListIterator.next().getContent() + "\n");
        }

        writer.flush();
    }
}
