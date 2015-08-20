package nl.weeaboo.vn.render.impl;

import nl.weeaboo.vn.image.IWritableScreenshot;

public final class ScreenshotRenderCommand extends BaseRenderCommand {

	public static final byte ID = ID_SCREENSHOT_RENDER_COMMAND;

	public final IWritableScreenshot ss;

	public ScreenshotRenderCommand(IWritableScreenshot ss, boolean clip) {
		super(ID, ss.getZ(), clip, (byte)255);

		this.ss = ss;
	}

}
