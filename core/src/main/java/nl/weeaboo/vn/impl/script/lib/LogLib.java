package nl.weeaboo.vn.impl.script.lib;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.lua2.stdlib.DebugTrace;
import nl.weeaboo.lua2.vm.LuaStackTraceElement;
import nl.weeaboo.lua2.vm.LuaThread;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.impl.script.lua.LuaConvertUtil;
import nl.weeaboo.vn.script.ScriptFunction;

/**
 * Lua "Log" library.
 */
public class LogLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    public LogLib() {
        super("Log");
    }

    private Logger getLogger() {
        LuaStackTraceElement stackTraceElem = DebugTrace.stackTraceElem(LuaThread.getRunning(), 0);
        return getLogger(stackTraceElem != null ? stackTraceElem.getFileName() : "unknown");
    }

    protected Logger getLogger(String fileName) {
        return LoggerFactory.getLogger("lua." + fileName);
    }

    private String getLogFormat(Varargs args) {
        return args.tojstring(1);
    }

    private Object[] getLogArgs(Varargs args) {
        return LuaConvertUtil.toObjectArray(args, 2);
    }

    /**
     * @param args
     *        <ol>
     *        <li>format string
     *        <li>any number of format args
     *        </ol>
     */
    @ScriptFunction
    public void trace(Varargs args) {
        getLogger().trace(getLogFormat(args), getLogArgs(args));
    }

    /**
     * @param args
     *        <ol>
     *        <li>format string
     *        <li>any number of format args
     *        </ol>
     */
    @ScriptFunction
    public void debug(Varargs args) {
        getLogger().debug(getLogFormat(args), getLogArgs(args));
    }

    /**
     * @param args
     *        <ol>
     *        <li>format string
     *        <li>any number of format args
     *        </ol>
     */
    @ScriptFunction
    public void info(Varargs args) {
        getLogger().info(getLogFormat(args), getLogArgs(args));
    }

    /**
     * @param args
     *        <ol>
     *        <li>format string
     *        <li>any number of format args
     *        </ol>
     */
    @ScriptFunction
    public void warn(Varargs args) {
        getLogger().warn(getLogFormat(args), getLogArgs(args));
    }

    /**
     * @param args
     *        <ol>
     *        <li>format string
     *        <li>any number of format args
     *        </ol>
     */
    @ScriptFunction
    public void error(Varargs args) {
        getLogger().error(getLogFormat(args), getLogArgs(args));
    }

}
