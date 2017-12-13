package org.liuyehcf.format.markdown.processor;

import org.liuyehcf.format.markdown.context.LineElement;
import org.liuyehcf.format.markdown.utils.FormatUtils;

import java.util.ListIterator;

/**
 * Created by HCF on 2017/8/3.
 */
public abstract class AbstractLineProcessor implements LineProcessor {

    private LineElement curLineElement;

    private LineElement preLineElement;

    private LineElement nextLineElement;

    protected void getContextLineElement(ListIterator<LineElement> lineContentListIterator) {
        if (!lineContentListIterator.hasNext()) throw new RuntimeException();

        setCurLineElement(lineContentListIterator);

        setPreLineElement(lineContentListIterator);

        setNextLineElement(lineContentListIterator);
    }

    protected void addPrevLineElement(ListIterator<LineElement> lineContentListIterator, String content, boolean isCode) {
        lineContentListIterator.add(new LineElement(content, isCode));
        setPreLineElement(lineContentListIterator);
    }

    protected void removePrevLineElement(ListIterator<LineElement> lineContentListIterator) {
        if (!lineContentListIterator.hasPrevious()) throw new RuntimeException();
        lineContentListIterator.previous();
        lineContentListIterator.remove();
        setPreLineElement(lineContentListIterator);
    }

    protected void addNextLineElement(ListIterator<LineElement> lineContentListIterator, String content, boolean isCode) {
        lineContentListIterator.next();
        lineContentListIterator.add(new LineElement(content, isCode));
        lineContentListIterator.previous();
        lineContentListIterator.previous();
        setNextLineElement(lineContentListIterator);
    }

    protected String getContentOfCurLineElement() {
        return curLineElement.getContent();
    }

    protected String getContentOfPreLineElement() {
        return preLineElement.getContent();
    }

    protected String getContentOfNextLineElement() {
        return nextLineElement.getContent();
    }

    protected void setContentOfCurLineElement(String content) {
        curLineElement.setContent(content);
    }

    protected boolean curContentIsCode() {
        return curLineElement.isCode();
    }

    protected boolean curContentIsEmpty() {
        return curLineElement != null
                && FormatUtils.isEmptyLine(getContentOfCurLineElement());
    }

    protected boolean preContentIsEmpty() {
        return preLineElement != null
                && FormatUtils.isEmptyLine(getContentOfPreLineElement());
    }

    protected boolean nextContentIsEmpty() {
        return nextLineElement != null
                && FormatUtils.isEmptyLine(getContentOfNextLineElement());
    }

    protected boolean hasPreLineElement() {
        return preLineElement != null;
    }

    protected boolean hasNextLineElement() {
        return nextLineElement != null;
    }

    private void setCurLineElement(ListIterator<LineElement> lineContentListIterator) {
        curLineElement = lineContentListIterator.next();
        lineContentListIterator.previous();
    }

    private void setPreLineElement(ListIterator<LineElement> lineContentListIterator) {
        if (lineContentListIterator.hasPrevious()) {
            preLineElement = lineContentListIterator.previous();
            lineContentListIterator.next();
        } else {
            preLineElement = null;
        }
    }

    private void setNextLineElement(ListIterator<LineElement> lineContentListIterator) {
        lineContentListIterator.next();
        if (lineContentListIterator.hasNext()) {
            nextLineElement = lineContentListIterator.next();
            lineContentListIterator.previous();
        } else {
            nextLineElement = null;
        }
        lineContentListIterator.previous();
    }


}
