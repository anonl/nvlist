package nl.weeaboo.vn.text;

import java.io.Serializable;

import nl.weeaboo.styledtext.layout.ITextLayout;

public interface ITextRenderState extends IMultiLineText, Serializable {

    final double ALL_GLYPHS_VISIBLE = 999999;

    /**
     * Returns the tags of the TextStyle of the characters at the specified location or <code>null</code> if no hit.
     */
    int[] getHitTags(double cx, double cy);

    /**
     * @see #setVisibleText(double)
     */
    double getVisibleText();

    /**
     * @return The number of glyphs between the start and end lines.
     */
    int getMaxVisibleText();

    /**
     * @return {@code true} if the final line is fully visible.
     */
    boolean isFinalLineFullyVisible();

    /**
     * Increases the number of visible glyphs within the current line range.
     *
     * @param textSpeed The number of additional glyphs to show.
     * @see #getStartLine()
     * @see #getEndLine()
     * @see #setVisibleText(double)
     */
    void increaseVisibleText(double textSpeed);

    /**
     * Sets the number of visible glyphs relative to the current start line.
     *
     * @see #setVisibleText(int, double)
     */
    void setVisibleText(double visibleGlyphs);

    /**
     * Changes the starting line, then sets the number of visible glyphs relative to that start line.
     */
    void setVisibleText(int startLine, double visibleGlyphs);

    /**
     * Returns a text layout for the current line range.
     * @see #getStartLine()
     * @see #getEndLine()
     */
    ITextLayout getVisibleLayout();

}
