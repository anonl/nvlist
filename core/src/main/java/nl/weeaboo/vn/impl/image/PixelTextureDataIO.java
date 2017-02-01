package nl.weeaboo.vn.impl.image;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;

final class PixelTextureDataIO {

    private PixelTextureDataIO() {
    }

    public static void serialize(Pixmap pixmap, OutputStream out) throws IOException {
        PixmapIO.PNG encoder = new PixmapIO.PNG();
        encoder.setFlipY(false);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {
            encoder.write(bout, pixmap);
        } finally {
            bout.close();
        }

        DataOutputStream dout = new DataOutputStream(out);
        dout.writeInt(bout.size());
        bout.writeTo(out);
    }

    public static Pixmap deserialize(InputStream in) throws IOException {
        DataInputStream din = new DataInputStream(in);
        int len = din.readInt();
        byte[] bytes = new byte[len];
        din.readFully(bytes);

        return new Pixmap(bytes, 0, len);
    }

}
