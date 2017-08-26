package nl.weeaboo.vn.impl.script.lua;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.lua2.LuaException;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.LuaUtil;
import nl.weeaboo.lua2.link.LuaLink;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.impl.core.ContextUtil;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptFunction;
import nl.weeaboo.vn.script.IScriptLoader;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;

public final class LuaScriptUtil {

    private static final String LVN_PATTERN = ".lvn:";

    private LuaScriptUtil() {
    }

    static boolean isLvnFile(String filename) {
        return filename.endsWith(".lvn");
    }

    /**
     * Creates a {@link LuaScriptFunction} from the closure stored in the given vararg in the given position.
     * All remaining arguments in the vararg are passed as parameters to the returned function.
     */
    public static @Nullable LuaScriptFunction toScriptFunction(Varargs args, int offset) {
        if (args.isnil(offset)) {
            return null;
        }
        return new LuaScriptFunction(args.checkclosure(offset), args.subargs(offset + 1));
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

    /**
     * Finds the topmost '*.lvn' source file in the call stack.
     */
    public static @Nullable String getNearestLvnSrcloc(List<String> stack) {
        for (String frame : stack) {
            if (frame.contains(LVN_PATTERN)) {
                return frame;
            }
        }
        return null;
    }

    /**
     * Creates {@link ResourceLoadInfo} for the current Lua call stack.
     * @see LuaUtil#getLuaStack()
     */
    public static ResourceLoadInfo createLoadInfo(FilePath filename) {
        return new ResourceLoadInfo(filename, LuaUtil.getLuaStack());
    }

    private static LuaScriptContext getScriptContext(IContext context) {
        return (LuaScriptContext)context.getScriptContext();
    }

    /**
     * Loads a script in the main thread of the given context.
     *
     * @throws IOException If the script file can't be read.
     * @throws ScriptException If the script throws an exception.
     * @see IScriptLoader#loadScript(IScriptThread, FilePath)
     */
    public static void loadScript(IContext mainContext, IScriptLoader scriptLoader, FilePath scriptFilename)
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

    /**
     * Calls a Lua function on the main thread of the current script context.
     * @throws ScriptException If the Lua function can't be found, or it throws an exception when called.
     */
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
     * @throws ScriptException If the Lua function throws an exception when called.
     * @see IScriptLoader#loadScript(IScriptThread, FilePath)
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
     * @throws ScriptException If the Lua code can't be parsed, or throws an exception.
     * @see IScriptLoader#loadScript(IScriptThread, FilePath)
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

    private static IScreen getCurrentScreen() throws ScriptException {
        IScreen currentScreen = ContextUtil.getCurrentScreen();
        if (currentScreen == null) {
            throw new ScriptException("No screen active");
        }
        return currentScreen;
    }

    /**
     * @return The active layer of the current screen.
     * @throws ScriptException If no screen is current.
     * @see #getCurrentScreen()
     * @see IScreen#getActiveLayer()
     */
    public static ILayer getActiveLayer() throws ScriptException {
        return getCurrentScreen().getActiveLayer();
    }

    /**
     * @return The root layer of the current screen.
     * @throws ScriptException If no screen is current.
     * @see #getCurrentScreen()
     * @see IScreen#getRootLayer()
     */
    public static ILayer getRootLayer() throws ScriptException {
        return getCurrentScreen().getRootLayer();
    }

    /**
     * @return The script context of the current context.
     * @throws ScriptException If no context is current.
     */
    public static IScriptContext getCurrentScriptContext() throws ScriptException {
        IContext context = ContextUtil.getCurrentContext();
        if (context == null) {
            throw new ScriptException("No script context current");
        }
        return context.getScriptContext();
    }

}
