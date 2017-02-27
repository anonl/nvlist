package nl.weeaboo.vn.impl.scene;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.impl.layout.ILayoutElemPeer;
import nl.weeaboo.vn.impl.layout.NullLayout;
import nl.weeaboo.vn.input.IInput;
import nl.weeaboo.vn.input.VKey;
import nl.weeaboo.vn.layout.ILayoutGroup;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.math.Vec2;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IViewport;
import nl.weeaboo.vn.scene.IVisualElement;

public class Viewport extends AxisAlignedContainer implements IViewport, ILayoutElemPeer {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private final Vec2 scroll = new Vec2();

    private boolean dragging;
    private Vec2 lastPointerPos;

    @Override
    protected ILayoutGroup createLayoutAdapter() {
        return new NullLayout(this);
    }

    @Override
    public void handleInput(Matrix parentTransform, IInput input) {
        super.handleInput(parentTransform, input);

        Vec2 pointerPos = input.getPointerPos(parentTransform);

        if (dragging) {
            if (!input.isPressed(VKey.MOUSE_LEFT, true)) {
                dragging = false;
            }

            if (lastPointerPos != null) {
                scroll(lastPointerPos.x - pointerPos.x, lastPointerPos.y - pointerPos.y);
            }
        } else {
            if (contains(pointerPos.x, pointerPos.y) && input.consumePress(VKey.MOUSE_LEFT)) {
                dragging = true;
            }
        }

        lastPointerPos = pointerPos;
    }

    @Override
    protected Matrix getChildInputTransform(Matrix parentTransform) {
        // TODO: Don't multiply a bunch of matrices. Make some kind of TransformedInput to lazily compute if needed
        return parentTransform.translatedCopy(-getX() + scroll.x, -getY() + scroll.y);
    }

    @Override
    public void draw(IDrawBuffer drawBuffer) {
        // Render as if the viewport contains an embedded layer
        IDrawBuffer layerBuffer = drawBuffer.subLayerBuffer(getZ(), getBounds(), -getScrollX(), -getScrollY());

        super.draw(layerBuffer);
    }

    @Override
    public void setLayoutBounds(Rect2D rect) {
        setBounds(rect.x, rect.y, rect.w, rect.h);
    }

    @Override
    public void setContents(IVisualElement elem) {
        // Remove the existing content element (if any)
        for (IVisualElement child : ImmutableList.copyOf(getChildren())) {
            remove(child);
        }

        add(elem);
    }

    public double getScrollX() {
        return scroll.x;
    }

    public double getScrollY() {
        return scroll.y;
    }

    public void scroll(double dx, double dy) {
        setScroll(getScrollX() + dx, getScrollY() + dy);
    }

    private void setScroll(double x, double y) {
        Checks.checkRange(x, "x");
        Checks.checkRange(y, "y");

        scroll.x = x;
        scroll.y = y;
    }

}
