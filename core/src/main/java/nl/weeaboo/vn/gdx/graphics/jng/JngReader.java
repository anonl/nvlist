package nl.weeaboo.vn.gdx.graphics.jng;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

import com.badlogic.gdx.graphics.Pixmap;
import com.google.common.base.Preconditions;

import nl.weeaboo.vn.gdx.graphics.PixmapUtil;
import nl.weeaboo.vn.gdx.graphics.jng.JngHeader.AlphaSettings;

public final class JngReader {

    private static final byte[] IEND = {
        0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, (byte)0xAE, 0x42, 0x60, (byte)0x82
    };

    private JngReader() {
    }

    /**
     * Reads a .jng image.
     *
     * @throws IOException If the image can't be read.
     * @see #read(InputStream)
     */
    public static Pixmap read(InputStream in) throws IOException {
        DataInput din = new DataInputStream(in);
        return read(din);
    }

    /**
     * Reads a .jng image.
     *
     * @throws IOException If the image can't be read.
     * @see #read(InputStream)
     */
    public static Pixmap read(DataInput din) throws IOException {
        List<byte[]> colorBytes = new ArrayList<>(); // Forms a valid JPEG file

        JngAlphaType alphaType = null;
        List<byte[]> alphaBytes = new ArrayList<>(); // Forms a valid JPEG/PNG file

        JngHeader header = JngHeader.read(din);

        int jdatIndex = 0;
        while (true) {
            int chunkLength = din.readInt();
            int type = din.readInt();
            if (type == JngConstants.CHUNK_IEND) {
                break;
            } else if (type == JngConstants.CHUNK_JDAT) {
                if (jdatIndex == 0) {
                    // Only read the first jpeg image
                    readChunks(colorBytes, din, chunkLength);
                    din.readInt();
                } else {
                    forceSkip(din, chunkLength + 4);
                }
            } else if (type == JngConstants.CHUNK_IDAT) {
                if (alphaType != JngAlphaType.PNG) {
                    alphaType = JngAlphaType.PNG;
                    alphaBytes.clear();
                }

                readChunks(alphaBytes, din, chunkLength);
                din.readInt(); // CRC
            } else if (type == JngConstants.CHUNK_JDAA) {
                if (alphaType != JngAlphaType.JPEG) {
                    alphaType = JngAlphaType.JPEG;
                    alphaBytes.clear();
                }

                readChunks(alphaBytes, din, chunkLength);
                din.readInt(); // CRC
            } else if (type == JngConstants.CHUNK_JSEP) {
                jdatIndex++;
                forceSkip(din, chunkLength + 4);
            } else {
                // Skip chunk
                forceSkip(din, chunkLength + 4);
            }
        }

        // Read color data
        Pixmap result;
        {
            byte[] colorBytesMerged = merge(colorBytes);
            result = new Pixmap(colorBytesMerged, 0, colorBytesMerged.length);
            result = PixmapUtil.convert(result, Pixmap.Format.RGBA8888, true);
        }

        // Read and apply alpha mask if it exists
        if (alphaType != null && !alphaBytes.isEmpty()) {
            final int iw = result.getWidth();
            final int ih = result.getHeight();

            Pixmap alpha;
            {
                byte[] alphaBytesMerged = mergeAlpha(header, alphaType, alphaBytes);
                alpha = new Pixmap(alphaBytesMerged, 0, alphaBytesMerged.length);
            }

            // Merge alpha into result
            ByteBuffer colorPixels = result.getPixels();
            ByteBuffer alphaPixels = alpha.getPixels();
            for (int n = 0, size = iw * ih; n < size; n++) {
                colorPixels.position(colorPixels.position() + 3); // RGB
                colorPixels.put(alphaPixels.get()); // A
            }
            colorPixels.rewind();
        }

        return result;
    }

    private static void readChunks(List<byte[]> out, DataInput din, int len) throws IOException {
        int read = 0;
        while (read < len) {
            byte[] chunk = new byte[Math.min(len - read, 32 << 10)];
            din.readFully(chunk, 0, chunk.length);
            read += chunk.length;
            out.add(chunk);
        }
    }

    private static byte[] merge(List<byte[]> c) {
        int count = 0;
        for (byte[] b : c) {
            count += b.length;
        }

        byte[] array = new byte[count];
        int t = 0;
        for (byte[] b : c) {
            System.arraycopy(b, 0, array, t, b.length);
            t += b.length;
        }
        c.clear();
        return array;
    }

    protected static byte[] mergeAlpha(JngHeader header, JngAlphaType type, List<byte[]> c) {
        Preconditions.checkArgument(type != null, "Invalid alpha type for this method: " + type);

        if (type != JngAlphaType.PNG) {
            return merge(c);
        }

        int count = 0;
        for (byte[] b : c) {
            count += b.length;
        }

        byte[] headerBytes = createPngHeaderForAlpha(header);

        ByteBuffer buf = ByteBuffer.allocate(headerBytes.length + (8 + count + 4) + IEND.length);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.put(headerBytes);

        buf.putInt(count);
        buf.putInt(JngConstants.CHUNK_IDAT);
        CRC32 crc = new CRC32();
        crc.update(buf.array(), buf.position() - 4, 4);
        for (byte[] b : c) {
            crc.update(b);
            buf.put(b);
        }
        buf.putInt((int)crc.getValue());
        buf.put(IEND);

        return buf.array();
    }

    private static byte[] createPngHeaderForAlpha(JngHeader hdr) {
        final byte[] MAGIC = JngConstants.PNG_MAGIC;

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
        buf.put((byte)alpha.compressionMethod);
        buf.put((byte)alpha.filterMethod);
        buf.put((byte)alpha.interlaceMethod);

        CRC32 crc = new CRC32();
        crc.update(buf.array(), MAGIC.length + 4, 17);
        buf.putInt((int)crc.getValue());

        return buf.array();
    }

    private static void forceSkip(DataInput in, int toSkip) throws IOException {
        int skipped = 0;
        while (skipped < toSkip) {
            int s = in.skipBytes(toSkip - skipped);
            if (s <= 0) {
                break;
            }
            skipped += s;
        }
        for (int n = skipped; n < toSkip; n++) {
            in.readByte();
        }
    }

}
