package nl.weeaboo.vn.image.impl;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Rect2D;
import nl.weeaboo.entity.Part;
import nl.weeaboo.vn.core.AlignUtil;
import nl.weeaboo.vn.core.impl.TransformablePart;
import nl.weeaboo.vn.image.IImagePart;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.math.Vec2;

public class ImagePart extends Part implements IImagePart {

	private static final long serialVersionUID = ImageImpl.serialVersionUID;

	private final TransformablePart drawable;

	private ITexture texture;
	private Area2D uv = ITexture.DEFAULT_UV;
	private boolean changed;

	public ImagePart(TransformablePart drawable) {
		this.drawable = drawable;
	}

	//Functions
	protected void markChanged() {
		changed = true;
	}

	public boolean consumeChanged() {
		boolean result = changed;
		changed = false;
		return result;
	}

	private void updateUnscaledSize() {
		if (texture != null) {
			drawable.setUnscaledSize(texture.getWidth(), texture.getHeight());
		} else {
			drawable.setUnscaledSize(0, 0);
		}
	}

	//Getters
	@Override
	public ITexture getTexture() {
		return texture;
	}

	@Override
	public Area2D getUV() {
		return uv;
	}

	//Setters
	@Override
	public void setTexture(ITexture i) {
		setTexture(i, 7);
	}

	@Override
	public void setTexture(ITexture i, int anchor) {
		double alignX = 0, alignY = 0;
		if (texture != i) {
			double w0 = drawable.getUnscaledWidth();
			double h0 = drawable.getUnscaledHeight();
			double w1 = (i != null ? i.getWidth() : 0);
			double h1 = (i != null ? i.getHeight() : 0);
			Rect2D rect = AlignUtil.getAlignedBounds(w0, h0, drawable.getAlignX(), drawable.getAlignY());
			Vec2 align = AlignUtil.alignSubRect(rect, w1, h1, anchor);
			alignX = align.x;
			alignY = align.y;
		}

		setTexture(i, alignX, alignY);
	}

	@Override
	public void setTexture(ITexture i, double imageAlignX, double imageAlignY) {
		if (texture != i || drawable.getAlignX() != imageAlignX || drawable.getAlignY() != imageAlignY) {
            double sx = drawable.getScaleX();
            double sy = drawable.getScaleY();

            texture = i;

			markChanged();
			updateUnscaledSize();
			drawable.setScale(sx, sy); // Maintain relative scale, but not the exact size
			drawable.setAlign(imageAlignX, imageAlignY);
		}
	}

	@Override
	public final void setUV(double w, double h) {
		setUV(0, 0, w, h);
	}

	@Override
	public final void setUV(double x, double y, double w, double h) {
		setUV(Area2D.of(x, y, w, h));
	}

	@Override
	public void setUV(Area2D uv) {
		if (!uv.equals(this.uv)) {
			this.uv = uv;

			markChanged();
		}
	}

}
