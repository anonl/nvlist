package nl.weeaboo.vn.save;

import java.io.IOException;
import java.io.Serializable;

import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.core.IProgressListener;
import nl.weeaboo.vn.image.IScreenshot;

public interface ISaveModule extends Serializable {

    public void loadPersistent();
    public void savePersistent();

    public void delete(int slot) throws IOException;
    public void load(INovel novel, int slot, IProgressListener pl) throws SaveFormatException, IOException;

    public void save(INovel novel, int slot, ISaveParams params, IProgressListener pl)
            throws SaveFormatException, IOException;

    public int getNextFreeSlot();

    public boolean getSaveExists(int slot);

    /**
     * Reads the header of a save file.
     *
     * @param slot The save slot to load.
     * @see #getQuickSaveSlot(int)
     * @see #getAutoSaveSlot(int)
     */
    public ISaveFileHeader readSaveHeader(int slot) throws SaveFormatException, IOException;

    public IScreenshot readSaveThumbnail(int slot) throws SaveFormatException, IOException;

    /**
     * @param slot The quicksave slot index in the range {@code (1, 99)}.
     * @return The general purpose save slot index.
     */
    public int getQuickSaveSlot(int slot);

    /**
     * @param slot The autosave slot index in the range {@code (1, 99)}.
     * @return The general purpose save slot index.
     */
    public int getAutoSaveSlot(int slot);

    public IStorage getGlobals();

    public IStorage getSharedGlobals();

}
