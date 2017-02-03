package nl.weeaboo.vn.impl.render;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.math.Matrix;

public final class QuadRenderCommand extends BaseRenderCommand {

    public static final byte ID = ID_QUAD_RENDER_COMMAND;

    public final ITexture tex;
    public final Matrix transform;
    public final Area2D bounds;
    public final Area2D uv;

    public QuadRenderCommand(short z, boolean clipEnabled, BlendMode blendMode,
            int argb, ITexture tex, Matrix transform, Area2D bounds, Area2D uv) {

        super(ID, z, clipEnabled, blendMode, argb, (byte)tex.hashCode());

        this.tex = Checks.checkNotNull(tex);
        this.transform = transform;
        this.bounds = bounds;
        this.uv = uv;
    }

}
