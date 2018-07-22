package nl.weeaboo.vn.image;

import java.io.Serializable;

public interface IScreenshotBuffer extends Serializable {

    /**
     * Adds a screenshot to the buffer.
     *
     * @param ss The screenshot object to fill with the pixel data later.
     * @param clip Set to {@code false} to ignore the layer bounds while taking the screenshot.
     */
    void add(IWritableScreenshot ss, boolean clip);

    /**
     * @return {@code true} if no screenshots are currently buffered.
     */
    boolean isEmpty();

}
