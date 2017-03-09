package nl.weeaboo.vn.impl.scene;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.vn.impl.layout.LayoutElem;
import nl.weeaboo.vn.layout.LayoutSize;
import nl.weeaboo.vn.layout.LayoutSizeType;

final class TextLayoutElem extends LayoutElem {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(TextLayoutElem.class);

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
            LayoutSize height = LayoutSize.of(textDrawable.calculateTextHeight(widthHint.value(0)));
            LOG.trace("Calculated text height={} given width={}", height, widthHint);
            return height;
        }

        throw new IllegalArgumentException("Unknown size type: " + type);
    }

}
