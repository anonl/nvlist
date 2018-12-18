package nl.weeaboo.vn.buildtools.optimizer.image.encoder;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.google.common.collect.Iterators;

import nl.weeaboo.vn.buildtools.file.EncodedResource;
import nl.weeaboo.vn.buildtools.file.IEncodedResource;
import nl.weeaboo.vn.buildtools.optimizer.image.BufferedImageHelper;
import nl.weeaboo.vn.buildtools.optimizer.image.EncodedImage;
import nl.weeaboo.vn.buildtools.optimizer.image.ImageWithDef;

final class DesktopJpegEncoder implements IJpegEncoder {

    @Override
    public byte[] encode(Pixmap pixmap, JpegEncoderParams params) throws IOException {
        BufferedImage image;
        switch (pixmap.getFormat()) {
        case Alpha:
        case Intensity:
            image = BufferedImageHelper.toBufferedImage(pixmap);
            break;
        default:
            // Flatten any alpha in the source image by requesting the image type for RGB888
            image = BufferedImageHelper.toBufferedImage(pixmap, BufferedImageHelper.toBufferedImageType(Format.RGB888));
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageWriter writer = Iterators.get(ImageIO.getImageWritersByFormatName("jpg"), 0);
        try {
            ImageOutputStream ios = ImageIO.createImageOutputStream(out);
            writer.setOutput(ios);

            JPEGImageWriteParam iwparam = new JPEGImageWriteParam(Locale.ROOT);
            iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            iwparam.setCompressionQuality(params.getQuality());
            iwparam.setOptimizeHuffmanTables(true);

            // Write the image
            writer.write(null, new IIOImage(image, null, null), iwparam);

            // Cleanup
            ios.flush();
        } finally {
            writer.dispose();
        }

        image.flush();

        return out.toByteArray();
    }

    @Override
    public EncodedImage encode(ImageWithDef image) throws IOException {
        byte[] bytes = encode(image.getPixmap(), new JpegEncoderParams());

        IEncodedResource encodedImage = EncodedResource.fromBytes(bytes);
        return new EncodedImage(encodedImage, image.getDef());
    }

}