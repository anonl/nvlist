package nl.weeaboo.vn.impl.scene;

import java.io.IOException;
import java.io.ObjectInputStream;

import javax.annotation.Nullable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.core.Direction;
import nl.weeaboo.vn.core.IEventListener;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.ITextureRenderer;
import nl.weeaboo.vn.impl.core.AlignUtil;
import nl.weeaboo.vn.impl.image.TextureRenderer;
import nl.weeaboo.vn.math.Vec2;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IImageDrawable;
import nl.weeaboo.vn.scene.IRenderable;

@CustomSerializable
public class ImageDrawable extends Transformable implements IImageDrawable {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private IRenderable renderer = new NullRenderer();

    private transient IEventListener rendererListener;

    public ImageDrawable() {
        initTransients();
    }

    private void initTransients() {
        rendererListener = new IEventListener() {
            @Override
            public void onEvent() {
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
    protected void onDestroyed() {
        renderer.onDetached(rendererListener);

        super.onDestroyed();
    }

    @Override
    public void onTick() {
        super.onTick();

        renderer.update();
    }

    @Override
    public void draw(IDrawBuffer buffer) {
        renderer.render(buffer, this, getAlignOffsetX(), getAlignOffsetY());
    }

    @Override
    public double getUnscaledWidth() {
        return renderer.getWidth();
    }

    @Override
    public double getUnscaledHeight() {
        return renderer.getHeight();
    }

    @Override
    public Rect2D getUntransformedVisualBounds() {
        return renderer.getVisualBounds();
    }

    @Override
    public void setUnscaledSize(double w, double h) {
        renderer.setSize(w, h);
    }

    @Override
    public IRenderable getRenderer() {
        return renderer;
    }

    @Override
    public void setRenderer(IRenderable r) {
        setRenderer(r, Direction.TOP_LEFT);
    }

    @Override
    public void setRenderer(IRenderable r, Direction anchor) {
        Checks.checkNotNull(r);

        double alignX = getAlignX();
        double alignY = getAlignY();
        if (renderer != r) {
            double w0 = getUnscaledWidth();
            double h0 = getUnscaledHeight();
            double w1 = r.getWidth();
            double h1 = r.getHeight();

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
            renderer.onDetached(rendererListener);
            renderer = r;
            initRenderer();

            setAlign(alignX, alignY);
        }
    }

    @Override
    public @Nullable ITexture getTexture() {
        if (!(renderer instanceof ITextureRenderer)) {
            return null;
        }
        ITextureRenderer texRenderer = (ITextureRenderer)renderer;
        return texRenderer.getTexture();
    }

    @Override
    public void setTexture(ITexture texture) {
        setRenderer(new TextureRenderer(texture));
    }

    @Override
    public void setTexture(ITexture texture, int anchor) {
        setTexture(texture, Direction.fromInt(anchor));
    }

    @Override
    public void setTexture(ITexture texture, Direction anchor) {
        setRenderer(new TextureRenderer(texture), anchor);
    }

    @Override
    public void setTexture(ITexture texture, double alignX, double alignY) {
        setRenderer(new TextureRenderer(texture), alignX, alignY);
    }

}
