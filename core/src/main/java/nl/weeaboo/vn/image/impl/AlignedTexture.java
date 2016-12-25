package nl.weeaboo.vn.image.impl;

import java.io.Serializable;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.impl.AlignUtil;
import nl.weeaboo.vn.image.ITexture;

/**
 * Wrapper object to contain a texture with alignment.
 */
public final class AlignedTexture implements Serializable {

    private static final long serialVersionUID = 1L;

    /** May be null */
    private final ITexture texture;

    private final double alignX, alignY;

    public AlignedTexture() {
        this(null, 0, 0);
    }

    /** @param texture May be null */
    public AlignedTexture(ITexture texture, double alignX, double alignY) {
        this.texture = texture;
        this.alignX = alignX;
        this.alignY = alignY;
    }

    /** May be null */
    public ITexture getTexture() {
        return texture;
    }

    public Rect2D getBounds() {
        return AlignUtil.getAlignedBounds(texture, alignX, alignY);
    }

    public Area2D getUV() {
        if (texture == null) {
            return ITexture.DEFAULT_UV;
        }
        return texture.getUV();
    }

}
