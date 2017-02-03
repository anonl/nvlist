package nl.weeaboo.vn.gdx.graphics;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;

import com.badlogic.gdx.graphics.Pixmap;
import com.google.common.base.Preconditions;

final class JngReader {

    private enum JNGAlphaType {
        JPEG, PNG
    }

    private static final byte[] JNG_MAGIC = {
        (byte)0x8B, 0x4A, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A
    };

    private static final byte[] IEND = {
        0x00, 0x00, 0x00, 0x00, 0x49, 0x45, 0x4E, 0x44, (byte)0xAE, 0x42, 0x60, (byte)0x82
    };

    private static final int CHUNK_JHDR = 0x4A484452;
    private static final int CHUNK_JDAT = 0x4A444154;
    private static final int CHUNK_JDAA = 0x4A444141;
    private static final int CHUNK_JSEP = 0x4A534550;
    private static final int CHUNK_IDAT = 0x49444154;
    private static final int CHUNK_IEND = 0x49454E44;

    public static Pixmap read(InputStream in) throws IOException {
        DataInput din = new DataInputStream(in);
        return read(din);
    }

    public static Pixmap read(DataInput din) throws IOException {
        List<byte[]> colorBytes = new ArrayList<>(); // Forms a valid JPEG file

        JNGAlphaType alphaType = null;
        List<byte[]> alphaBytes = new ArrayList<>(); // Forms a valid JPEG/PNG file

        JNGHeader header = JNGHeader.fromDataInput(din);

        int jdatIndex = 0;
        while (true) {
            int chunkLength = din.readInt();
            int type = din.readInt();
            if (type == CHUNK_IEND) {
                break;
            } else if (type == CHUNK_JDAT) {
                if (jdatIndex == 0) {
                    // Only read the first jpeg image
                    readChunks(colorBytes, din, chunkLength);
                    din.readInt();
                } else {
                    forceSkip(din, chunkLength + 4);
                }
            } else if (type == CHUNK_IDAT) {
                if (alphaType != JNGAlphaType.PNG) {
                    alphaType = JNGAlphaType.PNG;
                    alphaBytes.clear();
                }

                readChunks(alphaBytes, din, chunkLength);
                din.readInt(); //CRC
            } else if (type == CHUNK_JDAA) {
                if (alphaType != JNGAlphaType.JPEG) {
                    alphaType = JNGAlphaType.JPEG;
                    alphaBytes.clear();
                }

                readChunks(alphaBytes, din, chunkLength);
                din.readInt(); //CRC
            } else if (type == CHUNK_JSEP) {
                jdatIndex++;
                forceSkip(din, chunkLength + 4);
            } else {
                // Skip chunk
                forceSkip(din, chunkLength + 4);
            }
        }

        //Read color data
        Pixmap result;
        {
            byte[] colorBytesMerged = merge(colorBytes);
            result = new Pixmap(colorBytesMerged, 0, colorBytesMerged.length);
            result = PixmapUtil.convert(result, Pixmap.Format.RGBA8888, true);
        }

        //Read and apply alpha mask if it exists
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

    protected static byte[] mergeAlpha(JNGHeader header, JNGAlphaType type, List<byte[]> c) {
        Preconditions.checkArgument(type != null, "Invalid alpha type for this method: " + type);

        if (type != JNGAlphaType.PNG) {
            return merge(c);
        }

        int count = 0;
        for (byte[] b : c) {
            count += b.length;
        }

        byte[] headerBytes = createPNGHeaderForAlpha(header);

        ByteBuffer buf = ByteBuffer.allocate(headerBytes.length + (8 + count + 4) + IEND.length);
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.put(headerBytes);

        buf.putInt(count);
        buf.putInt(CHUNK_IDAT);
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

    private static byte[] createPNGHeaderForAlpha(JNGHeader hdr) {
        final byte[] MAGIC = {(byte)0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
        final int CHUNK_IHDR = 0x49484452;

        ByteBuffer buf = ByteBuffer.allocate(MAGIC.length + (8 + 13 + 4));
        buf.order(ByteOrder.BIG_ENDIAN);
        buf.put(MAGIC);
        buf.putInt(13);
        buf.putInt(CHUNK_IHDR);
        buf.putInt(hdr.width);
        buf.putInt(hdr.height);
        buf.put((byte)hdr.alphaSampleDepth);
        buf.put((byte)0); //colorType is always grayscale
        buf.put((byte)hdr.alphaCompressionMethod);
        buf.put((byte)hdr.alphaFilterMethod);
        buf.put((byte)hdr.alphaInterlaceMethod);

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

    private static class JNGHeader {

        public final int width;
        public final int height;
        public final int alphaSampleDepth;
        public final int alphaCompressionMethod;
        public final int alphaFilterMethod;
        public final int alphaInterlaceMethod;

        public JNGHeader(int w, int h, int alphaSampleDepth, int alphaCompressionMethod,
                int alphaFilterMethod, int alphaInterlaceMethod) {

            this.width = w;
            this.height = h;
            this.alphaSampleDepth = alphaSampleDepth;
            this.alphaCompressionMethod = alphaCompressionMethod;
            this.alphaFilterMethod = alphaFilterMethod;
            this.alphaInterlaceMethod = alphaInterlaceMethod;
        }

        public static JNGHeader fromDataInput(DataInput din) throws IOException {
            byte[] magicBytes = new byte[JNG_MAGIC.length];
            din.readFully(magicBytes);
            if (!Arrays.equals(JNG_MAGIC, magicBytes)) {
                StringBuilder sb = new StringBuilder("Invalid magic value: ");
                for (byte b : magicBytes) {
                    sb.append(String.format("%02x ", b & 0xFF));
                }
                throw new IOException(sb.toString());
            }

            int jhdrBytes = din.readInt();
            if (jhdrBytes != 16) {
                throw new IOException("Invalid JHDR length: " + jhdrBytes);
            }
            int jhdrMagic = din.readInt();
            if (jhdrMagic != CHUNK_JHDR) {
                throw new IOException(String.format("Invalid JHDR magic: 0x%08x", jhdrMagic));
            }

            final int w = din.readInt();
            final int h = din.readInt();
            /*int colorType = */din.readUnsignedByte();
            /*int imageSampleDepth = */din.readUnsignedByte();
            /*int imageCompressionMethod = */din.readUnsignedByte();
            /*int imageInterlaceMethod = */din.readUnsignedByte();
            final int alphaSampleDepth = din.readUnsignedByte();
            final int alphaCompressionMethod = din.readUnsignedByte();
            final int alphaFilterMethod = din.readUnsignedByte();
            final int alphaInterlaceMethod = din.readUnsignedByte();
            /*int crc = */din.readInt();

            return new JNGHeader(w, h, alphaSampleDepth, alphaCompressionMethod, alphaFilterMethod,
                    alphaInterlaceMethod);
        }

    }

}
