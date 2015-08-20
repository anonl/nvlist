package nl.weeaboo.vn.text;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.vn.core.IRenderable;

public interface ITextPart extends IRenderable {

    TextStyle DEFAULT_STYLE = TextStyle.fromString("fontSize=32|align=center");

    /**
     * @see #setText(StyledText)
     */
    StyledText getText();

    /**
     * @see #setDefaultStyle(TextStyle)
     */
    TextStyle getDefaultStyle();

    /**
     * @see #setText(StyledText)
     */
    void setText(String text); // Calls setText(StyledText)

    /**
     * Sets the text displayed on top of this button.
     */
    void setText(StyledText stext);

    /**
     * Sets the default text style to use as a base for the text displayed on top of this button.
     */
    void setDefaultStyle(TextStyle style);

    float increaseVisibleText(float textSpeed);

    boolean isRightToLeft();

    float getTextWidth();
    float getTextHeight();

    void setVisibleText(float visibleGlyphs);

    void setRightToLeft(boolean rtl);

}
