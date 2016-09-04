package nl.weeaboo.vn.image.impl.desc;

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

        this.area = area;
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
