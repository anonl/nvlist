package nl.weeaboo.vn.impl.scene;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.input.IInput;
import nl.weeaboo.vn.layout.ILayoutGroup;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IAxisAlignedContainer;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IVisualElement;
import nl.weeaboo.vn.scene.IVisualGroup;

public abstract class AxisAlignedContainer extends VisualElement implements IAxisAlignedContainer {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private final BoundsHelper boundsHelper = new BoundsHelper();

    private final ChildCollection children;

    protected AxisAlignedContainer() {
        children = new ChildCollection(this);
        addSignalHandler(0, children);
    }

    @Override
    public IAxisAlignedContainer getParent() {
        return (ILayer)super.getParent();
    }

    @Override
    public void setParent(IVisualGroup parent) {
        Checks.checkArgument(parent == null || parent instanceof IAxisAlignedContainer,
                "AxisAlignedContainers may only have other AxisAlignedContainers as parent, was: " + parent);

        super.setParent(parent);
    }

    @Override
    public boolean contains(double cx, double cy) {
        return getBounds().contains(cx, cy);
    }

    @Override
    public boolean contains(IVisualElement elem) {
        return children.contains(elem);
    }

    @Override
    public void handleInput(Matrix parentTransform, IInput input) {
        Matrix inputTransform = getChildInputTransform(parentTransform);
        for (IVisualElement elem : SceneUtil.getChildren(this, VisualOrdering.FRONT_TO_BACK)) {
            elem.handleInput(inputTransform, input);
        }
    }

    /**
     * @return The relative transform from pointer coordinates for this container, to pointer coordinates for the
     *         container's contents.
     */
    protected Matrix getChildInputTransform(Matrix parentTransform) {
        // TODO: Don't multiply a bunch of matrices. Make some kind of TransformedInput to lazily compute if needed
        return parentTransform.translatedCopy(-getX(), -getY());
    }

    @Override
    public final double getX() {
        return boundsHelper.getX();
    }

    @Override
    public final double getY() {
        return boundsHelper.getY();
    }

    @Override
    public final double getWidth() {
        return boundsHelper.getWidth();
    }

    @Override
    public final double getHeight() {
        return boundsHelper.getHeight();
    }

    @Override
    public Rect2D getBounds() {
        return boundsHelper.getBounds();
    }

    @Override
    public Rect2D getVisualBounds() {
        return getBounds();
    }

    @Override
    public final void setX(double x) {
        setPos(x, getY());
    }

    @Override
    public final void setY(double y) {
        setPos(getX(), y);
    }

    @Override
    public final void setWidth(double w) {
        setSize(w, getHeight());
    }

    @Override
    public final void setHeight(double h) {
        setSize(getWidth(), h);
    }

    @Override
    public final void translate(double dx, double dy) {
        setPos(getX() + dx, getY() + dy);
    }

    @Override
    public void setPos(double x, double y) {
        boundsHelper.setPos(x, y);
    }

    @Override
    public void setSize(double w, double h) {
        boundsHelper.setSize(w, h);
    }

    @Override
    public void setBounds(double x, double y, double w, double h) {
        setPos(x, y);
        setSize(w, h);
    }

    protected void add(IVisualElement child) {
        children.add(child);
    }

    @Override
    public void remove(IVisualElement child) {
        children.remove(child);
    }

    @Override
    public void draw(IDrawBuffer drawBuffer) {
        if (!isVisible()) {
            return;
        }

        for (IVisualElement elem : getChildren()) {
            elem.draw(drawBuffer);
        }
    }

    @Override
    public Iterable<? extends IVisualElement> getChildren() {
        return children.getSnapshot();
    }

    @Override
    protected abstract ILayoutGroup createLayoutAdapter();

}
