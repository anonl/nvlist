package nl.weeaboo.styledtext.layout;

import nl.weeaboo.styledtext.ETextAlign;

public abstract class TextElement extends AbstractElement implements IGlyphSequence {

    private final ETextAlign align;

    public TextElement(ETextAlign align) {
        this.align = align;
    }

    public ETextAlign getAlign() {
        return align;
    }

    @Override
    public boolean isWhitespace() {
        return false;
    }

}
