package nl.weeaboo.vn.impl.scene;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.impl.layout.ILayoutElemPeer;
import nl.weeaboo.vn.impl.layout.NullLayout;
import nl.weeaboo.vn.layout.ILayoutGroup;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IViewport;

public class Viewport extends AxisAlignedContainer implements IViewport, ILayoutElemPeer {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    @Override
    protected ILayoutGroup createLayoutAdapter() {
        return new NullLayout(this);
    }

    @Override
    public void draw(IDrawBuffer drawBuffer) {
        IDrawBuffer layerBuffer = drawBuffer.subLayerBuffer(getZ(), getBounds());

        super.draw(layerBuffer);
    }

    @Override
    public void setLayoutBounds(Rect2D rect) {
        setBounds(rect.x, rect.y, rect.w, rect.h);
    }

}
