package nl.weeaboo.vn.impl.image;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.badlogic.gdx.graphics.Pixmap;

import nl.weeaboo.vn.gdx.graphics.PngUtil;

final class PixelTextureDataIO {

    private PixelTextureDataIO() {
    }

    public static void serialize(Pixmap pixmap, OutputStream out) throws IOException {
        byte[] bytes = PngUtil.encodePng(pixmap);

        DataOutputStream dout = new DataOutputStream(out);
        dout.writeInt(bytes.length);
        out.write(bytes, 0, bytes.length);
    }

    public static Pixmap deserialize(InputStream in) throws IOException {
        DataInputStream din = new DataInputStream(in);
        int len = din.readInt();
        byte[] bytes = new byte[len];
        din.readFully(bytes);

        return new Pixmap(bytes, 0, len);
    }

}
