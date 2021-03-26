package nl.weeaboo.vn.impl.text;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.AbstractFontMetrics;
import nl.weeaboo.styledtext.layout.ILayoutElement;
import nl.weeaboo.styledtext.layout.LayoutParameters;

final class BasicFontMetrics extends AbstractFontMetrics {

    public BasicFontMetrics(float spaceWidth, float lineHeight) {
        super(spaceWidth, lineHeight);
    }

    @Override
    public ILayoutElement layoutText(CharSequence str, TextStyle style, int bidiLevel,
            LayoutParameters params) {

        return new BasicTextElement(str, style, bidiLevel);
    }

}