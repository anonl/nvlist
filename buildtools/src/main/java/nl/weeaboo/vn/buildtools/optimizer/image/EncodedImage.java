package nl.weeaboo.vn.buildtools.optimizer.image;

import java.io.IOException;

import nl.weeaboo.io.Filenames;
import nl.weeaboo.vn.buildtools.file.IEncodedResource;
import nl.weeaboo.vn.image.desc.IImageDefinition;
import nl.weeaboo.vn.impl.image.desc.ImageDefinition;

/**
 * Encoded image resource.
 */
public final class EncodedImage implements IEncodedResource {

    private final IEncodedResource encodedImage;
    private ImageDefinition imageDefinition;
    private boolean hasAlpha = true;

    public EncodedImage(IEncodedResource encodedImage, ImageDefinition def) {
        this.encodedImage = encodedImage;
        this.imageDefinition = def;

        // Guess hasAlpha based on file-ext
        String ext = Filenames.getExtension(def.getFilename());
        if (ext.equalsIgnoreCase("jpg") || ext.equalsIgnoreCase("jpeg")) {
            hasAlpha = false;
        }
    }

    @Override
    public void dispose() {
        encodedImage.dispose();
    }

    @Override
    public byte[] readBytes() throws IOException {
        return encodedImage.readBytes();
    }

    /** Returns the {@link IImageDefinition} accompanying the image. */
    public ImageDefinition getDef() {
        return imageDefinition;
    }

    /**
     * @see #getDef()
     */
    public void setDef(ImageDefinition def) {
        imageDefinition = def;
    }

    /**
     * Returns {@code true} if the encoded image has an alpha channel.
     */
    public boolean hasAlpha() {
        return hasAlpha;
    }

    /**
     * @see #hasAlpha
     */
    public void setHasAlpha(boolean alpha) {
        hasAlpha = alpha;
    }

}
