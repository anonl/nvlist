package nl.weeaboo.vn.save;

import nl.weeaboo.vn.image.IScreenshot;

public interface ISaveFile {

    ISaveFileHeader getHeader();

    int getSlot();

    IScreenshot getScreenshot();
    IScreenshot getScreenshot(int maxW, int maxH);

}
