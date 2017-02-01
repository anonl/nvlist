package nl.weeaboo.vn.impl.image;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.ITextureRenderer;
import nl.weeaboo.vn.impl.scene.AbstractRenderable;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IDrawable;

public final class TextureRenderer extends AbstractRenderable implements ITextureRenderer {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;

    /** May be null */
    private ITexture texture;

    private Area2D uv = ITexture.DEFAULT_UV;

    public TextureRenderer() {
        this(null);
    }

    /**
     * @param tex May be null
     */
    public TextureRenderer(ITexture tex) {
        this.texture = tex;

        pack();
    }

    @Override
    public void render(IDrawBuffer buffer, IDrawable d, Area2D bounds) {
        ITexture tex = getTexture();
        if (tex == null) {
            return;
        }

        buffer.drawQuad(d, d.getColorARGB(), tex, bounds, uv);
    }

    @Override
    public ITexture getTexture() {
        return texture;
    }

    @Override
    public Area2D getUV() {
        return uv;
    }

    @Override
    public double getNativeWidth() {
        ITexture texture = getTexture();
        return (texture != null ? texture.getWidth() : 0);
    }

    @Override
    public double getNativeHeight() {
        ITexture texture = getTexture();
        return (texture != null ? texture.getHeight() : 0);
    }

    @Override
    public void setTexture(ITexture newTexture) {
        if (texture != newTexture) {
            texture = newTexture;

            pack();
            fireChanged();
        }
    }

    @Override
    public final void setUV(double w, double h) {
        setUV(0, 0, w, h);
    }

    @Override
    public final void setUV(double x, double y, double w, double h) {
        setUV(Area2D.of(x, y, w, h));
    }

    @Override
    public void setUV(Area2D newUV) {
        Checks.checkNotNull(newUV);

        if (!uv.equals(newUV)) {
            uv = Checks.checkNotNull(newUV);

            fireChanged();
        }
    }

    @Override
    public void scrollUV(double du, double dv) {
        Area2D uv = getUV();

        setUV(uv.x + du, uv.y + dv, uv.w, uv.h);
    }

}
