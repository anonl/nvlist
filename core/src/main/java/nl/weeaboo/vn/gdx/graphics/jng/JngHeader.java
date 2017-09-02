package nl.weeaboo.vn.gdx.graphics.jng;

import java.io.DataInput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.zip.CRC32;

import nl.weeaboo.common.Dim;

final class JngHeader {

    public final Dim size;
    public final ColorSettings color;
    public final AlphaSettings alpha;

    public JngHeader(Dim size, ColorSettings color, AlphaSettings alpha) {
        this.size = size;
        this.color = color;
        this.alpha = alpha;
    }

    public static JngHeader fromDataInput(DataInput din) throws IOException {
        byte[] magicBytes = new byte[JngConstants.JNG_MAGIC.length];
        din.readFully(magicBytes);
        if (!Arrays.equals(JngConstants.JNG_MAGIC, magicBytes)) {
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
        if (jhdrMagic != JngConstants.CHUNK_JHDR) {
            throw new IOException(String.format("Invalid JHDR magic: 0x%08x", jhdrMagic));
        }

        final int w = din.readInt();
        final int h = din.readInt();
        int colorType = din.readUnsignedByte();
        int imageSampleDepth = din.readUnsignedByte();
        int imageCompressionMethod = din.readUnsignedByte();
        int imageInterlaceMethod = din.readUnsignedByte();
        final int alphaSampleDepth = din.readUnsignedByte();
        final int alphaCompressionMethod = din.readUnsignedByte();
        final int alphaFilterMethod = din.readUnsignedByte();
        final int alphaInterlaceMethod = din.readUnsignedByte();
        /*int crc = */din.readInt();

        return new JngHeader(w, h, alphaSampleDepth, alphaCompressionMethod, alphaFilterMethod,
                alphaInterlaceMethod);
    }

    public void write(DataOutput dout) throws IOException {
        dout.write(JngConstants.JNG_MAGIC);

        ByteBuffer jhdr = ByteBuffer.allocate(16);
        jhdr.order(ByteOrder.BIG_ENDIAN);
        jhdr.putInt(width);
        jhdr.putInt(height);
        jhdr.put((byte)colorType);
        jhdr.put((byte)imageSampleDepth);
        jhdr.put((byte)imageCompressionMethod);
        jhdr.put((byte)imageInterlaceMethod);
        jhdr.put((byte)alphaSampleDepth);
        jhdr.put((byte)alphaCompressionMethod);
        jhdr.put((byte)alphaFilterMethod);
        jhdr.put((byte)alphaInterlaceMethod);

        dout.writeInt(jhdr.limit());
        dout.writeInt(JngConstants.CHUNK_JHDR);
        dout.write(jhdr.array());

        CRC32 crc = new CRC32();
        crc.update(jhdr.array());
        dout.writeInt((int)crc.getValue());
    }

    public boolean hasAlpha() {
        return color.colorType.hasAlpha();
    }

    public static final class ColorSettings {

        public JngColorType colorType = JngColorType.COLOR_ALPHA;

        /**
         * Possible values:
         * <ul>
         * <li>8: 8-bit samples and quantization tables.
         * <li> 12: 12-bit samples and quantization tables.
         * <li> 20: 8-bit image followed by a 12-bit image.
         * </ul>
         */
        public int sampleDepth = 8;

        /**
         * Possible values:
         * <ul>
         * <li> 8: ISO-10918-1 Huffman-coded baseline JPEG
         * </ul>
         */
        public int compressionMethod = 8;

        /**
         * Possible values:
         * <ul>
         * <li> 0: Sequential JPEG, single scan.
         * <li> 8: Progressive JPEG
         * </ul>
         */
        public int interlaceMethod = 0;

    }

    public static final class AlphaSettings {

        /**
         * Possible values:
         * <ul>
         * <li> 0, 1, 2, 4, 8, or 16, if the Alpha compression method is 0 (PNG).
         * <li> 8, if the Alpha compression method is 8 (JNG).
         * </ul>
         */
        public int sampleDepth = 8;

        /**
         * Possible values:
         * <ul>
         * <li> 0: PNG grayscale IDAT format.
         * <li> 8: JNG 8-bit grayscale JDAA format.
         * </ul>
         */
        public int compressionMethod = 8;

        /**
         * Possible values:
         * <ul>
         * <li> 0: Adaptive PNG (see PNG spec) or not applicable (JPEG).
         * </ul>
         */
        public int filterMethod;

        /**
         * Possible values:
         * <ul>
         * <li> 0: Noninterlaced PNG or sequential single-scan JPEG.
         * </ul>
         */
        public int interlaceMethod;

    }

}