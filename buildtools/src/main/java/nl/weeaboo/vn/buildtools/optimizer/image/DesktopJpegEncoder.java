package nl.weeaboo.vn.buildtools.optimizer.image;

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
import com.google.common.collect.Iterators;

final class DesktopJpegEncoder {

    public byte[] encodeJpeg(Pixmap pixmap, float quality) throws IOException {
        BufferedImage image = BufferedImageHelper.toBufferedImage(pixmap, BufferedImage.TYPE_3BYTE_BGR);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageWriter writer = Iterators.get(ImageIO.getImageWritersByFormatName("jpg"), 0);
        try {
            ImageOutputStream ios = ImageIO.createImageOutputStream(out);
            writer.setOutput(ios);

            JPEGImageWriteParam iwparam = new JPEGImageWriteParam(Locale.ROOT);
            iwparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT) ;
            iwparam.setCompressionQuality(quality);
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

}