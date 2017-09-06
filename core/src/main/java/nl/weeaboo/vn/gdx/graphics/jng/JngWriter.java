package nl.weeaboo.vn.gdx.graphics.jng;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.zip.CRC32;

import javax.imageio.IIOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.gdx.graphics.jng.JngHeader.AlphaSettings;
import nl.weeaboo.vn.gdx.graphics.jng.JngHeader.ColorSettings;

public final class JngWriter {

    private static final Logger LOG = LoggerFactory.getLogger(JngWriter.class);

    private Dim size;

    private byte[] colorBytes;
    private int colorComponents;
    private int colorSampleDepth;

    private byte[] alphaBytes;
    private JngAlphaType alphaType;

    public JngWriter() {
        resetImageState();
    }

    /**
     * Reset all image-specific state.
     */
    public final void resetImageState() {
        size = Dim.EMPTY;

        colorBytes = null;
        colorComponents = 0;
        colorSampleDepth = 8;

        alphaBytes = null;
        alphaType = JngAlphaType.PNG;
    }

    private JngColorType getOutputColorType() {
        if (alphaBytes == null) {
            if (colorComponents == 1) {
                return JngColorType.GRAY;
            } else {
                return JngColorType.COLOR;
            }
        } else {
            if (colorComponents == 1) {
                return JngColorType.GRAY_ALPHA;
            } else {
                return JngColorType.COLOR_ALPHA;
            }
        }
    }

    public void setColorInput(byte[] data) throws IOException {
        Checks.checkArgument(JpegHelper.isJpeg(data), "Color input must be a JPEG file");

        DataInputStream din = new DataInputStream(new ByteArrayInputStream(data));

        byte[] magic = new byte[JpegHelper.JPEG_MAGIC.length];
        din.readFully(magic);
        if (!JpegHelper.isJpeg(magic, 0, magic.length)) {
            throw new JngParseException("Invalig JPEG magic: " + JngInputUtil.toByteString(magic));
        }

        while (true) {
            byte preMarkerByte = din.readByte();
            if (preMarkerByte != (byte)0xFF) {
                throw new IIOException(String.format("Marker not preceeded by 0xFF: 0x%02x", preMarkerByte));
            }

            int marker = din.readUnsignedByte();

            int length = din.readUnsignedShort();
            if (marker >= 0xc0 && marker <= 0xcf && marker != 0xC4 && marker != 0xC8) {
                colorSampleDepth = din.readUnsignedByte();
                int h = din.readUnsignedShort();
                int w = din.readUnsignedShort();
                size = Dim.of(w, h);
                colorComponents = din.readUnsignedByte();
                break;
            } else {
                JngInputUtil.forceSkip(din, length - 2); // Skip segment
            }
        }

        colorBytes = data;
    }

    public void setAlphaInput(byte[] data) throws IOException {
        if (colorBytes == null) {
            throw new IllegalStateException("Must set color input before alpha");
        }

        if (PngHelper.isPng(data)) {
            ByteBuffer buf = ByteBuffer.wrap(data);
            buf.order(ByteOrder.BIG_ENDIAN);
            buf.position(buf.position() + 16); // Skip until width
            int width = buf.getInt();
            int height = buf.getInt();
            Dim alphaSize = Dim.of(width, height);

            Checks.checkArgument(alphaSize.equals(size),
                    "Alpha image size (" + alphaSize + ") != color size (" + size + ")");

            int bitDepth = buf.get() & 0xFF;
            if (bitDepth > 8) {
                LOG.debug("Alpha input with bitDepth={} found, result image may use a lower bit depth",
                        bitDepth);
            }

            int colorType = buf.get() & 0xFF;
            Checks.checkArgument(colorType == PngColorType.GRAYSCALE.toInt(),
                    "Non-grayscale color type: " + colorType);

            alphaBytes = PngHelper.readIDAT(new ByteArrayInputStream(data));
            alphaType = JngAlphaType.PNG;
        } else if (JpegHelper.isJpeg(data)) {
            alphaBytes = data;
            alphaType = JngAlphaType.JPEG;
        } else {
            throw new IllegalArgumentException("Alpha input was an invalid file type (must be PNG or JPEG)");
        }
    }

    public void write(OutputStream out) throws IOException {
        write(out instanceof DataOutput ? (DataOutput)out : new DataOutputStream(out));
    }

    public void write(DataOutput dout) throws IOException {
        ColorSettings color = new ColorSettings();
        color.colorType = getOutputColorType();
        color.sampleDepth = colorSampleDepth;

        AlphaSettings alpha = new AlphaSettings();
        alpha.compressionMethod = alphaType;

        JngHeader header = new JngHeader(size, color, alpha);
        header.write(dout);

        writeChunk(dout, JngConstants.CHUNK_JDAT, colorBytes);

        if (header.hasAlpha()) {
            switch (alphaType) {
            case PNG: {
                writeChunk(dout, JngConstants.CHUNK_IDAT, alphaBytes);
            } break;
            case JPEG: {
                writeChunk(dout, JngConstants.CHUNK_JDAA, alphaBytes);
            } break;
            default:
                throw new IllegalStateException("Invalid alphaType: " + alphaType);
            }
        }

        dout.write(PngHelper.IEND);
    }

    private static void writeChunk(DataOutput dout, int chunkId, byte[] data) throws IOException {
        dout.writeInt(data.length);
        dout.writeInt(chunkId);

        CRC32 crc = new CRC32();
        crc.update(data, 0, data.length);
        dout.write(data, 0, data.length);
        dout.writeInt((int)crc.getValue());
    }

}
