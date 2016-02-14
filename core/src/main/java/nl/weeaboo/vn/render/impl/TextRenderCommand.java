package nl.weeaboo.vn.render.impl;

import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.render.IDrawTransform;

public final class TextRenderCommand extends BaseRenderCommand {

    public static final byte ID = ID_TEXT_RENDER_COMMAND;

    public final Matrix transform;
    public final double dx, dy;
    public final ITextLayout textLayout;
    public final double visibleGlyphs;

    protected TextRenderCommand(IDrawTransform dt, double dx, double dy, ITextLayout textLayout,
            double visibleGlyphs)
	{
        super(ID, dt.getZ(), dt.isClipEnabled(), dt.getBlendMode(), 0xFFFFFFFF,
                (byte)Double.doubleToRawLongBits(dt.getTransform().getTranslationY()));

        this.transform = dt.getTransform();
        this.dx = dx;
        this.dy = dy;
		this.textLayout = textLayout;
        this.visibleGlyphs = visibleGlyphs;
	}

}