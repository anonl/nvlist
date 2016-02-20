package nl.weeaboo.vn.scene.impl;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.core.impl.AlignUtil;
import nl.weeaboo.vn.math.IShape;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.math.MutableMatrix;
import nl.weeaboo.vn.math.Polygon;
import nl.weeaboo.vn.scene.ITransformable;

public abstract class Transformable extends VisualElement implements ITransformable {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private final ColorHelper color = new ColorHelper();

    private double x, y;
	private boolean clipEnabled = true;
	private BlendMode blendMode = BlendMode.DEFAULT;

    private double rotation;
    private double scaleX = 1, scaleY = 1;
    private double imageAlignX, imageAlignY;
    private Matrix baseTransform = Matrix.identityMatrix();

    private transient IShape _collisionShape;
    private transient Rect2D _visualBounds;
    private transient Matrix _transform;

	public Transformable() {
	}

    protected Matrix createTransform() {
        MutableMatrix m = baseTransform.mutableCopy();
        m.translate(getX(), getY());
        m.rotate(getRotation());
        m.scale(getScaleX(), getScaleY());
        return m.immutableCopy();
    }

    protected IShape createCollisionShape() {
        Rect2D vb = getUntransformedVisualBounds();
        vb = vb.translatedCopy(getAlignOffsetX(), getAlignOffsetY());

        return Polygon.transformedRect(getTransform(), vb);
    }

    protected Rect2D createVisualBounds() {
        Rect2D vb = getUntransformedVisualBounds();
        vb = vb.translatedCopy(getAlignOffsetX(), getAlignOffsetY());

        Polygon polygon = Polygon.transformedRect(getTransform(), vb);

        return polygon.getBoundingRect();
    }

    protected void invalidateTransform() {
        _transform = null;

        invalidateBounds();
    }

    protected void invalidateBounds() {
        _visualBounds = null;

        invalidateCollisionShape();
    }

    protected void invalidateCollisionShape() {
        _collisionShape = null;
    }

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
        return x;
    }

	@Override
    public final double getY() {
        return y;
    }

	@Override
    public final double getWidth() {
        return getUnscaledWidth() * getScaleX();
    }

	@Override
    public final double getHeight() {
        return getUnscaledHeight() * getScaleY();
    }

	@Override
    public final Rect2D getVisualBounds() {
        if (_visualBounds == null) {
            _visualBounds = createVisualBounds();
        }
        return _visualBounds;
	}

    protected abstract double getUnscaledWidth();
    protected abstract double getUnscaledHeight();

    public Rect2D getUntransformedVisualBounds() {
        return Rect2D.of(0, 0, getUnscaledWidth(), getUnscaledHeight());
    }

    @Override
    public boolean contains(double cx, double cy) {
        IShape p = getCollisionShape();
        if (p == null) {
            return false;
        }
        return p.contains(cx, cy);
    }

    @Override
    public final Matrix getTransform() {
        if (_transform == null) {
            _transform = createTransform();
        }
        return _transform;
    }

    protected final IShape getCollisionShape() {
        if (_collisionShape == null) {
            _collisionShape = createCollisionShape();
        }
        return _collisionShape;
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

    @Override
    public double getRotation() {
        return rotation;
    }

    @Override
    public double getScaleX() {
        return scaleX;
    }

    @Override
    public double getScaleY() {
        return scaleY;
    }

    @Override
    public double getAlignX() {
        return imageAlignX;
    }

    @Override
    public double getAlignY() {
        return imageAlignY;
    }

    @Override
    public final double getAlignOffsetX() {
        return AlignUtil.getAlignOffset(getUnscaledWidth(), getAlignX());
    }

    @Override
    public final double getAlignOffsetY() {
        return AlignUtil.getAlignOffset(getUnscaledHeight(), getAlignY());
    }

    @Override
    public Matrix getBaseTransform() {
        return baseTransform;
    }

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
        if (this.x != x || this.y != y) {
            this.x = x;
            this.y = y;

            invalidateTransform();
        }
    }

    @Override
    public final void setSize(double w, double h) {
        setUnscaledSize(w / getScaleX(), h / getScaleY());
    }

    protected abstract void setUnscaledSize(double w, double h);

    @Override
	public void setBounds(double x, double y, double w, double h) {
        setAlign(0, 0);
        setRotation(0);
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

    public void setBaseTransform(MutableMatrix transform) {
        setBaseTransform(transform.immutableCopy());
    }

    @Override
    public void setBaseTransform(Matrix transform) {
        if (!baseTransform.equals(transform)) {
            baseTransform = transform;

            invalidateTransform();
        }
    }

    @Override
    public final void rotate(double r) {
        setRotation(getRotation() + r);
    }

    @Override
    public void setRotation(double rot) {
        if (rotation != rot) {
            rotation = rot;

            invalidateTransform();
        }
    }

    @Override
    public final void scale(double s) {
        scale(s, s);
    }

    @Override
    public final void scale(double sx, double sy) {
        setScale(getScaleX() * sx, getScaleY() * sy);
    }

    @Override
    public final void setScale(double s) {
        setScale(s, s);
    }

    @Override
    public void setScale(double sx, double sy) {
        Checks.checkRange(sx, "sx");
        Checks.checkRange(sy, "sy");

        if (scaleX != sx || scaleY != sy) {
            scaleX = sx;
            scaleY = sy;

            invalidateTransform();
        }
    }

    @Override
    public void setAlign(double xFrac, double yFrac) {
        if (imageAlignX != xFrac || imageAlignY != yFrac) {
            imageAlignX = xFrac;
            imageAlignY = yFrac;

            invalidateBounds();
        }
    }

}
