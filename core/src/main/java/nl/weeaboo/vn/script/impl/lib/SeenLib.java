package nl.weeaboo.vn.script.impl.lib;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.ISeenLog;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.script.ScriptFunction;
import nl.weeaboo.vn.script.impl.lua.LuaConvertUtil;

public class SeenLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    private final IEnvironment env;

    public SeenLib(IEnvironment env) {
        super("Seen");

        this.env = env;
    }

    private ISeenLog getSeenLog() {
        return env.getSeenLog();
    }

    /**
     * @param args
     *        <ol>
     *        <li>Filename
     *        </ol>
     * @return {@code true} if the image was previously marked as 'seen'.
     */
    @ScriptFunction
    public Varargs hasSeenImage(Varargs args) {
        FilePath filename = LuaConvertUtil.getPath(args, 1);
        return LuaValue.valueOf(getSeenLog().hasSeen(MediaType.IMAGE, filename));
    }

    /**
     * @param args
     *        <ol>
     *        <li>Filename
     *        </ol>
     * @return {@code true} if the sound was previously marked as 'seen'.
     */
    @ScriptFunction
    public Varargs hasSeenSound(Varargs args) {
        FilePath filename = LuaConvertUtil.getPath(args, 1);
        return LuaValue.valueOf(getSeenLog().hasSeen(MediaType.SOUND, filename));
    }

    /**
     * @param args
     *        <ol>
     *        <li>Filename
     *        </ol>
     * @return {@code true} if the video was previously marked as 'seen'.
     */
    @ScriptFunction
    public Varargs hasSeenVideo(Varargs args) {
        FilePath filename = LuaConvertUtil.getPath(args, 1);
        return LuaValue.valueOf(getSeenLog().hasSeen(MediaType.VIDEO, filename));
    }

    /**
     * @param args
     *        <ol>
     *        <li>Filename
     *        <li>Line number (starting at 1)
     *        </ol>
     * @return {@code true} if the script line was previously marked as 'seen'.
     */
    @ScriptFunction
    public Varargs hasSeenLine(Varargs args) {
        FilePath filename = LuaConvertUtil.getPath(args, 1);
        int lineNumber = args.checkint(2);
        return LuaValue.valueOf(getSeenLog().hasSeenLine(filename, lineNumber));
    }

    /**
     * @param args
     *        <ol>
     *        <li>Filename
     *        <li>Line number (starting at 1)
     *        </ol>
     */
    @ScriptFunction
    public void markLineSeen(Varargs args) {
        FilePath filename = LuaConvertUtil.getPath(args, 1);
        int lineNumber = args.checkint(2);
        getSeenLog().markLineSeen(filename, lineNumber);
    }

}
