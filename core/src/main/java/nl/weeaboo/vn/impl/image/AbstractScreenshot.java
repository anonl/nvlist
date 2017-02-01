package nl.weeaboo.vn.impl.image;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.image.ITextureData;
import nl.weeaboo.vn.impl.render.AsyncRenderTask;

@CustomSerializable
public abstract class AbstractScreenshot extends AsyncRenderTask implements IScreenshot {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;

    private final short z;
    private final boolean isVolatile;

    private transient ITextureData pixels;
    private Dim screenSize = Dim.EMPTY;

    protected AbstractScreenshot(short z, boolean isVolatile) {
        this.z = z;
        this.isVolatile = isVolatile;

        if (isVolatile) {
            // Volatile implies transient
            isTransient = true;
        }
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        if (!isTransient && !isVolatile) {
            serializePixels(out, pixels);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        if (!isTransient && !isVolatile) {
            pixels = deserializePixels(in);
        }
    }

    protected void serializePixels(ObjectOutputStream out, ITextureData pixels) throws IOException {
        out.writeObject(pixels);
    }

    protected ITextureData deserializePixels(ObjectInputStream in) throws IOException, ClassNotFoundException {
        return (ITextureData)in.readObject();
    }

    @Override
    public short getZ() {
        return z;
    }

    @Override
    public void markTransient() {
        isTransient = true;
    }

    @Deprecated
    @Override
    public void makeTransient() {
        markTransient();
    }

    @Override
    public boolean isAvailable() {
        return !isFailed() && pixels != null;
    }

    @Override
    public final boolean isVolatile() {
        return isVolatile;
    }

    @Override
    public ITextureData getPixels() {
        return pixels;
    }

    @Override
    public Dim getScreenSize() {
        return screenSize;
    }

    protected void setPixels(ITextureData texData, Dim screenSize) {
        this.pixels = Checks.checkNotNull(texData);
        this.screenSize = Checks.checkNotNull(screenSize);
    }

}
