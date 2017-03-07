package nl.weeaboo.vn.impl.scene;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import nl.weeaboo.common.Area2D;
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
    private double scrollWheelAccel = 30;

    @Override
    protected ILayoutGroup createLayoutAdapter() {
        return new NullLayout(this);
    }

    @Override
    protected void onTick() {
        super.onTick();

        limitScroll();
    }

    @Override
    public void handleInput(Matrix parentTransform, IInput input) {
        super.handleInput(parentTransform, input);

        Vec2 pointerPos = input.getPointerPos(parentTransform);
        boolean containsPointer = contains(pointerPos.x, pointerPos.y);

        // Mouse scroll wheel handling
        if (containsPointer) {
            scroll(0, scrollWheelAccel * input.getPointerScroll());
        }

        // Mouse/touch draw handling
        if (dragging) {
            if (!input.isPressed(VKey.MOUSE_LEFT, true)) {
                dragging = false;
            }

            if (lastPointerPos != null) {
                scroll(lastPointerPos.x - pointerPos.x, lastPointerPos.y - pointerPos.y);
            }
        } else {
            if (containsPointer && input.consumePress(VKey.MOUSE_LEFT)) {
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

    @Override
    public double getScrollX() {
        return scroll.x;
    }

    @Override
    public double getScrollY() {
        return scroll.y;
    }

    @Override
    public void scroll(double dx, double dy) {
        setScroll(getScrollX() + dx, getScrollY() + dy);
    }

    @Override
    public void setScroll(double x, double y) {
        Checks.checkRange(x, "x");
        Checks.checkRange(y, "y");

        scroll.x = x;
        scroll.y = y;

        limitScroll();
    }

    /** Clamps the scroll position to within the scroll bounds. */
    private void limitScroll() {
        Area2D scrollBounds = getScrollBounds();

        scroll.x = Math.max(scrollBounds.x, Math.min(scroll.x, scrollBounds.x + scrollBounds.w));
        scroll.y = Math.max(scrollBounds.y, Math.min(scroll.y, scrollBounds.y + scrollBounds.h));
    }

    /** The rectangle within which the scroll offsets must remain. */
    private Area2D getScrollBounds() {
        List<Rect2D> childBounds = Lists.newArrayList();
        for (IVisualElement child : getChildren()) {
            childBounds.add(child.getVisualBounds());
        }
        Rect2D contentBounds = Rect2D.combine(childBounds.toArray(new Rect2D[childBounds.size()]));

        return Area2D.of(contentBounds.x, contentBounds.y, contentBounds.w - getWidth(), contentBounds.h - getHeight());
    }

}
