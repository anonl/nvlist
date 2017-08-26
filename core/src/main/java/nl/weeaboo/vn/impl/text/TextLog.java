package nl.weeaboo.vn.impl.text;

import java.util.Deque;

import javax.annotation.Nullable;

import com.google.common.collect.Iterables;
import com.google.common.collect.Queues;

import nl.weeaboo.common.Checks;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.text.ITextLog;

public class TextLog implements ITextLog {

    private static final long serialVersionUID = TextImpl.serialVersionUID;

    private final Deque<StyledText> pages = Queues.newArrayDeque();

    private int pageLimit;

    public TextLog() {
        pageLimit = Checks.checkRange(NovelPrefs.TEXTLOG_PAGE_LIMIT.getDefaultValue(), "pageLimit", 1);
    }

    @Override
    public void clear() {
        pages.clear();
    }

    private void limitPages(int maxPages) {
        while (!pages.isEmpty() && pages.size() >= maxPages) {
            removePage();
        }
    }

    protected void addPage(StyledText stext) {
        limitPages(pageLimit);
        pages.add(stext);
    }

    protected void removePage() {
        pages.remove();
    }

    @Override
    public @Nullable StyledText getPage(int offset) {
        int pos = pages.size() - 1 - offset;
        if (pos < 0 || pos >= pages.size()) {
            return null; // Page doesn't exist
        }
        return Iterables.get(pages, pos);
    }

    @Override
    public int getPageCount() {
        return pages.size();
    }

    @Override
    public int getPageLimit() {
        return pageLimit;
    }

    @Override
    public void setPageLimit(int numPages) {
        Checks.checkRange(numPages, "maxPages", 1);

        if (pageLimit != numPages) {
            pageLimit = numPages;

            limitPages(pageLimit);
        }
    }

    @Override
    public void setText(StyledText text) {
        if (getPageCount() == 0) {
            addPage(text);
        } else {
            StyledText current = getPage(0);
            if (current == null || current.length() == 0) {
                // Overwrite existing page if empty
                pages.removeLast();
            }
            addPage(text);
        }
    }

    @Override
    public void appendText(StyledText text) {
        if (getPageCount() == 0) {
            addPage(text);
        } else {
            StyledText current = pages.removeLast();
            if (current != null && current.length() > 0) {
                current = StyledText.concat(current, text);
            } else {
                current = text;
            }
            addPage(current);
        }
    }

}
