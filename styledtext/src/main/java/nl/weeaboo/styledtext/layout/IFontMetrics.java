package nl.weeaboo.styledtext.layout;

import nl.weeaboo.styledtext.TextStyle;

public interface IFontMetrics {

    float getSpaceWidth();

    float getLineHeight();

    ILayoutElement layoutText(CharSequence str, TextStyle style, int bidiLevel, LayoutParameters params);

    ILayoutElement layoutSpacing(CharSequence text, TextStyle style, LayoutParameters params);

}
