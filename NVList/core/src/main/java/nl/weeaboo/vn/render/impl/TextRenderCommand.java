package nl.weeaboo.vn.render.impl;

import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.vn.core.BlendMode;

public final class TextRenderCommand extends BaseRenderCommand {

    public static final byte ID = ID_TEXT_RENDER_COMMAND;

    public final ITextLayout textLayout;
    public final float visibleGlyphs;
	public final double x, y;

    protected TextRenderCommand(short z, boolean clipEnabled, BlendMode blendMode,
		ITextLayout textLayout, float visibleGlyphs, double x, double y)
	{
        super(ID, z, clipEnabled, blendMode, 0xFFFFFFFF, (byte)Double.doubleToRawLongBits(y));

		this.textLayout = textLayout;
        this.visibleGlyphs = visibleGlyphs;
		this.x = x;
		this.y = y;
	}

}