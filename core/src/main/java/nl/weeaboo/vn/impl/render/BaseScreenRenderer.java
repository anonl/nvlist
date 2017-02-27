package nl.weeaboo.vn.impl.render;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.google.common.base.Stopwatch;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.image.IWritableScreenshot;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.render.IScreenRenderer;
import nl.weeaboo.vn.render.RenderUtil;

public abstract class BaseScreenRenderer implements IScreenRenderer<DrawBuffer> {

    protected final IRenderEnv renderEnv;
    protected final RenderStats renderStats;

    //--- Properties only valid while render==true beneath this line ----------------------
    private boolean clipping;
    private BlendMode blendMode;
    private int foreground;
    //-------------------------------------------------------------------------------------

    public BaseScreenRenderer(IRenderEnv env, RenderStats stats) {
        this.renderEnv = env;
        this.renderStats = Checks.checkNotNull(stats);

        renderReset();
    }

    private void renderReset() {
        clipping = false;
        blendMode = BlendMode.DEFAULT;
        foreground = 0xFFFFFFFF;
    }

    @Override
    public void render(DrawBuffer d) {
        renderStats.startRender();
        try {
            renderReset();
            renderBegin();

            Rect2D screenRect = Rect2D.of(0, 0, renderEnv.getWidth(), renderEnv.getHeight());
            renderLayer(d, screenRect, renderEnv.getGLClip(), renderEnv.getGLClip().toRect2D());

            renderEnd();
        } finally {
            renderStats.stopRender();
        }
    }

    protected void renderBegin() {
        applyRenderState();
    }

    protected void renderEnd() {
        renderReset();
        applyClipRect(renderEnv.getGLClip());
        applyRenderState();
    }

    protected final void applyRenderState() {
        applyClip(clipping);
        applyBlendMode(blendMode);
        applyColor(foreground);
    }

    protected void renderLayer(DrawBuffer buffer, Rect2D bounds, Rect parentClip, Rect2D parentClip2D) {
        // Get sorted render commands
        Collection<? extends BaseRenderCommand> cmds = buffer.getCommands();
        if (cmds.isEmpty()) {
            return;
        }

        //Setup clipping/translate
        final Rect2D layerClip2D;
        final double scale = renderEnv.getScale();
        double bx0 = bounds.x * scale;
        double by0 = bounds.y * scale;
        double bx1 = (bounds.x + bounds.w) * scale;
        double by1 = (bounds.y + bounds.h) * scale;

        layerClip2D = Rect2D.of(
            parentClip2D.x + Math.max(0, Math.min(parentClip2D.w, bx0)),
            parentClip2D.y + Math.max(0, Math.min(parentClip2D.h, parentClip2D.h - by1)),
            Math.max(0, Math.min(parentClip2D.w - bx0, bx1 - bx0)),
            Math.max(0, Math.min(parentClip2D.h - by0, by1 - by0))
        );
        final Rect layerClip = RenderUtil.roundClipRect(layerClip2D);

        applyClipRect(layerClip);
        translate(bounds.x, bounds.y);

        //Render buffered commands
        Stopwatch sw = Stopwatch.createUnstarted();
        for (BaseRenderCommand cmd : cmds) {
            if (cmd.id != QuadRenderCommand.ID) {
                flushQuadBatch();
            }

            //Clipping changed
            if (cmd.clipEnabled != clipping) {
                flushQuadBatch();
                clipping = cmd.clipEnabled;
                applyClip(clipping);
            }

            //Blend mode changed
            if (cmd.blendMode != blendMode) {
                flushQuadBatch();
                blendMode = cmd.blendMode;
                applyBlendMode(blendMode);
            }

            //Foreground color changed
            if (cmd.argb != foreground) {
                flushQuadBatch();
                foreground = cmd.argb;
                applyColor(foreground);
            }

            //Perform command-specific rendering
            sw.reset();
            sw.start();

            preRenderCommand(cmd);

            switch (cmd.id) {
            case LayerRenderCommand.ID: {
                LayerRenderCommand lrc = (LayerRenderCommand)cmd;
                translate(lrc.contentDx, lrc.contentDy);
                renderLayer(buffer.getLayerBuffer(lrc.layerId), lrc.layerBounds, layerClip, layerClip2D);
                translate(-lrc.contentDx, -lrc.contentDy);
            } break;
            case QuadRenderCommand.ID:
                renderQuad((QuadRenderCommand)cmd);
                break;
            case DistortQuadCommand.ID:
                renderDistortQuad((DistortQuadCommand)cmd);
                break;
            case TextRenderCommand.ID:
                renderText((TextRenderCommand)cmd);
                break;
            case CustomRenderCommand.ID:
                renderCustom((CustomRenderCommand)cmd);
                break;
            case ScreenshotRenderCommand.ID: {
                ScreenshotRenderCommand src = (ScreenshotRenderCommand)cmd;
                renderScreenshot(src.ss, src.clipEnabled ? layerClip : renderEnv.getGLClip());
            } break;
            default:
                if (!renderUnknownCommand(cmd)) {
                    throw new RuntimeException("Unable to process render command (id=" + cmd.id + ")");
                }
            }

            postRenderCommand(cmd);

            sw.stop();
            renderStats.logCommand(cmd, sw.elapsed(TimeUnit.NANOSECONDS));
        }

        flushQuadBatch();

        translate(-bounds.x, -bounds.y);
        applyClipRect(parentClip);
    }

    /**
     * @param cmd The command that's about to be handled.
     */
    protected void preRenderCommand(BaseRenderCommand cmd) {
    }

    /**
     * @param cmd The command that was just handled.
     */
    protected void postRenderCommand(BaseRenderCommand cmd) {
    }

    protected abstract void renderQuad(QuadRenderCommand qrc);

    protected abstract void renderDistortQuad(DistortQuadCommand dqc);

    protected abstract void renderText(TextRenderCommand trc);

    protected abstract void renderTriangleGrid(TriangleGrid grid, ShaderProgram shader);

    protected abstract void renderScreenshot(IWritableScreenshot out, Rect glScreenRect);

    protected abstract void renderCustom(CustomRenderCommand cmd);

    protected abstract boolean renderUnknownCommand(RenderCommand cmd);

    protected abstract void applyClip(boolean c);

    protected abstract void applyColor(int argb);

    protected abstract void applyBlendMode(BlendMode bm);

    protected abstract void applyClipRect(Rect glRect);

    protected abstract void translate(double dx, double dy);

    protected void flushQuadBatch() {
    }

}
