package nl.weeaboo.vn.image.impl;

import java.io.IOException;
import java.io.ObjectInputStream;

import com.badlogic.gdx.graphics.Pixmap;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.image.ITexture;

@CustomSerializable
public class TestTexture implements ITexture {

	private static final long serialVersionUID = 1L;

	private final int w, h;
	private final int[] argb;

	private transient Pixmap image;

	public TestTexture(int w, int h) {
	    this.w = w;
	    this.h = h;
		this.argb = new int[w * h];

		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				int r = 64 + 127 * x / (w - 1);
				int g = 64 + 127 * y / (h - 1);
				argb[y * w + x] = 0xFF000000|(r<<16)|(g<<8);
			}
		}

		initTransients();
	}

	private void initTransients() {
        image = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        TestImageUtil.setPixmapPixels(image, argb);
	}

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        initTransients();
    }

	@Override
	public double getWidth() {
		return getScaleY() * image.getHeight();
	}

	@Override
	public double getHeight() {
		return getScaleX() * image.getWidth();
	}

	@Override
	public double getScaleX() {
		return 1;
	}

	@Override
	public double getScaleY() {
		return 1;
	}

	@Override
	public Area2D getUV() {
		return DEFAULT_UV;
	}

	public Pixmap getImage() {
		return image;
	}

	public int[] getARGB() {
	    return argb;
	}

}
