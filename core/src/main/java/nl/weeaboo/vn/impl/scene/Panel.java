package nl.weeaboo.vn.impl.scene;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.IEventListener;
import nl.weeaboo.vn.image.INinePatch;
import nl.weeaboo.vn.impl.image.NinePatchRenderer;
import nl.weeaboo.vn.layout.ILayoutGroup;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IPanel;
import nl.weeaboo.vn.scene.IVisualElement;

public abstract class Panel extends Transformable implements IPanel {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private final NinePatchRenderer renderer = new NinePatchRenderer();
    private final ChildCollection children;

    private transient IEventListener rendererListener;

    public Panel() {
        children = new ChildCollection(this);
        addSignalHandler(0, children);

        initTransients();
    }

    private void initTransients() {
        rendererListener = new IEventListener() {
            @Override
            public void onEvent() {
                invalidateTransform();
            }
        };

        renderer.onAttached(rendererListener);
    }

    protected abstract ILayoutGroup getLayout();

    protected void validateLayout() {
        ILayoutGroup layout = getLayout();
        if (!layout.isLayoutValid()) {
            layout.layout();
        }
    }

    @Override
    protected void onDestroyed() {
        super.onDestroyed();

        renderer.onDetached(rendererListener);
    }

    @Override
    public void onTick() {
        super.onTick();

        renderer.update();
    }

    @Override
    public void draw(IDrawBuffer drawBuffer) {
        super.draw(drawBuffer);

        validateLayout();

        renderer.render(drawBuffer, this, 0, 0);

        for (IVisualElement elem : getChildren()) {
            elem.draw(drawBuffer);
        }
    }

    @Override
    protected double getUnscaledWidth() {
        return renderer.getNativeWidth();
    }

    @Override
    protected double getUnscaledHeight() {
        return renderer.getNativeHeight();
    }

    @Override
    public void setUnscaledSize(double w, double h) {
        renderer.setSize(w, h);

        invalidateTransform();
    }

    @Override
    public void setBackground(INinePatch ninePatch) {
        renderer.set(ninePatch);
    }

    /**
     * Subclasses should call this method to add child elements to the panel. This method doesn't add the
     * element to the layout!
     */
    protected void add(IVisualElement child) {
        children.add(child);
    }

    /**
     * Removes an element from the panel and its layout.
     */
    protected void remove(IVisualElement child) {
        getLayout().remove(child.getLayoutAdapter());
        children.remove(child);
    }

    @Override
    public boolean contains(IVisualElement elem) {
        return children.contains(elem);
    }

    @Override
    public Iterable<? extends IVisualElement> getChildren() {
        return children.getSnapshot();
    }

    @Override
    public void setLayoutBounds(Rect2D rect) {
        super.setLayoutBounds(rect);

        getLayout().setLayoutBounds(rect);
    }

}
