package nl.weeaboo.vn.core.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.entity.Entity;
import nl.weeaboo.entity.EntityStream;
import nl.weeaboo.entity.Scene;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.core.IDrawablePart;
import nl.weeaboo.vn.core.ILayer;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.image.IScreenshotBuffer;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.render.impl.ScreenshotBuffer;

@CustomSerializable
public class Layer implements ILayer, ILayerHolder {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(Layer.class);

    private final ILayerHolder parent;
    private final Scene scene;
    private final BasicPartRegistry partRegistry;

    private final BoundsHelper boundsHelper = new BoundsHelper();
    private final ScreenshotBuffer screenshotBuffer = new ScreenshotBuffer();
    private final List<Layer> subLayers = new ArrayList<Layer>();

    private IRenderEnv renderEnv;
    private short z;
    private boolean visible = true;
    private boolean destroyed;

    private transient EntityStream renderStream;
    private transient boolean changed;

    public Layer(ILayerHolder parent, Scene s, BasicPartRegistry pr) {
        Checks.checkNotNull(parent, "parent");

        this.parent = parent;
        this.scene = Checks.checkNotNull(s);
        this.partRegistry = Checks.checkNotNull(pr);

        initTransients();
    }

    // Functions
    private void initTransients() {
        changed = true;

        renderStream = scene.joinStream(new DrawableZStreamDef(this, partRegistry.drawable, 1));

        boundsHelper.setChangeListener(new IChangeListener() {
            @Override
            public void onChanged() {
                markChanged();
            }
        });
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        initTransients();
    }

    protected final void markChanged() {
        changed = true;
    }

    @Override
    public final boolean consumeChanged() {
        boolean result = changed;
        changed = false;
        return result;
    }

    @Override
    public final void destroy() {
        if (!destroyed) {
            destroyed = true;

            destroySubLayers();

            LOG.debug("Layer destroyed: {}", this);
            parent.onSubLayerDestroyed(this);
        }
    }

    private void destroySubLayers() {
        List<Layer> layersCopy = ImmutableList.copyOf(subLayers);
        for (ILayer subLayer : layersCopy) {
            subLayer.destroy();
        }
    }

    /** Creates a new entity, but does not add it to this layer. */
    @Override
    public Entity createEntity() {
        return scene.createEntity();
    }

    public Layer createSubLayer() {
        Layer result = new Layer(this, scene, partRegistry);
        subLayers.add(result);
        LOG.debug("Sub-layer created: {}, parent={}", result, this);
        return result;
    }

    @Override
    public void add(Entity e) {
        e.moveToScene(scene);
        DrawablePart.moveToLayer((DrawablePart)e.getPart(partRegistry.drawable), this);
    }

    @Override
    public boolean contains(Entity e) {
        return renderStream.contains(e);
    }

    @Override
    public Iterable<Entity> getContents() {
        return renderStream;
    }

    protected void invalidateStreams() {
        renderStream.invalidate();
    }

    @Override
    public void onSubLayerDestroyed(ILayer layer) {
        subLayers.remove(layer);
    }

    protected boolean isDrawableVisible(IDrawablePart dp) {
        if (!dp.isVisible(.001)) {
            // Drawable is not visible
            return false;
        }

        if (!dp.isClipEnabled()) {
            return true;
        }

        // Check if the drawable's bounds lie within the layer bounds.
        // Remember: Drawable coordinates are relative to the coordinates of their parent layer.
        final Rect2D r = getBounds();
        return dp.getBounds().intersects(0, 0, r.w, r.h);
    }

    public void draw(IDrawBuffer buffer) {
        draw(buffer, buffer.reserveLayerIds(1));
    }

    private void draw(IDrawBuffer buffer, int layerId) {
        if (!isVisible()) {
            return;
        }

        buffer.startLayer(layerId, this);

        // Render drawables (insert layer render commands for the layers)
        for (Entity e : renderStream) {
            if (e.isDestroyed()) {
                continue;
            }

            IDrawablePart dp = e.getPart(partRegistry.drawable);
            if (isDrawableVisible(dp)) {
                buffer.draw(e);
            }
        }

        // Add render commands for our sub-layers
        int baseSubLayerId = buffer.reserveLayerIds(subLayers.size());
        for (int n = 0; n < subLayers.size(); n++) {
            Layer subLayer = subLayers.get(n);
            if (!subLayer.isDestroyed() && subLayer.isVisible()) {
                buffer.drawLayer(baseSubLayerId + n, subLayer);
            }
        }

        // Add screenshot render commands to the end of the list
        screenshotBuffer.flush(buffer);

        // Recursively render the contents of our sub-layers
        for (int n = 0; n < subLayers.size(); n++) {
            Layer subLayer = subLayers.get(n);
            if (!subLayer.isDestroyed() && subLayer.isVisible()) {
                subLayer.draw(buffer, baseSubLayerId + n);
            }
        }
    }

    @Override
    public String toString() {
        return String.format("Layer(%08x)", hashCode());
    }

    // Getters
    @Override
    public final boolean isDestroyed() {
        return destroyed;
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
    public final short getZ() {
        return z;
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
    public boolean isVisible() {
        return visible;
    }

    @Override
    public boolean containsLayer(ILayer layer) {
        if (layer == null) {
            return false;
        }

        for (ILayer sub : subLayers) {
            if (sub == layer || sub.containsLayer(layer)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Collection<? extends ILayer> getSubLayers() {
        return Collections.unmodifiableList(subLayers);
    }

    @Override
    public IScreenshotBuffer getScreenshotBuffer() {
        return screenshotBuffer;
    }

    @Override
    public IRenderEnv getRenderEnv() {
        return renderEnv;
    }

    // Setters
    @Override
    public final void setX(double x) {
        setPos(x, getY());
    }

    @Override
    public final void setY(double y) {
        setPos(getX(), y);
    }

    @Override
    public void setZ(short z) {
        if (this.z != z) {
            this.z = z;
            markChanged();
        }
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

    @Override
    public void setRenderEnv(IRenderEnv env) {
        renderEnv = env;

        for (ILayer layer : subLayers) {
            layer.setRenderEnv(env);
        }

        for (Entity e : renderStream) {
            IDrawablePart d = e.getPart(partRegistry.drawable);
            d.setRenderEnv(env);
        }
    }

    @Override
    public void setVisible(boolean v) {
        if (visible != v) {
            visible = v;
            markChanged();
        }
    }

}
