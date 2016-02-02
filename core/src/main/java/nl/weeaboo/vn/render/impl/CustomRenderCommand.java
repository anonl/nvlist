package nl.weeaboo.vn.render.impl;

import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.render.IRenderLogic;

final class CustomRenderCommand extends BaseRenderCommand {

    public final IRenderLogic renderLogic;

    public CustomRenderCommand(short z, boolean clipEnabled, BlendMode blendMode, int argb,
            IRenderLogic renderLogic) {

        this(z, clipEnabled, blendMode, argb, renderLogic, (byte)0);
    }

    public CustomRenderCommand(short z, boolean clipEnabled, BlendMode blendMode, int argb,
            IRenderLogic renderLogic, byte privateField)
    {
        super(ID_CUSTOM_RENDER_COMMAND, z, clipEnabled, blendMode, argb, privateField);

        this.renderLogic = renderLogic;
	}

}