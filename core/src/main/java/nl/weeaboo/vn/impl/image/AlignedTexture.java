package nl.weeaboo.vn.impl.image;

import java.io.Serializable;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.core.AlignUtil;

/**
 * Wrapper object to contain a texture with alignment.
 */
public final class AlignedTexture implements Serializable {

    private static final long serialVersionUID = 1L;

    /** May be null. */
    private final ITexture texture;

    private final double alignX;
    private final double alignY;

    public AlignedTexture() {
        this(null, 0, 0);
    }

    /**
     * @param texture May be null
     */
    public AlignedTexture(ITexture texture, double alignX, double alignY) {
        this.texture = texture;
        this.alignX = alignX;
        this.alignY = alignY;
    }

    /** May be null */
    public ITexture getTexture() {
        return texture;
    }

    /** Returns the relative texture bounds, based on the embedded alignment. */
    public Rect2D getBounds() {
        return AlignUtil.getAlignedBounds(texture, alignX, alignY);
    }

    /**
     * @return The UV-coordinates of the texture, or {@link ITexture#DEFAULT_UV} if the texture is {@code null}.
     * @see ITexture#getUV()
     */
    public Area2D getUV() {
        if (texture == null) {
            return ITexture.DEFAULT_UV;
        }
        return texture.getUV();
    }

}
