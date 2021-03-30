package nl.weeaboo.vn.impl.render;

import nl.weeaboo.styledtext.layout.ITextLayout;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.render.IDrawTransform;

/**
 * Draw command for rendering text.
 */
public final class TextRenderCommand extends BaseRenderCommand {

    public static final byte ID = ID_TEXT_RENDER_COMMAND;

    public final Matrix transform;
    public final double dx;
    public final double dy;
    public final ITextLayout textLayout;
    public final double visibleGlyphs;

    TextRenderCommand(IDrawTransform dt, double dx, double dy, int argb, ITextLayout textLayout,
            double visibleGlyphs) {

        super(ID, dt.getZ(), dt.isClipEnabled(), dt.getBlendMode(), argb,
                (byte)Double.doubleToRawLongBits(dt.getTransform().getTranslationY()));

        this.transform = dt.getTransform();
        this.dx = dx;
        this.dy = dy;
        this.textLayout = textLayout;
        this.visibleGlyphs = visibleGlyphs;
    }

}