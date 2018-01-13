package org.liuyehcf.markdownformat.context;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by HCF on 2018/1/14.
 */
public class DefaultLineContext implements LineContext {

    private ListIterator<String> iterator;

    private boolean isPreviousValid;

    private boolean isCurrentValid;

    private boolean isNextValid;

    private String previousLine;

    private String currentLine;

    private String nextLine;

    public DefaultLineContext(LinkedList<String> lines) {
        iterator = lines.listIterator();
    }

    @Override
    public boolean hasPreviousLine() {
        return iterator.hasPrevious();
    }

    @Override
    public boolean hasNextLine() {
        return iterator.hasNext();
    }

    @Override
    public String getPreviousLine() {
        if (!isPreviousValid) {
            if (hasPreviousLine()) {
                previousLine = iterator.previous();
                iterator.next();
            } else {
                previousLine = null;
            }
            isPreviousValid = true;
        }
        return previousLine;
    }

    @Override
    public String getCurrentLine() {
        if (!isCurrentValid) {
            currentLine = iterator.next();
            iterator.previous();
            isCurrentValid = true;
        }
        return currentLine;
    }

    @Override
    public String getNextLine() {
        if (!isNextValid) {
            iterator.next();
            if (hasNextLine()) {
                nextLine = iterator.next();
                iterator.previous();
            } else {
                nextLine = null;
            }
            iterator.previous();
            isNextValid = true;
        }
        return nextLine;
    }

    @Override
    public void moveForward() {
        previousLine = getCurrentLine();
        currentLine = getNextLine();

        isPreviousValid = true;
        isCurrentValid = true;
        isNextValid = false;

        iterator.next();
    }

    @Override
    public void moveBackward() {
        nextLine = getCurrentLine();
        currentLine = getPreviousLine();

        isNextValid = true;
        isCurrentValid = true;
        isPreviousValid = false;

        iterator.previous();
    }
}
