package nl.weeaboo.vn.script.impl.lib;

import java.util.Collection;

import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.save.ISaveFile;
import nl.weeaboo.vn.save.ISaveFileHeader;
import nl.weeaboo.vn.save.ISaveModule;
import nl.weeaboo.vn.script.ScriptFunction;

public class SaveLib extends LuaLib {

    private static final long serialVersionUID = 1L;

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
            table.rawset(saveInfo.getSlot(), LuajavaLib.toUserdata(saveInfo, ISaveFileHeader.class));
        }
        return table;
    }

/*

    protected Varargs save(Varargs args) {
        int slot = args.checkint(1);

        //Screenshot
        Object screenshotObj;
        int ssw = 224;
        int ssh = 126;
        if (args.istable(2)) {
            LuaTable t = args.checktable(2);
            screenshotObj = t.get("screenshot").touserdata();
            ssw = t.get("width").optint(ssw);
            ssh = t.get("height").optint(ssh);
        } else {
            screenshotObj = args.touserdata(2);
        }
        final IScreenshot ss = (screenshotObj instanceof IScreenshot ? (IScreenshot)screenshotObj : null);

        //Meta data
        IStorage metaData = new BaseStorage();
        if (args.istable(3)) {
            LuaTable table = args.checktable(3);
            metaData.set("", table);
        }

        //Save
        final LuaRunState lrs = LuaRunState.getCurrent();
        final LuaThread thread = lrs.getRunningThread();
        Varargs result = thread.yield(NONE);
        try {
            saveHandler.save(slot, ss, new Dim(ssw, ssh), metaData, null);
        } catch (IOException e) {
            throw new LuaError(e.getMessage(), e);
        }
        return result;
    }

    protected Varargs load(Varargs args) {
        final LuaRunState lrs = LuaRunState.getCurrent();
        final LuaThread thread = lrs.getRunningThread();
        Varargs result = thread.yield(NONE);
        lrs.destroy();

        int slot = args.checkint(1);
        try {
            saveHandler.load(slot, null);
        } catch (IOException e) {
            throw new LuaError(e);
        }

        return result;
    }

    protected Varargs getSavepointStorage(Varargs args) {
        return LuajavaLib.toUserdata(saveHandler.getSavepointStorage(), IStorage.class);
    }

    protected Varargs getQuickSaveSlot(Varargs args) {
        return valueOf(saveHandler.getQuickSaveSlot(args.optint(1, 1)));
    }

    protected Varargs getAutoSaveSlot(Varargs args) {
        return valueOf(saveHandler.getAutoSaveSlot(args.optint(1, 1)));
    }

    protected Varargs getFreeSaveSlot(Varargs args) {
        return valueOf(saveHandler.getNextFreeSlot());
    }

 */
}
