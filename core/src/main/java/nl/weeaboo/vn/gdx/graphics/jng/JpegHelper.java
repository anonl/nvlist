package nl.weeaboo.vn.gdx.graphics.jng;

import java.io.IOException;

import com.badlogic.gdx.graphics.Pixmap;

import nl.weeaboo.vn.gdx.graphics.PixmapLoader;

final class JpegHelper {

    static final byte[] JPEG_MAGIC = {(byte)0xFF, (byte)0xD8};

    private JpegHelper() {
    }

    public static boolean isJpeg(byte[] bytes, int offset, int length) {
        return JngInputUtil.startsWith(bytes, offset, length, JPEG_MAGIC);
    }

    public static byte[] convertToJpeg(byte[] data) throws IOException {
        if (JpegHelper.isJpeg(data, 0, data.length)) {
            // The data is already a JPEG file
            return data;
        }

        Pixmap pixmap = PixmapLoader.load(data, 0, data.length);
    }

}
