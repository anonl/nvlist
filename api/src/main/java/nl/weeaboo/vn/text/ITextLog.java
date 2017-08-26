package nl.weeaboo.vn.text;

import java.io.Serializable;

import javax.annotation.Nullable;

import nl.weeaboo.styledtext.StyledText;

public interface ITextLog extends Serializable {

    /**
     * Clears the contents of the text log.
     */
    void clear();

    /**
     * Returns the number of currently used pages.
     */
    int getPageCount();

    /**
     * Returns the contents of the indicated page, or {@code null} if the specified page doesn't exist.
     */
    @Nullable StyledText getPage(int offset);

    /**
     * Returns the maximum number of pages that this text log will store. Attempting to store more pages will discard
     * the oldest page.
     */
    int getPageLimit();

    /**
     * Changes the page limit.
     * @see #getPageLimit()
     */
    void setPageLimit(int numPages);

    /**
     * Creates a new page and fills it with the supplied text.
     */
    void setText(StyledText text);

    /**
     * Appends additional text to the current page.
     */
    void appendText(StyledText text);

}
