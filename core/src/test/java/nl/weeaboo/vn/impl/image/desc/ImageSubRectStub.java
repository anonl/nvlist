package nl.weeaboo.vn.impl.image.desc;

import nl.weeaboo.common.Area;
import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.image.desc.IImageSubRect;

final class ImageSubRectStub implements IImageSubRect {

    private static final long serialVersionUID = 1L;

    private final String id;

    public ImageSubRectStub() {
        this("test");
    }

    public ImageSubRectStub(String id) {
        this.id = Checks.checkNotNull(id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Area getArea() {
        return Area.of(0, 0, 1, 1);
    }

}
