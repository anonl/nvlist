package nl.weeaboo.vn.scene.impl;

import java.io.IOException;
import java.io.ObjectInputStream;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.core.IChangeListener;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.scene.IDrawable;

@CustomSerializable
public abstract class Drawable extends VisualElement implements IDrawable {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private final BoundsHelper layoutBounds = new BoundsHelper();
    private final ColorHelper color = new ColorHelper();

	private boolean clipEnabled = true;
	private BlendMode blendMode = BlendMode.DEFAULT;

	private transient Matrix transform;

	public Drawable() {
		initTransients();
	}

	//Functions
	private void initTransients() {
        layoutBounds.addChangeListener(new IChangeListener() {
			@Override
			public void onChanged() {
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

	//Getters
	@Override
	public final boolean isVisible() {
		return isVisible(0);
	}

    @Override
	public boolean isVisible(double minAlpha) {
        return super.isVisible() && getAlpha() >= minAlpha;
	}

	@Override
    public final double getX() {
        return layoutBounds.getX();
    }

	@Override
    public final double getY() {
        return layoutBounds.getY();
    }

	@Override
    public final double getWidth() {
        return layoutBounds.getWidth();
    }

	@Override
    public final double getHeight() {
        return layoutBounds.getHeight();
    }

	@Override
    public Rect2D getVisualBounds() {
        return layoutBounds.getBounds();
	}

	@Override
	public boolean contains(double px, double py) {
        return layoutBounds.contains(px, py);
	}

	@Override
	public final int getColorRGB() {
		return getColorARGB() & 0xFFFFFF;
	}

	@Override
	public final int getColorARGB() {
        return color.getColorARGB();
	}

	@Override
	public final double getRed() {
        return color.getRed();
	}

	@Override
	public final double getGreen() {
        return color.getGreen();
	}

	@Override
	public final double getBlue() {
        return color.getBlue();
	}

	@Override
	public final double getAlpha() {
        return color.getAlpha();
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

	@Override
    public final Matrix getTransform() {
		if (transform == null) {
			transform = createTransform();
		}
		return transform;
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
    public final void translate(double dx, double dy) {
        setPos(getX() + dx, getY() + dy);
    }

	@Override
    public void setPos(double x, double y) {
        layoutBounds.setPos(x, y);
    }

	@Override
    public void setSize(double w, double h) {
        layoutBounds.setSize(w, h);
    }

	@Override
	public void setBounds(double x, double y, double w, double h) {
		setPos(x, y);
		setSize(w, h);
	}

    @Override
    public final void setAlpha(double a) {
        color.setAlpha(a);
    }

	@Override
	public final void setColor(double r, double g, double b) {
        color.setColor(r, g, b);
	}

	@Override
    public final void setColor(double r, double g, double b, double a) {
        color.setColor(r, g, b, a);
	}

	@Override
	public final void setColorRGB(int rgb) {
        color.setColorRGB(rgb);
	}

	@Override
	public final void setColorARGB(int argb) {
        color.setColorARGB(argb);
	}

    @Override
    public void setBlendMode(BlendMode blendMode) {
        Checks.checkNotNull(blendMode, "blendMode");

        this.blendMode = blendMode;
	}

	@Override
	public void setClipEnabled(boolean clip) {
        clipEnabled = clip;
	}

}
