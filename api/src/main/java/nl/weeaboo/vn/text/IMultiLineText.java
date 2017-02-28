package nl.weeaboo.vn.text;

import nl.weeaboo.common.Rect2D;

public interface IMultiLineText extends IText {

    /**
     * @return The index of the first visible line.
     */
    int getStartLine();

    /**
     * The visible lines range from {@code [start, end)}. Te end line is the first line index that is no
     * longer rendered.
     */
    int getEndLine();

    /**
     * @return The number of lines in the current text layout.
     */
    int getLineCount();

    /**
     * @return The glyph offset at which the requested line starts.
     */
    int getGlyphOffset(int line);

    /** Word-wrap width for this multi-line text */
    double getMaxWidth();

    /**
     * Maximum visible height for this multi-line text. Lines that lie (partially) outside the maximum visible
     * area will not be rendered.
     */
    double getMaxHeight();

    /**
     * Sets the maximum size; the visible rectangle in which the multi-line text is rendered.
     *
     * @see #getMaxWidth()
     * @see #getMaxHeight()
     */
    void setMaxSize(double w, double h);

    /**
     * @return The minimum bounding text width for the currently visible lines.
     */
    float getTextWidth();

    /**
     * @return The minimum bounding text height for the currently visible lines.
     * @see #getTextHeight(int, int)
     */
    float getTextHeight();

    /** Returns the minimum bounding text height for the requested line range. */
    float getTextHeight(int startLine, int endLine);

    /**
     * @return The bounding box of the text in the requested line, relative to the origin of the entire text layout (not
     *         just the visible part).
     */
    Rect2D getLineBounds(int lineIndex);

    /**
     * Calculates the text height given a set width.
     */
    double calculateTextHeight(double widthHint);

}
