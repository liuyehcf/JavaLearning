package org.liuyehcf.format.markdown.processor.impl;

import org.liuyehcf.format.markdown.context.FormatContext;
import org.liuyehcf.format.markdown.context.LineElement;
import org.liuyehcf.format.markdown.processor.AbstractLineProcessor;

import java.util.Arrays;
import java.util.ListIterator;

/**
 * Created by t-chehe on 7/5/2017.
 */
public class IndexProcessor extends AbstractLineProcessor {

    private boolean indentation;//是否需要缩进

    public IndexProcessor(boolean indentation) {
        this.indentation = indentation;
    }

    private static final String[] tabStringAr = {
            "",
            "&emsp;",
            "&emsp;&emsp;&emsp;",
            "&emsp;&emsp;&emsp;&emsp;&emsp;",
            "&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;",
            "&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;",
    };

    private String getTabString(int level) {
        return indentation ? tabStringAr[level] : "";
    }

    //1~6级标题
    private int[] indexes = new int[6];


    private void freshIndex(int level) {
        if (level <= 4) {
            indexes[5] = 0;
        }
        if (level <= 3) {
            indexes[4] = 0;
        }
        if (level <= 2) {
            indexes[3] = 0;
        }
        if (level <= 1) {
            indexes[2] = 0;
        }
        if (level <= 0) {
            indexes[1] = 0;
        }
    }

    @Override
    public void process(FormatContext context, ListIterator<LineElement> lineContentListIterator) {

        getContextLineElement(lineContentListIterator);

        // escape code line
        if (curContentIsCode()) return;

        String content = getContentOfCurLineElement();

        if (content.startsWith("# ")) {
            checkBlank(lineContentListIterator);
            indexes[0]++;
            freshIndex(0);
            content = removeIndex(content, 0);
            content = "# "
                    + getTabString(0)
                    + indexes[0]
                    + " "
                    + content.substring(2);
        } else if (content.startsWith("## ")) {
            checkBlank(lineContentListIterator);
            indexes[1]++;
            freshIndex(1);
            content = removeIndex(content, 1);
            content = "## "
                    + getTabString(1)
                    + indexes[0]
                    + "."
                    + indexes[1]
                    + " "
                    + content.substring(3);
        } else if (content.startsWith("### ")) {
            checkBlank(lineContentListIterator);
            indexes[2]++;
            freshIndex(2);
            content = removeIndex(content, 2);
            content = "### "
                    + getTabString(2)
                    + indexes[0]
                    + "."
                    + indexes[1]
                    + "."
                    + indexes[2]
                    + " "
                    + content.substring(4);
        } else if (content.startsWith("#### ")) {
            checkBlank(lineContentListIterator);
            indexes[3]++;
            freshIndex(3);
            content = removeIndex(content, 3);
            content = "#### "
                    + getTabString(3)
                    + indexes[0]
                    + "."
                    + indexes[1]
                    + "."
                    + indexes[2]
                    + "."
                    + indexes[3]
                    + " "
                    + content.substring(5);
        } else if (content.startsWith("##### ")) {
            checkBlank(lineContentListIterator);
            indexes[4]++;
            freshIndex(4);
            content = removeIndex(content, 4);
            content = "##### "
                    + getTabString(4)
                    + indexes[0]
                    + "."
                    + indexes[1]
                    + "."
                    + indexes[2]
                    + "."
                    + indexes[3]
                    + "."
                    + indexes[4]
                    + " "
                    + content.substring(6);
        } else if (content.startsWith("###### ")) {
            checkBlank(lineContentListIterator);
            indexes[5]++;
            freshIndex(5);
            content = removeIndex(content, 5);
            content = "###### "
                    + getTabString(5)
                    + indexes[0]
                    + "."
                    + indexes[1]
                    + "."
                    + indexes[2]
                    + "."
                    + indexes[3]
                    + "."
                    + indexes[4]
                    + "."
                    + indexes[5]
                    + " "
                    + content.substring(7);
        }

        setContentOfCurLineElement(content);
    }

    @Override
    public void reset() {
        Arrays.fill(indexes, 0);
    }

    private String removeIndex(String content, int level) {
        switch (level) {
            case 0:
                content = content.replaceFirst("^# +(&emsp;)*[0-9]+((\\.[0-9]+)+)? ", "# ");
                break;
            case 1:
                content = content.replaceFirst("^## +(&emsp;)*[0-9]+((\\.[0-9]+)+)? ", "## ");
                break;
            case 2:
                content = content.replaceFirst("^### +(&emsp;)*[0-9]+((\\.[0-9]+)+)? ", "### ");
                break;
            case 3:
                content = content.replaceFirst("^#### +(&emsp;)*[0-9]+((\\.[0-9]+)+)? ", "#### ");
                break;
            case 4:
                content = content.replaceFirst("^##### +(&emsp;)*[0-9]+((\\.[0-9]+)+)? ", "##### ");
                break;
            case 5:
                content = content.replaceFirst("^###### +(&emsp;)*[0-9]+((\\.[0-9]+)+)? ", "###### ");
                break;
            default:
                throw new RuntimeException();
        }
        return content;
    }

    private void checkBlank(ListIterator<LineElement> lineContentListIterator) {
        if (!preContentIsEmpty()) {
            addPrevLineElement(lineContentListIterator, "", false);
        }

        if (!nextContentIsEmpty()) {
            addNextLineElement(lineContentListIterator, "", false);
        }
    }
}
