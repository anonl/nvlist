package nl.weeaboo.vn.render.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import nl.weeaboo.vn.image.IScreenshotBuffer;
import nl.weeaboo.vn.image.IWritableScreenshot;
import nl.weeaboo.vn.render.IDrawBuffer;

public class ScreenshotBuffer implements IScreenshotBuffer {

	private static final long serialVersionUID = RenderImpl.serialVersionUID;

	private Collection<ScreenshotEntry> screenshots;

	public ScreenshotBuffer() {
		screenshots = new ArrayList<ScreenshotEntry>();
	}

	//Functions
	@Override
	public void add(IWritableScreenshot ss, boolean clip) {
		screenshots.add(new ScreenshotEntry(ss, clip));
	}

	@Override
	public void clear() {
		for (ScreenshotEntry entry : screenshots) {
			entry.screenshot.cancel();
		}
		screenshots.clear();
	}

	public void flush(IDrawBuffer d) {
		for (ScreenshotEntry entry : screenshots) {
			d.screenshot(entry.screenshot, entry.clip);
		}
		screenshots.clear();
	}

	//Getters
	@Override
	public boolean isEmpty() {
		return screenshots.isEmpty();
	}

	//Setters

	//Inner Classes
	private static class ScreenshotEntry implements Serializable {

		private static final long serialVersionUID = 1L;

		final IWritableScreenshot screenshot;
		final boolean clip;

		public ScreenshotEntry(IWritableScreenshot ss, boolean clip) {
			this.screenshot = ss;
			this.clip = clip;
		}

	}

}
