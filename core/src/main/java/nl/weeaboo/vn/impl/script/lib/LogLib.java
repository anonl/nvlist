package nl.weeaboo.vn.impl.script.lib;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Iterables;

import nl.weeaboo.lua2.LuaUtil;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.impl.script.lua.LuaConvertUtil;
import nl.weeaboo.vn.script.ScriptFunction;

public class LogLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    public LogLib() {
        super("Log");
    }

    private Logger getLogger() {
        return getLogger(LuaUtil.getLuaStack());
    }

    protected Logger getLogger(List<String> luaStackTrace) {
        String luaScriptFile = Iterables.getFirst(luaStackTrace, "unknown");

        return LoggerFactory.getLogger("lua." + luaScriptFile);
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
