package nl.weeaboo.vn.impl.image;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.annotation.Nullable;
import javax.annotation.WillCloseWhenClosed;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import nl.weeaboo.common.Checks;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.gdx.graphics.GdxTextureUtil;
import nl.weeaboo.vn.gdx.res.AbstractResource;
import nl.weeaboo.vn.gdx.res.GdxCleaner;
import nl.weeaboo.vn.image.ITexture;

/**
 * Pixmap-backed {@link IGdxTextureData}.
 */
@CustomSerializable
public final class PixelTextureData implements IGdxTextureData {

    private static final long serialVersionUID = 2L;

    private transient Pixmap pixels;
    private final RegionResource regionResource = new RegionResource();

    private PixelTextureData(Pixmap pixels) {
        this.pixels = Checks.checkNotNull(pixels);
        GdxCleaner.get().register(this, pixels);
    }

    /**
     * Creates a new instance from a pixmap using premultiplied alpha.
     */
    public static PixelTextureData fromPremultipliedPixmap(@WillCloseWhenClosed Pixmap pixmap) {
        return new PixelTextureData(pixmap);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        PixelTextureDataIO.serialize(pixels, out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        pixels = PixelTextureDataIO.deserialize(in);
        GdxCleaner.get().register(this, pixels);
    }

    @Deprecated
    @Override
    public void destroy() {
    }

    @Deprecated
    @Override
    public boolean isDestroyed() {
        return false;
    }

    /**
     * The backing pixmap for this texture data object. Uses premultiplied alpha.
     */
    public Pixmap borrowPixels() {
        return pixels;
    }

    @Override
    public ITexture toTexture(double sx, double sy) {
        return new GdxTexture(regionResource, sx, sy);
    }

    @Override
    public int getWidth() {
        return pixels.getWidth();
    }

    @Override
    public int getHeight() {
        return pixels.getHeight();
    }

    private final class RegionResource extends AbstractResource<TextureRegion> {

        private static final long serialVersionUID = 1L;

        private transient @Nullable TextureRegion region;

        @Override
        public TextureRegion get() {
            TextureRegion result = region;
            if (result == null) {
                Texture texture = new Texture(pixels);
                GdxTextureUtil.setDefaultTextureParams(texture);
                result = GdxTextureUtil.newGdxTextureRegion(texture);
                GdxCleaner.get().register(this, texture);
                region = result;
            }
            return result;
        }

    }

}
