package nl.weeaboo.vn.impl.image;

import javax.annotation.Nullable;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import nl.weeaboo.vn.gdx.res.AbstractResource;
import nl.weeaboo.vn.gdx.res.GdxCleaner;
import nl.weeaboo.vn.image.ITexture;

/**
 * Texture data stored in volatile memory (typically VRAM).
 */
public final class VolatileTextureData implements IGdxTextureData {

    private static final long serialVersionUID = 2L;

    private final RegionResource regionResource;
    private final int width;
    private final int height;

    private VolatileTextureData(TextureRegion textureRegion) {
        regionResource = new RegionResource(textureRegion);

        this.width = textureRegion.getRegionWidth();
        this.height = textureRegion.getRegionHeight();
    }

    public static VolatileTextureData fromRegion(TextureRegion texture) {
        return new VolatileTextureData(texture);
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

    @Override
    public ITexture toTexture(double sx, double sy) {
        return new GdxTexture(regionResource, sx, sy);
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    private static final class RegionResource extends AbstractResource<TextureRegion> {

        private static final long serialVersionUID = 1L;

        private transient @Nullable TextureRegion textureRegion;

        public RegionResource(TextureRegion textureRegion) {
            this.textureRegion = textureRegion;
            GdxCleaner.get().register(this, textureRegion.getTexture());
        }

        @Override
        public TextureRegion get() {
            return textureRegion;
        }

    }
}
