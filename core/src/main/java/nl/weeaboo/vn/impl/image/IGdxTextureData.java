package nl.weeaboo.vn.impl.image;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

import nl.weeaboo.vn.image.ITextureData;

interface IGdxTextureData extends ITextureData, Disposable {

    /**
     * @return A texture region representing this texture, or {@code null} if such a texture couldn't be
     *         created.
     */
    TextureRegion toTextureRegion();

}
