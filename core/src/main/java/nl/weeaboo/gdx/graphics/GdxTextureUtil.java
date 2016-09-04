package nl.weeaboo.gdx.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import nl.weeaboo.common.Area;
import nl.weeaboo.common.Area2D;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.impl.TextureAdapter;

public final class GdxTextureUtil {

    private GdxTextureUtil() {
    }

    public static Texture getTexture(ITexture tex) {
        if (tex instanceof TextureAdapter) {
            return ((TextureAdapter)tex).getTexture();
        } else {
            return null;
        }
    }

    public static TextureRegion getTextureRegion(ITexture tex, Area2D uv) {
        if (tex instanceof TextureAdapter) {
            return ((TextureAdapter)tex).getTextureRegion(uv);
        } else {
            return null;
        }
    }

    public static void bindTexture(int texUnit, ITexture tex) {
        Texture texture = GdxTextureUtil.getTexture(tex);
        if (texture != null) {
            texture.bind(texUnit);
        } else {
            Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0 + texUnit);
            Gdx.gl.glBindTexture(GL20.GL_TEXTURE_2D, 0);
        }
    }

    public static TextureRegion newGdxTextureRegion(Texture texture) {
        return newGdxTextureRegion(texture, Area.of(0, 0, texture.getWidth(), texture.getHeight()));
    }

    public static TextureRegion newGdxTextureRegion(Texture texture, Area subRect) {
        TextureRegion region = new TextureRegion(texture, subRect.x, subRect.y, subRect.w, subRect.h);
        region.flip(false, true);
        return region;
    }

}
