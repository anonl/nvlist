package nl.weeaboo.vn.impl.image;

import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.image.ITextureData;

interface IGdxTextureData extends ITextureData {

    /**
     * Returns a texture consisting of the pixels in this texture data object.
     */
    ITexture toTexture(double sx, double sy);

}
