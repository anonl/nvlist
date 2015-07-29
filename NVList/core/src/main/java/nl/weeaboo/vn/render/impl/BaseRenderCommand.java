package nl.weeaboo.vn.render.impl;

import nl.weeaboo.vn.core.BlendMode;

class BaseRenderCommand extends RenderCommand {

	static final byte ID_LAYER_RENDER_COMMAND  = 8;
	static final byte ID_QUAD_RENDER_COMMAND   = 9;
	static final byte ID_BLEND_QUAD_COMMAND    = 10;
	static final byte ID_FADE_QUAD_COMMAND     = 11;
	static final byte ID_DISTORT_QUAD_COMMAND  = 12;
    static final byte ID_TEXT_RENDER_COMMAND   = 13;
	static final byte ID_CUSTOM_RENDER_COMMAND = 14;
	static final byte ID_SCREENSHOT_RENDER_COMMAND = 15;

	public final short z;
	public final boolean clipEnabled;
	public final BlendMode blendMode;
	public final int argb;

	protected BaseRenderCommand(byte id, short z, boolean clipEnabled,
			BlendMode blendMode, int argb, byte privateField)
	{
		super(id, ((-(1+z))             << 16) //We have to be careful, -Short.MIN_VALUE == Short.MIN_VALUE!!!
				| ((clipEnabled ? 1 : 0)     << 15)
				| ((blendMode.ordinal() & 7) << 12)
				| ((id & 15)                 << 8 )
				| (privateField & 255));

		this.z = z;
		this.clipEnabled = clipEnabled;
		this.blendMode = blendMode;
		this.argb = argb;
	}

	protected BaseRenderCommand(byte id, short z, boolean clipEnabled, byte privateField) {
		this(id, z, clipEnabled, BlendMode.DEFAULT, 0xFFFFFFFF, privateField);
	}

}
