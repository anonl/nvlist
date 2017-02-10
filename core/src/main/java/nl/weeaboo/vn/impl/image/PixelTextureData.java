package nl.weeaboo.vn.impl.image;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import nl.weeaboo.common.Checks;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.gdx.graphics.GdxTextureUtil;

@CustomSerializable
public final class PixelTextureData implements IGdxTextureData {

    private static final long serialVersionUID = 1L;

    private transient Pixmap pixels;
    private boolean destroyed;

    private PixelTextureData(Pixmap pixels) {
        this.pixels = Checks.checkNotNull(pixels);
    }

    /**
     * Creates a new instance from a pixmap using premultiplied alpha.
     */
    public static PixelTextureData fromPremultipliedPixmap(Pixmap pixmap) {
        return new PixelTextureData(pixmap);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        PixelTextureDataIO.serialize(pixels, out);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        pixels = PixelTextureDataIO.deserialize(in);
    }

    /**
     * @return The backing pixmap for this texture data object. Uses premultiplied alpha.
     */
    public Pixmap getPixels() {
        return pixels;
    }

    @Override
    public TextureRegion toTextureRegion() {
        Texture texture = new Texture(getPixels());
        GdxTextureUtil.setDefaultTextureParams(texture);
        return GdxTextureUtil.newGdxTextureRegion(texture);
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
