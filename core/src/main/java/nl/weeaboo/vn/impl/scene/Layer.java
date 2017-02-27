package nl.weeaboo.vn.impl.scene;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.image.IScreenshotBuffer;
import nl.weeaboo.vn.impl.layout.ILayoutElemPeer;
import nl.weeaboo.vn.impl.layout.NullLayout;
import nl.weeaboo.vn.impl.render.ScreenshotBuffer;
import nl.weeaboo.vn.layout.ILayoutGroup;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IDrawable;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IVisualElement;
import nl.weeaboo.vn.scene.IVisualGroup;

public class Layer extends AxisAlignedContainer implements ILayer, ILayoutElemPeer {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(Layer.class);

    private final ScreenshotBuffer screenshotBuffer = new ScreenshotBuffer();

    protected Layer() {
    }

    public Layer(ILayer parent) {
        setParent(parent);

        if (parent != null) {
            setBounds(parent.getX(), parent.getY(), parent.getWidth(), parent.getHeight());
        }
    }

    /**
     * Adds a new sub-layer.
     *
     * @return The newly created sub-layer.
     */
    public Layer createSubLayer() {
        Layer subLayer = new Layer(this);
        add(subLayer);
        LOG.debug("Sub-layer created: {}, parent={}", subLayer, this);
        return subLayer;
    }

    @Override
    public ILayer getParent() {
        return (ILayer)super.getParent();
    }

    @Override
    public void setParent(IVisualGroup parent) {
        Checks.checkArgument(parent == null || parent instanceof ILayer,
                "Layers may only have other layers as parent, was: " + parent);

        super.setParent(parent);
    }

    @Override
    public void add(IVisualElement elem) {
        super.add(elem);
    }

    protected boolean isElementVisible(IVisualElement elem) {
        if (elem instanceof IDrawable) {
            IDrawable d = (IDrawable)elem;
            if (!d.isVisible(.001)) {
                // Drawable is not visible
                return false;
            }
            if (d.isClipEnabled()) {
                // If clipping is off, bounds checks can be skipped
                return true;
            }
        } else {
            if (!elem.isVisible()) {
                // Visual element is not visible
                return false;
            }
        }

        // Check if the drawable's bounds lie within the layer bounds.
        // Remember: Drawable coordinates are relative to the coordinates of their parent layer.
        final Rect2D r = getBounds();
        return elem.getVisualBounds().intersects(0, 0, r.w, r.h);
    }

    @Override
    public void draw(IDrawBuffer mainBuffer) {
        if (!isVisible()) {
            return;
        }

        IDrawBuffer drawBuffer = mainBuffer.subLayerBuffer(getZ(), getBounds(), 0, 0);

        // Render children (except sub-layers)
        for (IVisualElement child : getChildren()) {
            if (child.isDestroyed()) {
                continue;
            }

            if (isElementVisible(child)) {
                child.draw(drawBuffer);
            }
        }

        // Add screenshot render commands to the end of the list
        screenshotBuffer.flush(drawBuffer);
    }

    @Override
    public String toString() {
        return String.format("Layer(%08x)", hashCode());
    }

    @Override
    public boolean containsLayer(ILayer layer) {
        if (layer == null) {
            return false;
        }

        for (ILayer sub : getSubLayers()) {
            if (sub == layer || sub.containsLayer(layer)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterable<? extends Layer> getSubLayers() {
        return Iterables.unmodifiableIterable(Iterables.filter(getChildren(), Layer.class));
    }

    @Override
    public IScreenshotBuffer getScreenshotBuffer() {
        return screenshotBuffer;
    }

    @Override
    protected ILayoutGroup createLayoutAdapter() {
        return new NullLayout(this);
    }

    @Override
    public void setLayoutBounds(Rect2D rect) {
        setBounds(rect.x, rect.y, rect.w, rect.h);
    }

}
