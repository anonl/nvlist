package nl.weeaboo.vn.text;

import java.io.Serializable;

import nl.weeaboo.styledtext.layout.ITextLayout;

public interface ITextRenderState extends IMultiLineText, Serializable {

    final float ALL_GLYPHS_VISIBLE = 999999;

	/**
	 * Returns the tags of the TextStyle of the characters at the specified location or <code>null</code> if no hit.
	 */
    int[] getHitTags(float cx, float cy);

    void increaseVisibleText(float textSpeed);

    /**
     * @see #setVisibleText(float)
     */
    float getVisibleText();

    /**
     * Sets the number of visible glyphs relative to the current start line.
     *
     * @see #setVisibleText(int, float)
     */
    void setVisibleText(float visibleGlyphs);

    void setVisibleText(int startLine, float visibleGlyphs);

    ITextLayout getVisibleLayout();

}
