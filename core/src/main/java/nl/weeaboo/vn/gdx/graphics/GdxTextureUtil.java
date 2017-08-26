package nl.weeaboo.vn.gdx.graphics;

import javax.annotation.Nullable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import nl.weeaboo.common.Area;
import nl.weeaboo.common.Area2D;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.image.TextureAdapter;

public final class GdxTextureUtil {

    private GdxTextureUtil() {
    }

    /**
     * Returns the backing libGDX texture for the given {@link ITexture}, or {@code null} if unable to extract a valid
     * texture.
     */
    public static @Nullable Texture getTexture(ITexture tex) {
        if (tex instanceof TextureAdapter) {
            return ((TextureAdapter)tex).getTexture();
        } else {
            return null;
        }
    }

    /**
     * @see #getTexture(ITexture)
     */
    public static @Nullable TextureRegion getTextureRegion(ITexture tex) {
        return getTextureRegion(tex, ITexture.DEFAULT_UV);
    }

    /**
     * Returns a sub-region of the backing libGDX texture for the given {@link ITexture}, or {@code null} if unable to
     * extract a valid texture.
     */
    public static @Nullable TextureRegion getTextureRegion(ITexture tex, Area2D uv) {
        if (tex instanceof TextureAdapter) {
            return ((TextureAdapter)tex).getTextureRegion(uv);
        } else {
            return null;
        }
    }

    /**
     * Binds a texture to OpenGL. If no backing libGDX texture can be resolved, binds {@code 0} as the active texture.
     * @see #getTexture(ITexture)
     */
    public static void bindTexture(int texUnit, ITexture tex) {
        Texture texture = GdxTextureUtil.getTexture(tex);
        if (texture != null) {
            texture.bind(texUnit);
        } else {
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + texUnit);
            Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, 0);
        }
    }

    /**
     * Creates a sub-region for the given libGDX texture.
     * @see #newGdxTextureRegion(Texture, Area)
     */
    public static TextureRegion newGdxTextureRegion(Texture texture) {
        return newGdxTextureRegion(texture, Area.of(0, 0, texture.getWidth(), texture.getHeight()));
    }

    /**
     * Creates a sub-region for the given libGDX texture.
     */
    public static TextureRegion newGdxTextureRegion(Texture texture, Area subRect) {
        TextureRegion region = new TextureRegion(texture, subRect.x, subRect.y, subRect.w, subRect.h);
        region.flip(false, true);
        return region;
    }

    /**
     * Applies NVList-wide default values for the texture's filter/wrap settings.
     */
    public static void setDefaultTextureParams(Texture texture) {
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        texture.setWrap(TextureWrap.ClampToEdge, TextureWrap.ClampToEdge);
    }

}
