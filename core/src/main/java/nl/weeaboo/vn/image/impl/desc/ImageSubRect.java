package nl.weeaboo.vn.image.impl.desc;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.vn.image.desc.IImageSubRect;

public final class ImageSubRect implements IImageSubRect {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final Rect rect;

    public ImageSubRect(String id, Rect rect) {
        this.id = Checks.checkNotNull(id);

        Checks.checkArgument(rect.w > 0 && rect.h > 0,
                "Sub-rect dimensions must be positive, was: " + rect);
        this.rect = rect;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Rect getRect() {
        return rect;
    }

    @Override
    public String toString() {
        return StringUtil.formatRoot("%s: (%d, %d, %d, %d)", getId(), rect.x, rect.y, rect.w, rect.h);
    }

}
