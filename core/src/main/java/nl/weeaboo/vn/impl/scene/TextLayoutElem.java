package nl.weeaboo.vn.impl.scene;

import nl.weeaboo.vn.impl.layout.LayoutElem;
import nl.weeaboo.vn.layout.LayoutSize;
import nl.weeaboo.vn.layout.LayoutSizeType;

final class TextLayoutElem extends LayoutElem {

    private static final long serialVersionUID = 1L;

    public TextLayoutElem(TextDrawable textDrawable) {
        super(textDrawable);
    }

    @Override
    protected TextDrawable getPeer() {
        return (TextDrawable)super.getPeer();
    }

    @Override
    public LayoutSize calculateLayoutHeight(LayoutSizeType type, LayoutSize widthHint) {
        TextDrawable textDrawable = getPeer();

        switch (type) {
        case MIN:
            return LayoutSize.ZERO;
        case PREF:
        case MAX:
            return LayoutSize.of(textDrawable.calculateTextHeight(widthHint.value(0)));
        }

        throw new IllegalArgumentException("Unknown size type: " + type);
    }

}
