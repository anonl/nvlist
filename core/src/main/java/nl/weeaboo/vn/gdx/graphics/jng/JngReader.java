package nl.weeaboo.vn.gdx.graphics.jng;

import java.io.DataInput;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.google.common.base.Preconditions;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.gdx.graphics.PixmapUtil;

/**
 * Reads JNG image data.
 */
public final class JngReader {

    private final JngReaderOpts opts;

    private JngReader(JngReaderOpts opts) {
        this.opts = Checks.checkNotNull(opts);
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
     * @see #read(InputStream, JngReaderOpts)
     */
    public static Pixmap read(InputStream in, JngReaderOpts opts) throws IOException {
        return read(JngInputUtil.toDataInput(in), opts);
    }

    /**
     * Reads a .jng image.
     *
     * @throws IOException If the image can't be read.
     * @see #read(InputStream, JngReaderOpts)
     */
    public static Pixmap read(DataInput din, JngReaderOpts opts) throws IOException {
        return new JngReader(opts).read(din);
    }

    private Pixmap read(DataInput din) throws JngParseException, IOException {
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

        boolean inputHasAlpha = alphaType != null && !alphaBytes.isEmpty();

        // Read color data
        final Format resultFormat = getResultFormat(inputHasAlpha);
        Pixmap result;
        {
            byte[] colorBytesMerged = JngInputUtil.concatChunks(colorBytes);
            result = new Pixmap(new Gdx2DPixmap(colorBytesMerged, 0, colorBytesMerged.length,
                    Format.toGdx2DPixmapFormat(resultFormat)));
        }

        // Read and apply alpha mask if it exists
        if (inputHasAlpha && PixmapUtil.hasAlpha(resultFormat)) {
            Pixmap alpha;
            {
                byte[] alphaBytesMerged = mergeAlpha(header, alphaType, alphaBytes);
                alpha = new Pixmap(alphaBytesMerged, 0, alphaBytesMerged.length);
            }

            insertAlpha(result, alpha);
        }

        return result;
    }

    /**
     * Merge alpha into result.
     */
    static void insertAlpha(Pixmap result, Pixmap alpha) {
        switch (alpha.getFormat()) {
        case Alpha:
        case Intensity:
            break; // Allow alpha stored as either explicityly alpha, or just as a grayscale color image
        default:
            throw new IllegalArgumentException("Unsupported alpha format: " + alpha.getFormat());
        }

        ByteBuffer colorPixels = result.getPixels();
        ByteBuffer alphaPixels = alpha.getPixels();
        try {
            int size = result.getWidth() * result.getHeight();
            switch (result.getFormat()) {
            case RGBA8888:
                for (int n = 0; n < size; n++) {
                    colorPixels.position(colorPixels.position() + 3); // Skip RGB
                    colorPixels.put(alphaPixels.get()); // Overwrite A
                }
                break;
            case RGBA4444:
                // RGBA4444 is stored as shorts in native order (see Pixmap#getPixels)
                colorPixels.order(ByteOrder.nativeOrder());
                ShortBuffer colorPixelsShort = colorPixels.asShortBuffer();
                for (int n = 0; n < size; n++) {
                    int a = alphaPixels.get() & 0xFF;
                    a = (a + 7) >> 4; // 8 bpp -> 4 bpp

                    int rgba4 = (colorPixelsShort.get(n) & ~15) | a; // Replace alpha
                    colorPixelsShort.put(n, (short)rgba4);
                }
                break;
            default:
                throw new IllegalArgumentException("Unsupported result format: " + result.getFormat());
            }
        } finally {
            colorPixels.rewind();
            alphaPixels.rewind();
        }
    }

    private Format getResultFormat(boolean hasAlpha) {
        Format resultFormat = opts.resultFormat;
        if (resultFormat != null) {
            return resultFormat;
        }

        return (hasAlpha ? Format.RGBA8888 : Format.RGB888);
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

    private static byte[] mergeAlpha(JngHeader header, JngAlphaType type, List<byte[]> c) {
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
