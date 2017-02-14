package nl.weeaboo.vn.impl.render;

import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.render.IRenderLogic;

final class CustomRenderCommand extends BaseRenderCommand {

    public static final byte ID = ID_CUSTOM_RENDER_COMMAND;

    public final Matrix transform;
    public final IRenderLogic renderLogic;

    public CustomRenderCommand(short z, boolean clipEnabled, BlendMode blendMode, int argb, Matrix transform,
            IRenderLogic renderLogic) {

        super(ID, z, clipEnabled, blendMode, argb, (byte)0);
        
        this.transform = transform;
        this.renderLogic = renderLogic;
    }

}