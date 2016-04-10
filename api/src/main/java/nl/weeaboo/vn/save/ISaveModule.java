package nl.weeaboo.vn.save;

import java.io.IOException;

import nl.weeaboo.vn.core.IModule;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.core.IProgressListener;
import nl.weeaboo.vn.image.IScreenshot;

public interface ISaveModule extends IModule {

    void loadPersistent();

    void savePersistent();

    void delete(int slot) throws IOException;

    void load(INovel novel, int slot, IProgressListener pl) throws SaveFormatException, IOException;

    void save(INovel novel, int slot, ISaveParams params, IProgressListener pl)
            throws SaveFormatException, IOException;

    int getNextFreeSlot();

    boolean getSaveExists(int slot);

    /**
     * Reads the header of a save file.
     *
     * @param slot The save slot to load.
     * @see #getQuickSaveSlot(int)
     * @see #getAutoSaveSlot(int)
     */
    ISaveFileHeader readSaveHeader(int slot) throws SaveFormatException, IOException;

    IScreenshot readSaveThumbnail(int slot) throws SaveFormatException, IOException;

    /**
     * @param slot The quicksave slot index in the range {@code (1, 99)}.
     * @return The general purpose save slot index.
     */
    int getQuickSaveSlot(int slot);

    /**
     * @param slot The autosave slot index in the range {@code (1, 99)}.
     * @return The general purpose save slot index.
     */
    int getAutoSaveSlot(int slot);

    IStorage getGlobals();

    IStorage getSharedGlobals();

}
