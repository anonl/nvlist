package nl.weeaboo.vn.core.impl;

import java.io.IOException;
import java.io.ObjectInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Rect2D;
import nl.weeaboo.entity.Part;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.core.IDrawablePart;
import nl.weeaboo.vn.core.ILayer;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.math.Matrix;

@CustomSerializable
public class DrawablePart extends Part implements IDrawablePart {

	private static final long serialVersionUID = CoreImpl.serialVersionUID;
	private static final Logger LOG = LoggerFactory.getLogger(DrawablePart.class);

	private Layer parentLayer;

	private final BoundsHelper boundsHelper = new BoundsHelper();
	private boolean visible = true;
	private boolean clipEnabled = true;
	private short z;
	private double rgba[] = {1.0, 1.0, 1.0, 1.0};
	private BlendMode blendMode = BlendMode.DEFAULT;
	private /*transient*/ IRenderEnv renderEnv;

	private transient boolean changed;
	private transient int colorARGBInt;
	private transient Matrix transform;

	public DrawablePart() {
		initTransients();
	}

	//Functions
	private void initTransients() {
		changed = true;
		colorARGBInt = CoreImpl.packRGBAtoARGB(rgba[0], rgba[1], rgba[2], rgba[3]);
		boundsHelper.setChangeListener(new IChangeListener() {
			@Override
			public void onChanged() {
				markChanged();
				invalidateTransform();
			}
		});
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();

		initTransients();
	}

	protected void invalidateTransform() {
		transform = null;
	}

	protected final void markChanged() {
		changed = true;
	}

	@Override
	public final boolean consumeChanged() {
		boolean result = changed;
		changed = false;
		return result;
	}

	protected void onRenderEnvChanged() {
	}

	public static void moveToLayer(DrawablePart part, Layer newLayer) {
		if (part == null) {
			return;
		}

		Layer oldLayer = part.parentLayer;
		part.parentLayer = newLayer;

		if (oldLayer != null) {
			oldLayer.invalidateStreams();
		}
		if (newLayer != null) {
			newLayer.invalidateStreams();
		}

		LOG.debug("Moved drawable part: {} -> {}", oldLayer, newLayer);
	}

	//Getters
	@Override
	public final boolean isVisible() {
		return isVisible(0);
	}

	@Override
	public boolean isVisible(double minAlpha) {
		return visible && getAlpha() >= minAlpha;
	}

	@Override
	public final double getX() { return boundsHelper.getX(); }

	@Override
	public final double getY() { return boundsHelper.getY(); }

	@Override
	public final short getZ() { return z; }

	@Override
	public final double getWidth() { return boundsHelper.getWidth(); }

	@Override
	public final double getHeight() { return boundsHelper.getHeight(); }

	@Override
	public Rect2D getBounds() {
		return boundsHelper.getBounds();
	}

	@Override
	public boolean contains(double px, double py) {
		return boundsHelper.contains(px, py);
	}

	@Override
	public final int getColorRGB() {
		return getColorARGB() & 0xFFFFFF;
	}

	@Override
	public final int getColorARGB() {
		return colorARGBInt;
	}

	@Override
	public final double getRed() {
		return rgba[0];
	}

	@Override
	public final double getGreen() {
		return rgba[1];
	}

	@Override
	public final double getBlue() {
		return rgba[2];
	}

	@Override
	public final double getAlpha() {
		return rgba[3];
	}

	@Override
	public final BlendMode getBlendMode() {
		return blendMode;
	}

	@Override
	public final boolean isClipEnabled() {
		return clipEnabled;
	}

	protected Matrix createTransform() {
		return Matrix.translationMatrix(getX(), getY());
	}

	public final Matrix getTransform() {
		if (transform == null) {
			transform = createTransform();
		}
		return transform;
	}

	@Override
	public IRenderEnv getRenderEnv() {
		return renderEnv;
	}

	@Override
	public ILayer getParentLayer() {
		return parentLayer;
	}

	//Setters
	@Override
	public final void setX(double x) { setPos(x, getY()); }

	@Override
	public final void setY(double y) { setPos(getX(), y); }

	@Override
	public final void setWidth(double w) { setSize(w, getHeight()); }

	@Override
	public final void setHeight(double h) { setSize(getWidth(), h); }

	@Override
	public void setPos(double x, double y) { boundsHelper.setPos(x, y); }

	@Override
	public void setSize(double w, double h) { boundsHelper.setSize(w, h); }

	@Override
	public void setBounds(double x, double y, double w, double h) {
		setPos(x, y);
		setSize(w, h);
	}

	@Override
	public void setZ(short z) {
		if (this.z != z) {
			this.z = z;

			markChanged();
		}
	}

	@Override
	public final void setColor(double r, double g, double b) {
		setColor(r, g, b, rgba[3]);
	}

	@Override
	public void setColor(double r, double g, double b, double a) {
		if (rgba[0] != r || rgba[1] != g || rgba[2] != b || rgba[3] != a) {
			rgba[0] = r;
			rgba[1] = g;
			rgba[2] = b;
			rgba[3] = a;
			colorARGBInt = CoreImpl.packRGBAtoARGB(rgba[0], rgba[1], rgba[2], rgba[3]);

			markChanged();
		}
	}

	@Override
	public final void setColorRGB(int rgb) {
		int ri = (rgb>>16)&0xFF;
		int gi = (rgb>> 8)&0xFF;
		int bi = (rgb    )&0xFF;

		setColor(Math.max(0, Math.min(1, ri/255.0)),
				Math.max(0, Math.min(1, gi/255.0)),
				Math.max(0, Math.min(1, bi/255.0)));
	}

	@Override
	public final void setColorARGB(int argb) {
		int ai = (argb>>24)&0xFF;
		int ri = (argb>>16)&0xFF;
		int gi = (argb>> 8)&0xFF;
		int bi = (argb    )&0xFF;

		setColor(Math.max(0, Math.min(1, ri/255.0)),
				Math.max(0, Math.min(1, gi/255.0)),
				Math.max(0, Math.min(1, bi/255.0)),
				Math.max(0, Math.min(1, ai/255.0)));
	}

	@Override
	public final void setAlpha(double a) {
		setColor(rgba[0], rgba[1], rgba[2], a);
	}

	@Override
	public void setVisible(boolean v) {
		if (visible != v) {
			visible = v;
			markChanged();
		}
	}

	@Override
	public void setBlendMode(BlendMode mode) {
		if (mode == null) throw new IllegalArgumentException("BlendMode must not be null");

		if (blendMode != mode) {
			blendMode = mode;
			markChanged();
		}
	}

	@Override
	public void setClipEnabled(boolean clip) {
		if (clipEnabled != clip) {
			clipEnabled = clip;
			markChanged();
		}
	}

	@Override
	public void setRenderEnv(IRenderEnv env) {
		if (renderEnv != env) {
			renderEnv = env;
			onRenderEnvChanged();
		}
	}

}
