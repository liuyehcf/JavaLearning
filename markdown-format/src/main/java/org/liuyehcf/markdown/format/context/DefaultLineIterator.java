package org.liuyehcf.markdown.format.context;

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Created by HCF on 2018/1/14.
 */
public class DefaultLineIterator implements LineIterator {

    private ListIterator<LineElement> iterator;

    private boolean isPreviousValid;

    private boolean isCurrentValid;

    private boolean isNextValid;

    private LineElement previousLine;

    private LineElement currentLine;

    private LineElement nextLine;

    public DefaultLineIterator(LinkedList<LineElement> lines) {
        iterator = lines.listIterator();
    }

    @Override
    public boolean isNotFinish() {
        return iterator.hasNext();
    }

    @Override
    public LineElement getPreviousLineElement() {
        if (!isPreviousValid) {
            if (iterator.hasPrevious()) {
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
    public LineElement getCurrentLineElement() {
        if (!isCurrentValid) {
            currentLine = iterator.next();
            iterator.previous();
            isCurrentValid = true;
        }
        return currentLine;
    }

    @Override
    public LineElement getNextLineElement() {
        if (!isNextValid) {
            iterator.next();
            if (isNotFinish()) {
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
    public void insertPrevious(LineElement lineElement) {
        iterator.add(lineElement);

        setInValid();
    }

    @Override
    public void insertNext(LineElement lineElement) {
        iterator.next();

        iterator.add(lineElement);

        iterator.previous();
        iterator.previous();

        setInValid();
    }

    @Override
    public void removePreviousLine() {
        iterator.previous();
        iterator.remove();

        setInValid();
    }

    @Override
    public void moveForward() {
        iterator.next();

        setInValid();
    }

    @Override
    public void moveBackward() {
        iterator.previous();

        setInValid();
    }

    private void setInValid() {
        isNextValid = false;
        isCurrentValid = false;
        isPreviousValid = false;
    }
}
