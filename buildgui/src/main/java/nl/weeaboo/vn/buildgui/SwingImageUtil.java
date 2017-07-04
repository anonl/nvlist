package nl.weeaboo.vn.buildgui;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.imageio.ImageIO;

import com.google.common.collect.Lists;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IFileSystem;

final class SwingImageUtil {

    private SwingImageUtil() {
    }

    /**
     * Returns scaled version of an image.
     * @param sizes The list of images sizes ({@code size x size}) to scale to.
     */
    public static List<BufferedImage> getScaledImages(BufferedImage original, int... sizes) {
        List<BufferedImage> result = Lists.newArrayList();

        int[] sortedSizes = sizes.clone();
        Arrays.sort(sortedSizes);

        // Use the results of a previous scaling step to obtain better-looking results.
        // Scaling from 256x256 -> 64x64 -> 32x32 provides better results than scaling 256 -> 32 directly.
        BufferedImage current = original;
        for (int n = sortedSizes.length - 1; n >= 0; n--) {
            int size = sortedSizes[n];
            current = scaledImage(current, size, size);
            result.add(current);
        }
        return result;
    }

    /**
     * Returns a scaled version of an image, returning the original image if it was already the correct size.
     */
    public static BufferedImage scaledImage(BufferedImage original, int newWidth, int newHeight) {
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

    public static BufferedImage readImage(IFileSystem fileSystem, String path) throws IOException {
        byte[] iconBytes = FileSystemUtil.readBytes(fileSystem, FilePath.of(path));
        return ImageIO.read(new ByteArrayInputStream(iconBytes));
    }
}
