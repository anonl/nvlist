package nl.weeaboo.vn.gdx.graphics.jng;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;

import nl.weeaboo.vn.gdx.graphics.PixmapLoader;
import nl.weeaboo.vn.gdx.graphics.PixmapUtil;
import nl.weeaboo.vn.gdx.graphics.PngUtil;
import nl.weeaboo.vn.gdx.graphics.jng.JngHeader.AlphaSettings;

final class PngHelper {

    /**
     * Byte pattern at the start of every PNG file.
     */
    static final byte[] PNG_MAGIC = {
        (byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    static final byte[] IEND = {
        0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, (byte)0xAE, 0x42, 0x60, (byte)0x82
    };

    private PngHelper() {
    }

    public static boolean isPng(byte[] data, int offset, int length) {
        return JngInputUtil.startsWith(data, offset, length, PNG_MAGIC);
    }

    public static byte[] readIDAT(InputStream in) throws IOException {
        DataInput din = JngInputUtil.toDataInput(in);
        JngInputUtil.forceSkip(din, 33); // Skip PNG magic and IHDR

        List<byte[]> chunks = new ArrayList<>();
        int bytesCount = 0;

        while (true) {
            int chunkLength = din.readInt();
            int type = din.readInt();
            if (type == JngConstants.CHUNK_IEND) {
                break;
            } else if (type == JngConstants.CHUNK_IDAT) {
                byte[] data = new byte[chunkLength];
                din.readFully(data);
                chunks.add(data);
                bytesCount += data.length;
                din.readInt(); // CRC
            } else {
                // Skip chunk
                JngInputUtil.forceSkip(din, chunkLength + 4);
            }
        }

        return JngInputUtil.concatChunks(chunks, bytesCount);
    }

    static byte[] createPngHeaderForAlpha(JngHeader hdr) {
        final byte[] MAGIC = PNG_MAGIC;

        ByteBuffer buf = ByteBuffer.allocate(MAGIC.length + (8 + 13 + 4));
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.put(MAGIC);
        buf.putInt(13);
        buf.putInt(JngConstants.CHUNK_IHDR);

        buf.putInt(hdr.size.w);
        buf.putInt(hdr.size.h);

        AlphaSettings alpha = hdr.alpha;
        buf.put((byte)alpha.sampleDepth);
        buf.put((byte)0); // colorType is always grayscale
        buf.put((byte)alpha.compressionMethod.toInt());
        buf.put((byte)alpha.filterMethod);
        buf.put((byte)alpha.interlaceMethod);

        CRC32 crc = new CRC32();
        crc.update(buf.array(), MAGIC.length + 4, 17);
        buf.putInt((int)crc.getValue());

        return buf.array();
    }

    public static byte[] toGrayscalePng(byte[] data) throws IOException {
        // TODO: Consider checking if the input is already a grayscale PNG

        Pixmap pixmap = PixmapLoader.load(data, 0, data.length);
        pixmap = PixmapUtil.convert(pixmap, Format.Intensity, true);

        byte[] pngData;
        try {
            pngData = PngUtil.encodePng(pixmap);
        } finally {
            pixmap.dispose();
        }
        return pngData;
    }
}
