package nl.weeaboo.vn.impl.text;

import nl.weeaboo.common.Checks;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.IFontMetrics;
import nl.weeaboo.vn.text.ILoadingFontStore;

public class FontStoreMock implements ILoadingFontStore {

    private static final long serialVersionUID = 1L;

    private TextStyle defaultStyle = new TextStyle(null, 32);

    @Override
    public IFontMetrics getFontMetrics(TextStyle style) {
        return new BasicFontMetrics(style.getFontSize(), style.getFontSize());
    }

    @Override
    public TextStyle getDefaultStyle() {
        return defaultStyle;
    }

    @Override
    public void setDefaultStyle(TextStyle style) {
        defaultStyle = Checks.checkNotNull(style);
    }

}
