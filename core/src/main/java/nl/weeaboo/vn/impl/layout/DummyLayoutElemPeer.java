package nl.weeaboo.vn.impl.layout;

import nl.weeaboo.common.Dim2D;
import nl.weeaboo.common.Rect2D;

public class DummyLayoutElemPeer implements ILayoutElemPeer {

    private Dim2D size = Dim2D.of(10, 10);

    @Override
    public boolean isVisible() {
        return true;
    }

    @Override
    public void setLayoutBounds(Rect2D rect) {
        size = Dim2D.of(rect.w, rect.h);
    }

    @Override
    public double getWidth() {
        return size.w;
    }

    @Override
    public double getHeight() {
        return size.h;
    }

}
