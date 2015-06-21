package nl.weeaboo.vn.core.impl;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.vn.core.ITransformablePart;
import nl.weeaboo.vn.math.IShape;
import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.math.MutableMatrix;
import nl.weeaboo.vn.math.Polygon;

public class TransformablePart extends DrawablePart implements ITransformablePart {

	private static final long serialVersionUID = CoreImpl.serialVersionUID;

	private double rotation;
	private double imageAlignX, imageAlignY;
	private double unscaledWidth, unscaledHeight;
	private Matrix baseTransform = Matrix.identityMatrix();

	private transient IShape collisionShape;
	private transient Rect2D _bounds;

	public TransformablePart() {
	}

	//Functions
	@Override
	protected Matrix createTransform() {
		MutableMatrix m = baseTransform.mutableCopy();
		m.translate(getX(), getY());
		m.scale(getScaleX(), getScaleY());
		m.rotate(getRotation());
		return m.immutableCopy();
	}

	protected IShape createCollisionShape() {
		Matrix transform = getTransform();
		double dx = getAlignOffsetX();
		double dy = getAlignOffsetY();
		if (dx != 0 || dy != 0) {
			MutableMatrix mm = transform.mutableCopy();
			mm.translate(dx, dy);
			transform = mm.immutableCopy();
		}
		return Polygon.rotatedRect(transform, 0, 0, getUnscaledWidth(), getUnscaledHeight());
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
		_bounds = null;

		invalidateCollisionShape();
	}

	//Getters
	@Override
	public double getUnscaledWidth() {
		return unscaledWidth;
	}

	@Override
	public double getUnscaledHeight() {
		return unscaledHeight;
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
	public Rect2D getBounds() {
		if (_bounds == null) {
			float xa = (float)getAlignOffsetX();
			float xb = xa + (float)getUnscaledWidth();
			float ya = (float)getAlignOffsetY();
			float yb = ya + (float)getUnscaledHeight();

			Matrix transform = getTransform();
			float[] coords = new float[] {xa, ya, xb, ya, xa, yb, xb, yb};
			transform.transform(coords, 0, coords.length);

			xa = Float.POSITIVE_INFINITY;
			xb = Float.NEGATIVE_INFINITY;
			ya = Float.POSITIVE_INFINITY;
			yb = Float.NEGATIVE_INFINITY;
			for (int n = 0; n < coords.length; n+=2) {
				xa = Math.min(xa, coords[n  ]);
				xb = Math.max(xb, coords[n  ]);
				ya = Math.min(ya, coords[n+1]);
				yb = Math.max(yb, coords[n+1]);
			}

			double w = xb-xa;
			double h = yb-ya;
			_bounds = Rect2D.of(xa, ya, Double.isNaN(w) ? 0 : w, Double.isNaN(h) ? 0 : h);
		}
		return _bounds;
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

			markChanged();
			invalidateTransform();
		}
	}

	@Override
	public void setRotation(double rot) {
		if (rotation != rot) {
			rotation = rot;

			markChanged();
			invalidateTransform();
		}
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

	public void setUnscaledSize(double w, double h) {
		if (unscaledWidth != w || unscaledHeight != h) {
			unscaledWidth = w;
			unscaledHeight = h;
			markChanged();
			invalidateBounds();
		}
	}

	@Override
	public void setBounds(double x, double y, double w, double h) {
		setAlign(0, 0);
		setRotation(0);
		setPos(x, y);
		setSize(w, h);
	}

	@Override
	public void setSize(double w, double h) {
		setUnscaledSize(w, h); //Needed to make setSize() function like DrawablePart's
		super.setSize(w, h);
	}

	@Override
	public void setAlign(double xFrac, double yFrac) {
		if (imageAlignX != xFrac || imageAlignY != yFrac) {
			imageAlignX = xFrac;
			imageAlignY = yFrac;

			markChanged();
			invalidateBounds();
		}
	}

}
