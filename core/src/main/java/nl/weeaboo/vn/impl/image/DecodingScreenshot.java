package nl.weeaboo.vn.impl.image;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import nl.weeaboo.io.CustomSerializable;
import nl.weeaboo.vn.image.ITextureData;

@CustomSerializable
public abstract class DecodingScreenshot extends AbstractScreenshot {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;

    private transient byte[] data;
    private transient boolean isLoaded;

    public DecodingScreenshot(byte[] bytes) {
        super((short)0, false);

        if (bytes == null) {
            cancel();
        } else {
            data = bytes.clone();
        }
    }

    @Override
    public boolean isAvailable() {
        return !isFailed() && data != null;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();

        if (data != null && !isTransient()) {
            out.writeInt(data.length);
            out.write(data);
        } else {
            out.writeInt(-1);
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();

        int len = in.readInt();
        if (len >= 0) {
            data = new byte[len];
            in.readFully(data);
        }
    }

    protected abstract void tryLoad(byte[] data);

    @Override
    public void cancel() {
        data = null; //Allows garbage collection of data and makes isAvailable() return false

        super.cancel();
    }

    @Override
    public ITextureData getPixels() {
        if (!isLoaded) {
            tryLoad(data);
            isLoaded = true;
        }
        return super.getPixels();
    }

}
