package nl.weeaboo.vn.image;

import nl.weeaboo.common.Dim;

public interface IWritableScreenshot extends IScreenshot {

    /**
     * Sets the pixel data for this screenshot.
     *
     * @param texData Pixel data.
     * @param screenSize The screen resolution at the time that this screenshot was taken. This is necessary in order to
     *        support changing screen resolutions. If we store the screen resolution with the pixel data, we can
     *        maintain the relative size of the texture compared to the screen.
     */
    void setPixels(ITextureData texData, Dim screenSize);

}
