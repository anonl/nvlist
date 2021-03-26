package nl.weeaboo.vn.impl.image;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.vn.image.ITexture;

public class TextureStub implements ITexture {

    private static final long serialVersionUID = 1L;

    private final int width;
    private final int height;
    private final double scaleX;
    private final double scaleY;
    private final Area2D uv;

    public TextureStub(int w, int h) {
        width = w;
        height = h;

        scaleX = scaleY = 1.0;

        uv = ITexture.DEFAULT_UV;
    }

    @Override
    public double getWidth() {
        return getPixelWidth() * getScaleX();
    }

    @Override
    public double getHeight() {
        return getPixelHeight() * getScaleY();
    }

    @Override
    public int getPixelWidth() {
        return width;
    }

    @Override
    public int getPixelHeight() {
        return height;
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
    public Area2D getUV() {
        return uv;
    }

}
