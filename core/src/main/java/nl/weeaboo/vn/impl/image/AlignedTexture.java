package nl.weeaboo.vn.impl.image;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.core.AlignUtil;

/**
 * Wrapper object to contain a texture with alignment.
 */
public final class AlignedTexture implements Serializable {

    private static final long serialVersionUID = 1L;

    private final @Nullable ITexture texture;

    private final double alignX;
    private final double alignY;

    public AlignedTexture() {
        this(null, 0, 0);
    }

    public AlignedTexture(@Nullable ITexture texture, double alignX, double alignY) {
        this.texture = texture;
        this.alignX = alignX;
        this.alignY = alignY;
    }

    @CheckForNull
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
