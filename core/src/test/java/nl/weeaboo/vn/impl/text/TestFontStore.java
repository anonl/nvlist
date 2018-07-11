package nl.weeaboo.vn.impl.text;

import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.styledtext.layout.IFontMetrics;
import nl.weeaboo.vn.text.ILoadingFontStore;

public class TestFontStore implements ILoadingFontStore {

    private boolean destroyed;

    @Override
    public IFontMetrics getFontMetrics(TextStyle style) {
        return new BasicFontMetrics(style.getFontSize(), style.getFontSize());
    }

    @Override
    public void destroy() {
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

}
