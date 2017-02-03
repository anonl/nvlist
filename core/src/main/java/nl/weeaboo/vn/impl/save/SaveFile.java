package nl.weeaboo.vn.impl.save;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.save.ISaveFile;
import nl.weeaboo.vn.save.ISaveFileHeader;

public final class SaveFile implements ISaveFile {

    private final int slot;
    private final ISaveFileHeader header;
    private IScreenshot screenshot;

    public SaveFile(int slot, ISaveFileHeader header, IScreenshot screenshot) {
        this.slot = slot;
        this.header = Checks.checkNotNull(header);
        this.screenshot = Checks.checkNotNull(screenshot);
    }

    @Override
    public int getSlot() {
        return slot;
    }

    @Override
    public ISaveFileHeader getHeader() {
        return header;
    }

    @Override
    public IScreenshot getThumbnail() {
        return screenshot;
    }

    @Override
    public IScreenshot getThumbnail(int maxW, int maxH) {
        // TODO: Implement this
        return screenshot;
    }

}
