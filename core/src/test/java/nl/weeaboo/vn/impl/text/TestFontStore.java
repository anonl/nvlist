package nl.weeaboo.vn.impl.text;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.IFontMetrics;
import nl.weeaboo.vn.text.ILoadingFontStore;

public class TestFontStore implements ILoadingFontStore {

    private static final long serialVersionUID = 1L;

    @Override
    public IFontMetrics getFontMetrics(TextStyle style) {
        return new BasicFontMetrics(style.getFontSize(), style.getFontSize());
    }

}
