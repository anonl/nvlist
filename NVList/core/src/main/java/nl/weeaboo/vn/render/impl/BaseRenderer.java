package nl.weeaboo.vn.render.impl;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.image.IWritableScreenshot;
import nl.weeaboo.vn.render.IRenderer;
import nl.weeaboo.vn.render.RenderUtil;

public abstract class BaseRenderer implements IRenderer<DrawBuffer> {

	protected final IRenderEnv renderEnv;
	protected final RenderStats renderStats;

	/** This boolean is <code>true</code> when inside a call to render() */
	protected boolean rendering;

	//--- Properties only valid while render==true beneath this line ----------------------
	private boolean clipping;
	private BlendMode blendMode;
	private int foreground;
	//-------------------------------------------------------------------------------------

	public BaseRenderer(IRenderEnv env, RenderStats stats) {
		this.renderEnv = env;
		this.renderStats = Checks.checkNotNull(stats);

		renderReset();
	}

	//Functions
	private void renderReset() {
		clipping = true;
		blendMode = BlendMode.DEFAULT;
		foreground = 0xFFFFFFFF;
	}

	@Override
	public void render(DrawBuffer d) {
		rendering = true;
        renderStats.startRender();
		try {
			renderReset();
			renderBegin();

			final LayerRenderCommand lrc = d.getRootLayerCommand();
			if (lrc != null) {
				renderLayer(d, lrc, renderEnv.getGLClip(), renderEnv.getGLClip().toRect2D());
			}

			renderEnd();
		} finally {
			rendering = false;
			renderStats.stopRender();
		}
	}

	protected void renderBegin() {
		applyRenderState();
	}

	protected void renderEnd() {
		renderReset();
		applyRenderState();
	}

	private void applyRenderState() {
		setClip(clipping);
		setBlendMode(blendMode);
		setColor(foreground);
	}

	protected void renderLayer(DrawBuffer buffer, LayerRenderCommand lrc, Rect parentClip, Rect2D parentClip2D) {
		int cstart = buffer.getLayerStart(lrc.layerId);
		int cend = buffer.getLayerEnd(lrc.layerId);
		if (cend <= cstart) {
			return;
		}

		//Get sorted render commands
		BaseRenderCommand[] cmds = buffer.sortCommands(cstart, cend);

		//Setup clipping/translate
		final Rect2D bounds = lrc.layerBounds;
		final Rect2D layerClip2D;
		final double scale = renderEnv.getScale();
		double bx0 = bounds.x * scale;
		double by0 = bounds.y * scale;
		double bx1 = (bounds.x+bounds.w) * scale;
		double by1 = (bounds.y+bounds.h) * scale;

		layerClip2D = Rect2D.of(
			parentClip2D.x + Math.max(0, Math.min(parentClip2D.w, bx0)),
			parentClip2D.y + Math.max(0, Math.min(parentClip2D.h, parentClip2D.h-by1)),
			Math.max(0, Math.min(parentClip2D.w-bx0, bx1-bx0)),
			Math.max(0, Math.min(parentClip2D.h-by0, by1-by0))
		);
		final Rect layerClip = RenderUtil.roundClipRect(layerClip2D);

		setClipRect(layerClip);
		translate(bounds.x, bounds.y);

		//Render buffered commands
		long renderStatsTimestamp = 0;
		for (int n = cstart; n < cend; n++) {
			BaseRenderCommand cmd = cmds[n];
			if (cmd.id != QuadRenderCommand.ID) {
				flushQuadBatch();
			}

			//Clipping changed
			if (cmd.clipEnabled != clipping) {
				flushQuadBatch();
				clipping = cmd.clipEnabled;
				setClip(clipping);
			}

			//Blend mode changed
			if (cmd.blendMode != blendMode) {
				flushQuadBatch();
				blendMode = cmd.blendMode;
				setBlendMode(blendMode);
			}

			//Foreground color changed
			if (cmd.argb != foreground) {
				flushQuadBatch();
				foreground = cmd.argb;
				setColor(foreground);
			}

			//Perform command-specific rendering
			renderStatsTimestamp = System.nanoTime();

			preRenderCommand(cmd);

			switch (cmd.id) {
			case LayerRenderCommand.ID: renderLayer(buffer, (LayerRenderCommand)cmd, layerClip, layerClip2D); break;
			case QuadRenderCommand.ID:  renderQuad((QuadRenderCommand)cmd); break;
			case BlendQuadCommand.ID:   renderBlendQuad((BlendQuadCommand)cmd); break;
			case FadeQuadCommand.ID:    renderFadeQuad((FadeQuadCommand)cmd); break;
			case DistortQuadCommand.ID: renderDistortQuad((DistortQuadCommand)cmd); break;
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

			renderStats.log(cmd, System.nanoTime()-renderStatsTimestamp);
		}

		flushQuadBatch();

		translate(-bounds.x, -bounds.y);
		setClipRect(parentClip);
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

	public abstract void renderQuad(QuadRenderCommand qrc);

	public abstract void renderBlendQuad(BlendQuadCommand bqc);

	public abstract void renderFadeQuad(FadeQuadCommand fqc);

	public abstract void renderDistortQuad(DistortQuadCommand dqc);

	public abstract void renderTriangleGrid(TriangleGrid grid);

	public abstract void renderScreenshot(IWritableScreenshot out, Rect glScreenRect);

	protected abstract boolean renderUnknownCommand(RenderCommand cmd);

	protected abstract void setClip(boolean c);
	protected abstract void setColor(int argb);
	protected abstract void setBlendMode(BlendMode bm);

	protected abstract void setClipRect(Rect glRect);
	protected abstract void translate(double dx, double dy);

	protected void flushQuadBatch() {
	}

}
