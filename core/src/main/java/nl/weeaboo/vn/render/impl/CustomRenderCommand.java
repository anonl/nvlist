package nl.weeaboo.vn.render.impl;

import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.render.IRenderLogic;

final class CustomRenderCommand extends BaseRenderCommand {

    public static final byte ID = ID_CUSTOM_RENDER_COMMAND;

    public final Matrix transform;
    public final IRenderLogic renderLogic;

    public CustomRenderCommand(short z, boolean clipEnabled, BlendMode blendMode, int argb, Matrix transform,
            IRenderLogic renderLogic) {

        this(z, clipEnabled, blendMode, argb, transform, renderLogic, (byte)0);
    }

    public CustomRenderCommand(short z, boolean clipEnabled, BlendMode blendMode, int argb, Matrix transform,
            IRenderLogic renderLogic, byte privateField)
    {
        super(ID, z, clipEnabled, blendMode, argb, privateField);

        this.transform = transform;
        this.renderLogic = renderLogic;
	}

}