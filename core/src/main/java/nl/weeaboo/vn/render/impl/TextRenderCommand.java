package nl.weeaboo.vn.render.impl;

import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.math.Matrix;

public final class TextRenderCommand extends BaseRenderCommand {

    public static final byte ID = ID_TEXT_RENDER_COMMAND;

    public final Matrix transform;
    public final ITextLayout textLayout;
    public final float visibleGlyphs;

    protected TextRenderCommand(short z, boolean clipEnabled, BlendMode blendMode, Matrix transform,
            ITextLayout textLayout, float visibleGlyphs)
	{
        super(ID, z, clipEnabled, blendMode, 0xFFFFFFFF,
                (byte)Double.doubleToRawLongBits(transform.getTranslationY()));

        this.transform = transform;
		this.textLayout = textLayout;
        this.visibleGlyphs = visibleGlyphs;
	}

}