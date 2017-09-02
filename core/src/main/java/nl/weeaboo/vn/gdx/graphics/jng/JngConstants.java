package nl.weeaboo.vn.gdx.graphics.jng;

final class JngConstants {

    /**
     * Byte pattern at the start of every JNG file.
     */
    static final byte[] JNG_MAGIC = { (byte)0x8B, 0x4A, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };

    // --- Chunk identifiers ---------------------------------------------------
    static final int CHUNK_JHDR = 0x4A484452;
    static final int CHUNK_JDAT = 0x4A444154;
    static final int CHUNK_JDAA = 0x4A444141;
    static final int CHUNK_JSEP = 0x4A534550;
    static final int CHUNK_IDAT = 0x49444154;
    static final int CHUNK_IEND = 0x49454E44;
    // -------------------------------------------------------------------------

    private JngConstants() {
    }

}
