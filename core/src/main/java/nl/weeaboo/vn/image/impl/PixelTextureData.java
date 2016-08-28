package nl.weeaboo.vn.image.impl;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import nl.weeaboo.common.Checks;
import nl.weeaboo.gdx.graphics.GdxTextureUtil;
import nl.weeaboo.io.CustomSerializable;

@CustomSerializable
public final class PixelTextureData implements IGdxTextureData {

    private static final long serialVersionUID = 1L;

    private transient Pixmap pixels;
    private boolean destroyed;

    private PixelTextureData(Pixmap pixels) {
        this.pixels = Checks.checkNotNull(pixels);
    }

    public static PixelTextureData fromPixmap(Pixmap pixmap) {
        return new PixelTextureData(pixmap);
    }

    public static PixelTextureData fromImageFile(byte[] encoded, int off, int len) {
        return new PixelTextureData(new Pixmap(encoded, off, len));
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        PngUtil.writePng(pixels, out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        pixels = PngUtil.readPng(in);
    }

    public Pixmap getPixels() {
        return pixels;
    }

    @Override
    public TextureRegion toTextureRegion() {
        Texture texture = new Texture(getPixels());
        return GdxTextureUtil.getDefaultRegion(texture);
    }

    @Override
    public int getWidth() {
        return pixels.getWidth();
    }

    @Override
    public int getHeight() {
        return pixels.getHeight();
    }

    @Override
    public void destroy() {
        if (!destroyed) {
            destroyed = true;

            pixels.dispose();
        }
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public final void dispose() {
        destroy();
    }

}
