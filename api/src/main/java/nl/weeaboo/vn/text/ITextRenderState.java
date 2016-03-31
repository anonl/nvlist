package nl.weeaboo.vn.text;

import java.io.Serializable;

import nl.weeaboo.styledtext.layout.ITextLayout;

public interface ITextRenderState extends IMultiLineText, Serializable {

    final double ALL_GLYPHS_VISIBLE = 999999;

	/**
	 * Returns the tags of the TextStyle of the characters at the specified location or <code>null</code> if no hit.
	 */
    int[] getHitTags(double cx, double cy);

    void increaseVisibleText(double textSpeed);

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
     * Sets the number of visible glyphs relative to the current start line.
     *
     * @see #setVisibleText(int, double)
     */
    void setVisibleText(double visibleGlyphs);

    void setVisibleText(int startLine, double visibleGlyphs);

    ITextLayout getVisibleLayout();

}
