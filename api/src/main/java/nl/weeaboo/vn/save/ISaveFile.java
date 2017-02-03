package nl.weeaboo.vn.save;

import nl.weeaboo.vn.image.IScreenshot;

public interface ISaveFile {

    /**
     * @return The save file header.
     */
    ISaveFileHeader getHeader();

    /**
     * @return The save slot that this file is stored in.
     */
    int getSlot();

    /**
     * @return The thumbnail stored with the save file. If no thumbnail was stored, a dummy image is returned.
     */
    IScreenshot getThumbnail();

    /**
     * Like {@code #getThumbnail()}, but limits the size of the returned image to the specified dimensions.
     *
     * @see #getThumbnail()
     */
    IScreenshot getThumbnail(int maxW, int maxH);

}
