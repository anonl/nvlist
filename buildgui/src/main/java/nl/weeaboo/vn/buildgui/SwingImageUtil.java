package nl.weeaboo.vn.buildgui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.List;

import com.google.common.collect.Lists;

final class SwingImageUtil {

    private SwingImageUtil() {
    }

    /**
     * Returns scaled version of an image.
     * @param sizes The list of images sizes ({@code size x size}) to scale to.
     */
    public static List<BufferedImage> getScaledImages(BufferedImage original, int... sizes) {
        List<BufferedImage> result = Lists.newArrayList();
        for (int size : sizes) {
            result.add(scaledImage(original, size, size));
        }
        return result;
    }

    /**
     * Returns a scaled version of an image, returning the original image if it was already the correct size.
     */
    private static BufferedImage scaledImage(BufferedImage original, int newWidth, int newHeight) {
        int newType = BufferedImage.TYPE_INT_ARGB;
        if (!original.getColorModel().hasAlpha()) {
            newType = BufferedImage.TYPE_INT_BGR;
        }

        BufferedImage result = new BufferedImage(newWidth, newHeight, newType);
        Graphics2D g = result.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(original, 0, 0, newWidth, newHeight, null);
        g.dispose();
        return result;
    }
}
