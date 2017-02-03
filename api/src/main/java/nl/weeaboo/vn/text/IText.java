package nl.weeaboo.vn.text;

import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;

public interface IText {

    TextStyle DEFAULT_STYLE = new TextStyle(null, 32);

    /**
     * @see #setText(StyledText)
     */
    StyledText getText();

    /**
     * @see #setText(StyledText)
     */
    void setText(String text); // Calls setText(StyledText)

    /** Sets the text that should be rendered. */
    void setText(StyledText stext);

    /**
     * @see #setDefaultStyle(TextStyle)
     */
    TextStyle getDefaultStyle();

    /** Sets the default text style for the rendered text. */
    void setDefaultStyle(TextStyle style);

    /** Convenience method for changing the default style by extending it with the given value */
    void extendDefaultStyle(TextStyle ts);

    /**
     * @return {@code true} if the default direction of this text is right-to-left.
     */
    boolean isRightToLeft();

    /** Sets the default direction of this text */
    void setRightToLeft(boolean rtl);

}
