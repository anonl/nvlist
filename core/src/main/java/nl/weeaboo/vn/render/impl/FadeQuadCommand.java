package nl.weeaboo.vn.render.impl;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.math.Matrix;

public final class FadeQuadCommand extends BaseRenderCommand {

	public static final byte ID = ID_FADE_QUAD_COMMAND;

	public final ITexture tex;
	public final Matrix transform;
	public final Area2D bounds;
	public final Area2D uv;
	public final int dir;
	public final boolean fadeIn;
	public final double span;
	public final double frac;

	public FadeQuadCommand(short z, boolean clipEnabled, BlendMode blendMode,
		int argb, ITexture tex, Matrix trans, Area2D bounds, Area2D uv,
		int dir, boolean fadeIn, double span, double frac)
	{
		super(ID, z, clipEnabled, blendMode, argb, tex != null ? (byte)tex.hashCode() : 0);

		this.tex = tex;
		this.transform = trans;
		this.bounds = bounds;
		this.uv = uv;
		this.dir = dir;
		this.fadeIn = fadeIn;
		this.span = span;
		this.frac = frac;
	}

}
