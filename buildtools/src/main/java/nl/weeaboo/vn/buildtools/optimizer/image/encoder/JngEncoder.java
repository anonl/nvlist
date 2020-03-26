package nl.weeaboo.vn.buildtools.optimizer.image.encoder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.google.common.annotations.VisibleForTesting;

import nl.weeaboo.io.Filenames;
import nl.weeaboo.vn.buildtools.file.EncodedResource;
import nl.weeaboo.vn.buildtools.file.IEncodedResource;
import nl.weeaboo.vn.buildtools.optimizer.image.EncodedImage;
import nl.weeaboo.vn.buildtools.optimizer.image.ImageWithDef;
import nl.weeaboo.vn.gdx.graphics.PixmapUtil;
import nl.weeaboo.vn.gdx.graphics.jng.JngWriter;
import nl.weeaboo.vn.impl.image.desc.ImageDefinitionBuilder;

/**
 * JNG image file encoder.
 */
public final class JngEncoder implements IImageEncoder {

    private final JngEncoderParams params = new JngEncoderParams();

    private final IJpegEncoder jpegEncoder;
    private final IPngEncoder pngEncoder;

    public JngEncoder() {
        this(new DesktopJpegEncoder(), new DesktopPngEncoder());
    }

    public JngEncoder(IJpegEncoder jpegEncoder, IPngEncoder pngEncoder) {
        this.jpegEncoder = jpegEncoder;
        this.pngEncoder = pngEncoder;
    }

    @Override
    public EncodedImage encode(ImageWithDef image) throws IOException {
        Pixmap pixmap = image.getPixmap();

        JngWriter writer = new JngWriter();

        // Set RGB color
        byte[] colorData = jpegEncoder.encode(pixmap, newJpegParams(params.getJpegQuality()));
        writer.setColorInput(colorData);

        // Set alpha
        Pixmap alphaPixmap = extractAlpha(pixmap);
        try {
            if (!PixmapUtil.hasTranslucentPixel(alphaPixmap)) {
                // If the image has no alpha, encode as JPEG instead
                return encodeAsJpeg(image, colorData);
            } else {
                byte[] jpegAlpha = null;
                if (params.isAllowLossyAlpha()) {
                    jpegAlpha = jpegEncoder.encode(alphaPixmap, newJpegParams(params.getJpegAlphaQuality()));
                }

                byte[] pngAlpha = pngEncoder.encode(alphaPixmap, new PngEncoderParams());

                // Pick smallest alpha encoding
                if (jpegAlpha != null && jpegAlpha.length < pngAlpha.length) {
                    writer.setAlphaInput(jpegAlpha);
                } else {
                    writer.setAlphaInput(pngAlpha);
                }
            }
        } finally {
            alphaPixmap.dispose();
        }

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        writer.write(bout);
        IEncodedResource encoded = EncodedResource.fromBytes(bout.toByteArray());

        ImageDefinitionBuilder imageDef = image.getDef().builder();
        imageDef.setFilename(Filenames.replaceExt(imageDef.getFilename(), "jng"));
        return new EncodedImage(encoded, imageDef.build());
    }

    private EncodedImage encodeAsJpeg(ImageWithDef image, byte[] jpegData) {
        IEncodedResource encoded = EncodedResource.fromBytes(jpegData);

        ImageDefinitionBuilder imageDef = image.getDef().builder();
        imageDef.setFilename(Filenames.replaceExt(imageDef.getFilename(), "jpg"));
        return new EncodedImage(encoded, imageDef.build());
    }

    private JpegEncoderParams newJpegParams(float jpegQuality) {
        JpegEncoderParams result = new JpegEncoderParams();
        result.setQuality(jpegQuality);
        return result;
    }

    @VisibleForTesting
    static Pixmap extractAlpha(Pixmap src) {
        Pixmap dst = PixmapUtil.newUninitializedPixmap(src.getWidth(), src.getHeight(), Format.Alpha);

        final ByteBuffer srcPixels = src.getPixels();
        final ByteBuffer dstPixels = dst.getPixels();

        switch (src.getFormat()) {
        case RGB565:
        case RGB888:
        case Intensity:
            // Format doesn't have alpha -- return an opaque image
            dst.setColor(Color.WHITE);
            dst.fill();
            break;
        case Alpha:
            // Format only has alpha, so just return a copy of the input
            PixmapUtil.copy(src, dst);
            break;
        case LuminanceAlpha: {
            final int limit = src.getWidth() * src.getHeight() * 2;
            for (int n = 0; n < limit; n += 2) {
                int a = srcPixels.get(n + 1) & 0xFF;
                dstPixels.put((byte)a);
            }
        } break;
        case RGBA4444: {
            // RGBA4444 is stored as shorts in native order (see Pixmap#getPixels)
            srcPixels.order(ByteOrder.nativeOrder());
            ShortBuffer pixels = srcPixels.asShortBuffer();

            final int limit = src.getWidth() * src.getHeight();
            for (int n = 0; n < limit; n++) {
                int a = pixels.get(n) & 0xF;
                a = (a << 4) | a; // 4 bpp -> 8 bpp
                dstPixels.put((byte)a);
            }
        } break;
        case RGBA8888: {
            final int limit = src.getWidth() * src.getHeight() * 4;
            for (int n = 0; n < limit; n += 4) {
                int a = srcPixels.get(n + 3) & 0xFF;
                dstPixels.put((byte)a);
            }
        } break;
        default:
            throw new IllegalArgumentException("Pixmap with unsupported format: " + src.getFormat());
        }

        dstPixels.rewind();

        return dst;
    }

}
