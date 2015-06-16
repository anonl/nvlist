package nl.weeaboo.vn.save;

import nl.weeaboo.vn.image.IScreenshot;

public interface ISaveFile {

    public ISaveFileHeader getHeader();

    public int getSlot();

    public IScreenshot getScreenshot();
    public IScreenshot getScreenshot(int maxW, int maxH);

}
