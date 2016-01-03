package nl.weeaboo.vn.scene.impl;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.impl.AlignUtil;
import nl.weeaboo.vn.math.IShape;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.math.MutableMatrix;
import nl.weeaboo.vn.math.Polygon;
import nl.weeaboo.vn.scene.ITransformable;

public abstract class Transformable extends Drawable implements ITransformable {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

	private double rotation;
	private double imageAlignX, imageAlignY;
	private Matrix baseTransform = Matrix.identityMatrix();

	private transient IShape collisionShape;
    private transient Rect2D _visualBounds;

	public Transformable() {
	}

	//Functions
	@Override
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

	protected void invalidateCollisionShape() {
		collisionShape = null;
	}

	@Override
    protected void invalidateTransform() {
		super.invalidateTransform();

		invalidateBounds();
	}

	protected void invalidateBounds() {
        _visualBounds = null;

		invalidateCollisionShape();
	}

	//Getters
    protected double getUnscaledWidth() {
        return getWidth();
    }

    protected double getUnscaledHeight() {
        return getHeight();
    }

    public Rect2D getUntransformedVisualBounds() {
        return Rect2D.of(0, 0, getUnscaledWidth(), getUnscaledHeight());
    }

	@Override
	public double getRotation() {
		return rotation;
	}

	@Override
	public double getScaleX() {
		double uw = getUnscaledWidth();
		double w = getWidth();
		return (uw != 0 ? w / uw : 1);
	}

	@Override
	public double getScaleY() {
		double uh = getUnscaledHeight();
		double h = getHeight();
		return (uh != 0 ? h / uh : 1);
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
    public final Rect2D getVisualBounds() {
        if (_visualBounds == null) {
            _visualBounds = createVisualBounds();
		}
        return _visualBounds;
	}

	@Override
	public boolean contains(double cx, double cy) {
		IShape p = getCollisionShape();
		if (p == null) {
			return false;
		}
		return p.contains(cx, cy);
	}

	protected final IShape getCollisionShape() {
		if (collisionShape == null) {
			collisionShape = createCollisionShape();
		}
		return collisionShape;
	}

	//Setters
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

		super.setSize(sx * getUnscaledWidth(), sy * getUnscaledHeight());
	}

	@Override
	public void setBounds(double x, double y, double w, double h) {
		setAlign(0, 0);
		setRotation(0);
		setPos(x, y);
		setSize(w, h);
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
