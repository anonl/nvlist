package nl.weeaboo.vn.buildtools.optimizer.image;

import nl.weeaboo.vn.buildtools.optimizer.IOptimizerConfig;

/**
 * Default implementation of {@link IOptimizerConfig}.
 */
public final class ImageEncoderConfig implements IOptimizerConfig {

    private EImageEncoding encoding = EImageEncoding.JNG;

    /**
     * The file-type for encoded images.
     */
    public EImageEncoding getEncoding() {
        return encoding;
    }

    /**
     * @see #getEncoding()
     */
    public void setEncoding(EImageEncoding encoding) {
        this.encoding = encoding;
    }

    enum EImageEncoding {
        JNG;
    }

}
