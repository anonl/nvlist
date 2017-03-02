package nl.weeaboo.vn.save;

import java.io.IOException;
import java.util.Collection;

import nl.weeaboo.vn.core.IModule;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.core.IProgressListener;

public interface ISaveModule extends IModule {

    /** Load persistent storage. */
    void loadPersistent();

    /** Save persistent storage. */
    void savePersistent();

    /**
     * Delete a save slot.
     *
     * @throws IOException If deletion fails.
     */
    void delete(int slot) throws IOException;

    /**
     * Load a save slot. Resumes script execution from the saved state.
     *
     * @throws IOException If an I/O error occurs while reading data from the save file.
     * @throws SaveFormatException If the stored save data is in an incompatible format.
     */
    void load(INovel novel, int slot, IProgressListener pl) throws SaveFormatException, IOException;

    /**
     * Saves the current state to the specified save slot.
     * @throws IOException If an I/O error occurs while writing data to the save file.
     */
    void save(INovel novel, int slot, ISaveParams params, IProgressListener pl) throws IOException;

    /**
     * Returns the index of a currently unused save slot.
     */
    int getNextFreeSlot();

    /**
     * @return {@code true} if the specified save slot contains saved data.
     */
    boolean getSaveExists(int slot);

    /**
     * Reads all metadata from a save file.
     *
     * @param slot The save slot to load.
     * @throws IOException If an I/O error occurs while reading data from the save file.
     * @throws SaveFormatException If the stored save data is in an incompatible format.
     * @see #getQuickSaveSlot(int)
     * @see #getAutoSaveSlot(int)
     * @see #readSaveHeader(int)
     */
    ISaveFile readSave(int slot) throws SaveFormatException, IOException;

    /**
     * Reads only the header of a save file.
     *
     * @param slot The save slot to load.
     * @throws IOException If an I/O error occurs while reading data from the save file.
     * @throws SaveFormatException If the stored save data is in an incompatible format.
     * @see #getQuickSaveSlot(int)
     * @see #getAutoSaveSlot(int)
     * @see #readSave(int)
     */
    ISaveFileHeader readSaveHeader(int slot) throws SaveFormatException, IOException;

    /**
     * @param slot The quicksave slot index in the range {@code (1, getNumQuickSaveSlots())}.
     * @return The general purpose save slot index.
     */
    int getQuickSaveSlot(int slot);

    /**
     * @param slot The autosave slot index in the range {@code (1, getNumAutoSaveSlots())}.
     * @return The general purpose save slot index.
     */
    int getAutoSaveSlot(int slot);

    /**
     * Returns the shared globals. These values are <em>shared</em> between all save slots.
     * <p>
     * A common use for shared globals is to store global unlock data such as route clear flags.
     */
    IStorage getSharedGlobals();

    /**
     * Fetches save file metadata for the save slots in the range {@code [fromSlot, fromSlot+numSlots)}. For empty save
     * slots, no information will be returned.
     */
    Collection<ISaveFile> getSaves(int fromSlot, int numSlots);

}
