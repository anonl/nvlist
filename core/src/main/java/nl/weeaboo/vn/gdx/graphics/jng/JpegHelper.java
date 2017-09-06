package nl.weeaboo.vn.gdx.graphics.jng;

final class JpegHelper {

    static final byte[] JPEG_MAGIC = {(byte)0xFF, (byte)0xD8};

    private JpegHelper() {
    }

    public static boolean isJpeg(byte[] bytes) {
        return isJpeg(bytes, 0, bytes.length);
    }

    public static boolean isJpeg(byte[] bytes, int offset, int length) {
        return JngInputUtil.startsWith(bytes, offset, length, JPEG_MAGIC);
    }

}
