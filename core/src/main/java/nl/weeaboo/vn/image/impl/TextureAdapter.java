package nl.weeaboo.vn.image.impl;

import java.util.Locale;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Checks;
import nl.weeaboo.gdx.res.IResource;
import nl.weeaboo.vn.image.ITexture;

public class TextureAdapter implements ITexture {

	private static final long serialVersionUID = ImageImpl.serialVersionUID;

	private IResource<TextureRegion> res;
	private double scaleX, scaleY;

	public TextureAdapter(IResource<TextureRegion> region, double scaleX, double scaleY) {
	    this.res = Checks.checkNotNull(region);
	    this.scaleX = scaleX;
	    this.scaleY = scaleY;
    }

	@Override
	public String toString() {
		return String.format(Locale.ROOT,
		        "TextureAdapter(%d, %.1fx%.1f)",
		        getHandle(), getWidth(), getHeight());
	}

	protected int getHandle() {
	    Texture tex = getTexture();
        return (tex != null ? tex.getTextureObjectHandle() : 0);
	}

	public Texture getTexture() {
	    TextureRegion tr = getTextureRegion();
        return (tr != null ? tr.getTexture() : null);
	}

	public TextureRegion getTextureRegion() {
		return res.get();
	}

    public TextureRegion getTextureRegion(Area2D uv) {
        if (uv.equals(ITexture.DEFAULT_UV)) {
            return getTextureRegion();
        }

        TextureRegion tr = getTextureRegion();

        float uspan = tr.getU2() - tr.getU();
        float u = tr.getU() + uspan * (float)uv.x;
        float u2 = u + uspan * (float)uv.w;

        float vspan = tr.getV2() - tr.getV();
        float v = tr.getU() + vspan * (float)uv.y;
        float v2 = v + vspan * (float)uv.h;

        return new TextureRegion(tr.getTexture(), u, v, u2, v2);
    }

	@Override
	public Area2D getUV() {
        TextureRegion tr = getTextureRegion();
        if (tr == null) {
            return ITexture.DEFAULT_UV;
        }

        float u1 = tr.getU();
        float u2 = tr.getU2();
        float v1 = tr.getV();
        float v2 = tr.getV2();
		return Area2D.of(u1, v1, u2-u1, v2-v1);
	}

	@Override
	public double getWidth() {
        return getPixelWidth() * scaleX;
	}

	@Override
	public double getHeight() {
        return getPixelHeight() * scaleY;
	}

    @Override
    public int getPixelWidth() {
        TextureRegion tr = getTextureRegion();
        return (tr != null ? tr.getRegionWidth() : 0);
    }

    @Override
    public int getPixelHeight() {
        TextureRegion tr = getTextureRegion();
        return (tr != null ? tr.getRegionHeight() : 0);
    }

	@Override
	public double getScaleX() {
		return scaleX;
	}

	@Override
	public double getScaleY() {
		return scaleY;
	}

	public void setTextureRegion(IResource<TextureRegion> tr, double scale) {
        setTextureRegion(tr, scale, scale);
    }
	public void setTextureRegion(IResource<TextureRegion> tr, double sx, double sy) {
		this.res = Checks.checkNotNull(tr);
		this.scaleX = sx;
		this.scaleY = sy;
	}

}