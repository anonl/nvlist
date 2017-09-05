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
import com.google.common.base.Preconditions;

import nl.weeaboo.vn.gdx.graphics.PixmapUtil;

public final class JngReader {

    private JngReader() {
    }

    /**
     * @return {@code true} if the given data has a JNG header.
     */
    public static boolean isJng(byte[] bytes, int offset, int length) {
        return JngInputUtil.startsWith(bytes, offset, length, JngConstants.JNG_MAGIC);
    }

    /**
     * Reads a .jng image.
     *
     * @throws IOException If the image can't be read.
     * @see #read(InputStream)
     */
    public static Pixmap read(InputStream in) throws IOException {
        return read(JngInputUtil.toDataInput(in));
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
                    JngInputUtil.forceSkip(din, chunkLength + 4);
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
                JngInputUtil.forceSkip(din, chunkLength + 4);
            } else {
                // Skip chunk
                JngInputUtil.forceSkip(din, chunkLength + 4);
            }
        }

        // Read color data
        Pixmap result;
        {
            byte[] colorBytesMerged = JngInputUtil.concatChunks(colorBytes);
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

    protected static byte[] mergeAlpha(JngHeader header, JngAlphaType type, List<byte[]> c) {
        Preconditions.checkArgument(type != null, "Invalid alpha type for this method: " + type);

        if (type != JngAlphaType.PNG) {
            return JngInputUtil.concatChunks(c);
        }

        int count = 0;
        for (byte[] b : c) {
            count += b.length;
        }

        byte[] headerBytes = PngHelper.createPngHeaderForAlpha(header);

        ByteBuffer buf = ByteBuffer.allocate(headerBytes.length + (8 + count + 4) + PngHelper.IEND.length);
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
        buf.put(PngHelper.IEND);

        return buf.array();
    }


}
