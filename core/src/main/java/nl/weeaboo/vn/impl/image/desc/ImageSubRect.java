package nl.weeaboo.vn.impl.image.desc;

import nl.weeaboo.common.Area;
import nl.weeaboo.common.Checks;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.vn.image.desc.IImageSubRect;

public final class ImageSubRect implements IImageSubRect {

    private static final long serialVersionUID = 1L;

    private final String id;
    private final Area area;

    public ImageSubRect(String id, Area area) {
        this.id = Checks.checkNotNull(id);

        Checks.checkArgument(area.w != 0 && area.h != 0,
                "Sub-rect dimensions must be non-zero, was: " + area);
        this.area = area;
    }

    /**
     * Instantiates a new {@link ImageSubRect} initialized with the given sub-rect.
     */
    public static ImageSubRect from(IImageSubRect subRect) {
        if (subRect instanceof ImageSubRect) {
            return (ImageSubRect)subRect;
        }
        return new ImageSubRect(subRect.getId(), subRect.getArea());
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Area getArea() {
        return area;
    }

    @Override
    public String toString() {
        return StringUtil.formatRoot("%s: (%d, %d, %d, %d)", getId(), area.x, area.y, area.w, area.h);
    }

}
