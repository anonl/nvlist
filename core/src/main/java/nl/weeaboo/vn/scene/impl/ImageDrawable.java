package nl.weeaboo.vn.scene.impl;

import java.io.IOException;
import java.io.ObjectInputStream;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.core.IChangeListener;
import nl.weeaboo.vn.core.NullRenderer;
import nl.weeaboo.vn.core.impl.AlignUtil;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.impl.TextureRenderer;
import nl.weeaboo.vn.math.Vec2;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IImageDrawable;
import nl.weeaboo.vn.scene.IRenderable;

@CustomSerializable
public class ImageDrawable extends Transformable implements IImageDrawable {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private IRenderable renderer = NullRenderer.getInstance();

    private transient IChangeListener rendererListener;

    public ImageDrawable() {
        initTransients();
    }

    private void initTransients() {
        rendererListener = new IChangeListener() {
            @Override
            public void onChanged() {
                invalidateTransform();
            }
        };

        initRenderer();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        initTransients();
    }

    private void initRenderer() {
        renderer.onAttached(rendererListener);

        invalidateTransform();
    }

    @Override
    public void draw(IDrawBuffer buffer) {
        Area2D bounds = Area2D.of(getAlignOffsetX(), getAlignOffsetY(), getUnscaledWidth(),
                getUnscaledHeight());

        renderer.render(this, bounds, buffer);
    }

    @Override
    public double getUnscaledWidth() {
        return renderer.getNativeWidth();
    }

    @Override
    public double getUnscaledHeight() {
        return renderer.getNativeHeight();
    }

    @Override
    public Rect2D getUntransformedVisualBounds() {
        return renderer.getVisualBounds();
    }

    @Override
    public IRenderable getRenderer() {
        return renderer;
    }

    @Override
    public void setRenderer(IRenderable r) {
        setRenderer(r, 7);
    }

    @Override
    public void setRenderer(IRenderable r, int anchor) {
        Checks.checkNotNull(r);

        double alignX = getAlignX();
        double alignY = getAlignY();
        if (renderer != r) {
            double w0 = getUnscaledWidth();
            double h0 = getUnscaledHeight();
            double w1 = r.getNativeWidth();
            double h1 = r.getNativeHeight();
            Rect2D rect = AlignUtil.getAlignedBounds(w0, h0, getAlignX(), getAlignY());
            Vec2 align = AlignUtil.alignSubRect(rect, w1, h1, anchor);
            alignX = align.x;
            alignY = align.y;
        }

        setRenderer(r, alignX, alignY);
    }

    @Override
    public void setRenderer(IRenderable r, double alignX, double alignY) {
        Checks.checkNotNull(r);

        if (renderer != r || getAlignX() != alignX || getAlignY() != alignY) {
            double sx = getScaleX();
            double sy = getScaleY();

            renderer.onDetached(rendererListener);
            renderer = r;
            initRenderer();

            setScale(sx, sy); // Maintain relative scale, but not the exact size
            setAlign(alignX, alignY);
        }
    }

    @Override
    public void setTexture(ITexture texture) {
        setRenderer(new TextureRenderer(texture));
    }

    @Override
    public void setTexture(ITexture texture, int anchor) {
        setRenderer(new TextureRenderer(texture), anchor);
    }

    @Override
    public void setTexture(ITexture texture, double alignX, double alignY) {
        setRenderer(new TextureRenderer(texture), alignX, alignY);
    }

}
