package nl.weeaboo.vn.impl.script.lib;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Filter;

import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.LuaBoolean;
import nl.weeaboo.lua2.vm.LuaConstants;
import nl.weeaboo.lua2.vm.LuaInteger;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.LuaThread;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.gdx.graphics.GdxScreenshotUtil;
import nl.weeaboo.vn.gdx.graphics.PixmapUtil;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.impl.core.ContextUtil;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;
import nl.weeaboo.vn.impl.save.SaveParams;
import nl.weeaboo.vn.impl.script.lua.ILuaStorage;
import nl.weeaboo.vn.impl.script.lua.LuaConvertUtil;
import nl.weeaboo.vn.impl.script.lua.LuaStorage;
import nl.weeaboo.vn.save.ISaveFile;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.save.IStorage;
import nl.weeaboo.vn.save.ThumbnailInfo;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;

/**
 * Lua "Save" library.
 */
public final class SaveLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(SaveLib.class);

    private final StaticRef<INovel> novelRef = StaticEnvironment.NOVEL;
    private final IEnvironment env;

    public SaveLib(IEnvironment env) {
        super("Save");

        this.env = env;
    }

    /**
     * @param args
     *        <ol>
     *        <li>(optional) start index
     *        <li>(optional) max result count
     *        </ol>
     * @return All used save slots in the range {@code [start, start + maxResultCount)}.
     */
    @ScriptFunction
    public Varargs getSaves(Varargs args) {
        int offset = args.optint(1, 0);
        int maxResults = args.optint(2, 10);

        ISaveModule saveModule = env.getSaveModule();
        Collection<ISaveFile> saves = saveModule.getSaves(offset, maxResults);

        LuaTable table = new LuaTable(saves.size(), 0);
        for (ISaveFile saveInfo : saves) {
            table.rawset(saveInfo.getSlot(), LuajavaLib.toUserdata(saveInfo, ISaveFile.class));
        }
        return table;
    }

    /**
     * @param args
     *        <ol>
     *        <li>save slot (int)
     *        <li>(optional) userdata table
     *        <li>(optional) screenshot table (screenshot, width, height)
     *        </ol>
     * @throws ScriptException If the input parameters are invalid.
     */
    @ScriptFunction
    public Varargs save(Varargs args) throws ScriptException {
        int slot = args.checkint(1);
        IStorage userData = LuaConvertUtil.toStorage(args.opttable(2, new LuaTable()));

        // Gather required params
        ISaveModule saveModule = env.getSaveModule();
        INovel novel = novelRef.get();
        SaveParams saveParams = new SaveParams();
        saveParams.setUserData(userData);

        // Add screenshot
        handleScreenshotParam(args.arg(3), saveParams);

        // Save
        final LuaRunState lrs = LuaRunState.getCurrent();
        final LuaThread thread = lrs.getRunningThread();
        Varargs result = thread.yield(LuaConstants.NONE);
        try {
            saveModule.save(novel, slot, saveParams);
        } catch (IOException e) {
            throw new ScriptException("Error saving to slot " + slot, e);
        }
        return result;
    }

    private static void handleScreenshotParam(LuaValue ssTable, SaveParams saveParams) {
        if (ssTable.isnil()) {
            return;
        }

        IScreenshot screenshot = ssTable.get("screenshot").checkuserdata(IScreenshot.class);
        Dim targetSize = Dim.of(ssTable.get("width").checkint(), ssTable.get("height").checkint());

        Pixmap original = GdxScreenshotUtil.borrowPixels(screenshot);
        if (original == null) {
            LOG.warn("Screenshot pixels are missing: {}", screenshot);
            return;
        }

        byte[] pngBytes;
        Pixmap resized = PixmapUtil.resizedCopy(original, targetSize, Filter.BiLinear);
        try {
            try {
                pngBytes = PixmapUtil.encodePng(resized);
            } catch (IOException e) {
                LOG.warn("Error encoding thumbnail for save slot", e);
                return;
            }
        } finally {
            resized.dispose();
        }

        ThumbnailInfo thumbnailInfo = new ThumbnailInfo(FilePath.of("thumbnail.png"), targetSize);
        saveParams.setThumbnail(thumbnailInfo, pngBytes);
    }

    /**
     * @param args
     *        <ol>
     *        <li>save slot (int)
     *        </ol>
     * @throws ScriptException If a load error occurs.
     */
    @ScriptFunction
    public Varargs load(Varargs args) throws ScriptException {
        int slot = args.checkint(1);
        ISaveModule saveModule = env.getSaveModule();
        INovel novel = novelRef.get();

        ContextUtil.getCurrentContext().destroy();
        try {
            saveModule.load(novel, slot);
        } catch (IOException e) {
            throw new ScriptException("Error loading save slot: " + slot, e);
        }
        return LuaConstants.NONE;
    }

    /**
     * @param args
     *        <ol>
     *        <li>save slot index
     *        </ol>
     * @return {@code true} if the save slot exists, {@code false} otherwise.
     */
    @ScriptFunction
    public Varargs getSaveExists(Varargs args) {
        int slot = args.checkint(1);
        ISaveModule saveModule = env.getSaveModule();

        return LuaBoolean.valueOf(saveModule.getSaveExists(slot));
    }

    /**
     * @param args Not used.
     * @return The index of a free save slot.
     */
    @ScriptFunction
    public Varargs getNextFreeSlot(Varargs args) {
        ISaveModule saveModule = env.getSaveModule();
        return LuaInteger.valueOf(saveModule.getNextFreeSlot());
    }

    /**
     * Deletes a save file.
     *
     * @param args
     *        <ol>
     *        <li>save slot index
     *        </ol>
     * @throws ScriptException If deletion fails.
     */
    @ScriptFunction
    public Varargs delete(Varargs args) throws ScriptException {
        int slot = args.checkint(1);
        ISaveModule saveModule = env.getSaveModule();

        try {
            saveModule.delete(slot);
            return LuaConstants.NONE;
        } catch (IOException e) {
            throw new ScriptException("Unable to delete save slot: " + slot, e);
        }
    }

    /**
     * Returns the save slot corresponding to the given quick save slot index.
     *
     * @param args
     *        <ol>
     *        <li>quick save slot index
     *        </ol>
     */
    @ScriptFunction
    public Varargs getQuickSaveSlot(Varargs args) {
        int slot = args.checkint(1);
        ISaveModule saveModule = env.getSaveModule();

        return LuaValue.valueOf(saveModule.getQuickSaveSlot(slot));
    }

    /**
     * Returns the save slot corresponding to the given auto save slot index.
     *
     * @param args
     *        <ol>
     *        <li>auto save slot index
     *        </ol>
     */
    @ScriptFunction
    public Varargs getAutoSaveSlot(Varargs args) {
        int slot = args.checkint(1);
        ISaveModule saveModule = env.getSaveModule();

        return LuaValue.valueOf(saveModule.getAutoSaveSlot(slot));
    }

    /**
     * Returns an {@link IStorage} object that's shared between all save files.
     *
     * @param args Not used.
     */
    @ScriptFunction
    public Varargs getSharedGlobals(Varargs args) {
        ISaveModule saveModule = env.getSaveModule();

        IStorage sharedGlobals = saveModule.getSharedGlobals();
        return LuajavaLib.toUserdata(LuaStorage.from(sharedGlobals), ILuaStorage.class);
    }
}
