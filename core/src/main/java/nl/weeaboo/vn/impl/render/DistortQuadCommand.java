package nl.weeaboo.vn.impl.render;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.core.DistortGrid;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.math.Matrix;

public final class DistortQuadCommand extends BaseRenderCommand {

    public static final byte ID = ID_DISTORT_QUAD_COMMAND;

    public final ITexture tex;
    public final Matrix transform;
    public final Area2D bounds;
    public final Area2D uv;
    public final DistortGrid grid;
    public final Rect2D clampBounds;

    public DistortQuadCommand(short z, boolean clipEnabled, BlendMode blendMode, int argb,
            ITexture tex, Matrix trans, Area2D bounds, Area2D uv,
            DistortGrid grid, Rect2D clampBounds) {

        super(ID, z, clipEnabled, blendMode, argb, tex != null ? (byte)tex.hashCode() : 0);

        this.tex = tex;
        this.transform = trans;
        this.bounds = bounds;
        this.uv = uv;

        this.grid = grid.copy();
        this.clampBounds = clampBounds;
    }

}
