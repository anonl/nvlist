package nl.weeaboo.vn.script.impl.lua;

import java.io.IOException;
import java.util.List;

import nl.weeaboo.lua2.LuaException;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.LuaUtil;
import nl.weeaboo.lua2.link.LuaLink;
import nl.weeaboo.lua2.vm.LuaThread;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.core.impl.ContextUtil;
import nl.weeaboo.vn.script.IScriptFunction;
import nl.weeaboo.vn.script.IScriptLoader;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;

public final class LuaScriptUtil {

    private static final String LVN_PATTERN = ".lvn:";

    private LuaScriptUtil() {
    }

    public static boolean isLvnFile(String filename) {
        return filename.endsWith(".lvn");
    }

    /**
     * Creates a {@link LuaScriptFunction} from the closure stored in the given vararg in the given position.
     * All remaining arguments in the vararg are passed as parameters to the returned function.
     */
    public static LuaScriptFunction toScriptFunction(Varargs args, int offset) {
        if (args.isnil(offset)) {
            return null;
        }
        return new LuaScriptFunction(args.checkclosure(offset), args.subargs(offset+1));
    }

    /** Converts a {@link LuaException} to a {@link ScriptException} */
    public static ScriptException toScriptException(String message, LuaException e) {
        ScriptException se = new ScriptException(message + ": " + e.getMessage());
        se.setStackTrace(e.getStackTrace());
        se.initCause(e.getCause());
        return se;
    }

    /**
     * Creates a persistent script thread.
     *
     * @see LuaLink#setPersistent(boolean)
     */
    public static LuaScriptThread createPersistentThread(LuaRunState runState, LuaValue environment) {
        LuaLink luaLink = new LuaLink(runState, environment);
        luaLink.setPersistent(true);
        return new LuaScriptThread(luaLink);
    }

    public static String getNearestLvnSrcloc(List<String> stack) {
        for (String frame : stack) {
            if (frame.contains(LVN_PATTERN)) {
                return frame;
            }
        }
        return null;
    }

    public static ResourceLoadInfo createLoadInfo(String filename) {
        return new ResourceLoadInfo(filename, LuaUtil.getLuaStack());
    }

    private static LuaScriptContext getScriptContext(IContext context) {
        return (LuaScriptContext)context.getScriptContext();
    }

    /**
     * Loads a script in the main thread of the given context.
     *
     * @see IScriptLoader#loadScript(IScriptThread, String)
     */
    public static void loadScript(IContext mainContext, IScriptLoader scriptLoader, String scriptFilename)
            throws IOException, ScriptException {

        LuaScriptContext scriptContext = getScriptContext(mainContext);
        LuaScriptThread mainThread = scriptContext.getMainThread();

        IContext oldContext = ContextUtil.setCurrentContext(mainContext);
        try {
            scriptLoader.loadScript(mainThread, scriptFilename);
        } finally {
            ContextUtil.setCurrentContext(oldContext);
        }
    }

    public static void callFunction(IContext mainContext, String functionName, Object... args)
            throws ScriptException {

        LuaScriptContext scriptContext = getScriptContext(mainContext);
        IContext oldContext = ContextUtil.setCurrentContext(mainContext);
        try {
            scriptContext.getMainThread().call(functionName, args);
        } finally {
            ContextUtil.setCurrentContext(oldContext);
        }
    }

    /**
     * Calls a function in the main thread of the given context.
     *
     * @see IScriptLoader#loadScript(IScriptThread, String)
     */
    public static void callFunction(IContext mainContext, IScriptFunction func) throws ScriptException {
        LuaScriptContext scriptContext = getScriptContext(mainContext);
        IContext oldContext = ContextUtil.setCurrentContext(mainContext);
        try {
            scriptContext.getMainThread().call((LuaScriptFunction)func);
        } finally {
            ContextUtil.setCurrentContext(oldContext);
        }
    }

    /**
     * Runs arbitrary Lua code in the main thread of the given context.
     *
     * @see IScriptLoader#loadScript(IScriptThread, String)
     */
    public static String eval(IContext mainContext, String luaCode) throws ScriptException {
        LuaScriptContext scriptContext = getScriptContext(mainContext);
        LuaScriptThread mainThread = scriptContext.getMainThread();

        Varargs result;
        IContext oldContext = ContextUtil.setCurrentContext(mainContext);
        try {
            result = mainThread.eval(luaCode);
        } finally {
            ContextUtil.setCurrentContext(oldContext);
        }

        return result.tojstring();
    }

    public static LuaValue getFunctionEnv(LuaThread thread) {
        if (thread.callstack != null) {
            return thread.getCallstackFunction(0).getfenv();
        }
        return thread.getfenv();
    }

}
