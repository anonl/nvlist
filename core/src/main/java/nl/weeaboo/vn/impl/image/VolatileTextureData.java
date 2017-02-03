package nl.weeaboo.vn.impl.image;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import nl.weeaboo.common.Checks;

public final class VolatileTextureData implements IGdxTextureData {

    private static final long serialVersionUID = 1L;

    private final transient TextureRegion textureRegion;
    private final boolean isShared;
    private final int width;
    private final int height;

    private boolean destroyed;

    private VolatileTextureData(TextureRegion textureRegion, boolean isShared) {
        this.textureRegion = Checks.checkNotNull(textureRegion);
        this.isShared = isShared;

        this.width = textureRegion.getRegionWidth();
        this.height = textureRegion.getRegionHeight();
    }

    /**
     * @param isShared If {@code true}, the texture region is considered a shared resource and disposing the
     *        resulting texture data object won't dispose the texture region.
     */
    public static VolatileTextureData fromRegion(TextureRegion texture, boolean isShared) {
        return new VolatileTextureData(texture, isShared);
    }

    @Override
    public TextureRegion toTextureRegion() {
        return textureRegion;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public void destroy() {
        if (!destroyed) {
            destroyed = true;

            if (!isShared) {
                textureRegion.getTexture().dispose();
            }
        }
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public final void dispose() {
        destroy();
    }

}
