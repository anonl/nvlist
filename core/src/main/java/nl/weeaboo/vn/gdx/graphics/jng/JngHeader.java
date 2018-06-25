package nl.weeaboo.vn.gdx.graphics.jng;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

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

    public boolean hasAlpha() {
        return color.colorType.hasAlpha();
    }

    public static JngHeader read(DataInput din) throws IOException, JngParseException {
        byte[] magicBytes = new byte[JngConstants.JNG_MAGIC.length];
        din.readFully(magicBytes);
        if (!Arrays.equals(JngConstants.JNG_MAGIC, magicBytes)) {
            throw new JngParseException(JngInputUtil.toByteString(magicBytes));
        }

        int jhdrBytes = din.readInt();
        if (jhdrBytes != 16) {
            throw new JngParseException("Invalid JHDR length: " + jhdrBytes);
        }

        int jhdrMagic = din.readInt();
        if (jhdrMagic != JngConstants.CHUNK_JHDR) {
            throw new JngParseException(String.format("Invalid JHDR magic: 0x%08x", jhdrMagic));
        }

        int w = din.readInt();
        int h = din.readInt();
        final Dim size = Dim.of(w, h);

        final ColorSettings color = new ColorSettings();
        color.colorType = JngColorType.fromInt(din.readUnsignedByte());
        color.sampleDepth = din.readUnsignedByte();
        color.compressionMethod = din.readUnsignedByte();
        color.interlaceMethod = din.readUnsignedByte();

        final AlphaSettings alpha = new AlphaSettings();
        alpha.sampleDepth = din.readUnsignedByte();
        alpha.compressionMethod = JngAlphaType.fromInt(din.readUnsignedByte());
        alpha.filterMethod = din.readUnsignedByte();
        alpha.interlaceMethod = din.readUnsignedByte();

        /*int crc = */din.readInt();

        return new JngHeader(size, color, alpha);
    }

    public void write(DataOutput dout) throws IOException {
        dout.write(JngConstants.JNG_MAGIC);

        ByteBuffer jhdr = ByteBuffer.allocate(16);
        jhdr.order(ByteOrder.BIG_ENDIAN);
        jhdr.putInt(size.w);
        jhdr.putInt(size.h);
        jhdr.put((byte)color.colorType.toInt());
        jhdr.put((byte)color.sampleDepth);
        jhdr.put((byte)color.compressionMethod);
        jhdr.put((byte)color.interlaceMethod);
        jhdr.put((byte)alpha.sampleDepth);
        jhdr.put((byte)alpha.compressionMethod.toInt());
        jhdr.put((byte)alpha.filterMethod);
        jhdr.put((byte)alpha.interlaceMethod);

        JngWriter.writeChunk(dout, JngConstants.CHUNK_JHDR, jhdr.array());
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
        public int interlaceMethod = 8;

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

        public JngAlphaType compressionMethod = JngAlphaType.PNG;

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