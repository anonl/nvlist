package nl.weeaboo.vn.impl.script.lib;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.lua2.vm.LuaBoolean;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IChoiceSeenLog;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.IResourceSeenLog;
import nl.weeaboo.vn.core.IScriptSeenLog;
import nl.weeaboo.vn.core.ISeenLogHolder;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.impl.script.lua.LuaConvertUtil;
import nl.weeaboo.vn.script.ScriptFunction;

public class SeenLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    private final IEnvironment env;

    public SeenLib(IEnvironment env) {
        super("Seen");

        this.env = env;
    }

    private ISeenLogHolder getSeenLog() {
        return env.getSeenLog();
    }

    private IResourceSeenLog getResourceLog() {
        return getSeenLog().getResourceLog();
    }

    private IChoiceSeenLog getChoiceLog() {
        return getSeenLog().getChoiceLog();
    }

    private IScriptSeenLog getScriptLog() {
        return getSeenLog().getScriptLog();
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
        return LuaValue.valueOf(getResourceLog().hasSeen(MediaType.IMAGE, filename));
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
        return LuaValue.valueOf(getResourceLog().hasSeen(MediaType.SOUND, filename));
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
        return LuaValue.valueOf(getResourceLog().hasSeen(MediaType.VIDEO, filename));
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
        return LuaValue.valueOf(getScriptLog().hasSeenLine(filename, lineNumber));
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
        getScriptLog().markLineSeen(filename, lineNumber);
    }

    /**
     * @param args
     *        <ol>
     *        <li>Choice ID (string)
     *        <li>Number of options
     *        </ol>
     */
    @ScriptFunction
    public void registerChoice(Varargs args) {
        String uniqueChoiceId = args.tojstring(1);
        int numOptions = args.checkint(2);
        getChoiceLog().registerChoice(uniqueChoiceId, numOptions);
    }

    /**
     * @param args
     *        <ol>
     *        <li>Choice ID (string)
     *        <li>Option index (starts at 1)
     *        </ol>
     */
    @ScriptFunction
    public void markChoiceSelected(Varargs args) {
        String uniqueChoiceId = args.tojstring(1);
        int optionIndex = args.checkint(2);
        getChoiceLog().markChoiceSelected(uniqueChoiceId, optionIndex);
    }

    /**
     * @param args
     *        <ol>
     *        <li>Choice ID (string)
     *        <li>Option index (starts at 1)
     *        </ol>
     */
    @ScriptFunction
    public Varargs hasSelectedChoice(Varargs args) {
        String uniqueChoiceId = args.tojstring(1);
        int optionIndex = args.checkint(2);
        return LuaBoolean.valueOf(getChoiceLog().hasSelectedChoice(uniqueChoiceId, optionIndex));
    }

}
