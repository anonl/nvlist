package nl.weeaboo.vn.impl.script.lua;

import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.lua2.LuaException;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.LuaUtil;
import nl.weeaboo.lua2.vm.LuaThread;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.impl.core.ContextUtil;
import nl.weeaboo.vn.impl.stats.FileLine;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.IScreen;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptFunction;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;

/**
 * Various utility functions for working with Lua scripts.
 */
public final class LuaScriptUtil {

    private static final Logger LOG = LoggerFactory.getLogger(LuaScriptUtil.class);
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
        ScriptException se = new ScriptException(message + ": " + e.getMessage(), e.getCause());
        se.setStackTrace(e.getStackTrace());
        return se;
    }

    /**
     * Creates a persistent script thread.
     *
     * @see LuaThread#setPersistent(boolean)
     */
    public static ILuaScriptThread createPersistentThread(LuaRunState runState) {
        LuaThread thread = runState.newThread();
        thread.setPersistent(true);
        return new LuaScriptThread(thread);
    }

    /**
     * Finds the topmost '*.lvn' source file in the call stack.
     */
    public static @Nullable FileLine getNearestLvnSrcloc(List<String> stack) {
        for (String frame : stack) {
            if (frame.contains(LVN_PATTERN)) {
                return FileLine.fromString(frame);
            }
        }
        return null;
    }

    /**
     * Creates {@link ResourceLoadInfo} for the current Lua call stack.
     * @see LuaUtil#getLuaStack()
     */
    public static ResourceLoadInfo createLoadInfo(MediaType mediaType, FilePath filename) {
        List<String> callStack = ImmutableList.of();
        IContext context = ContextUtil.getCurrentContext();
        if (context == null) {
            LOG.debug("No context is current: unable to determine callStack: {}", filename);
        } else {
            try {
                IScriptContext scriptContext = getCurrentScriptContext();

                /*
                 * By taking the stack trace of the main thread, we can correlate image loads with the current
                 * line in the .lvn file. This assumes that the main thread is used to run the .lvn files, but that should
                 * pretty much always be the case.
                 */
                callStack = scriptContext.getMainThread().getStackTrace();
            } catch (ScriptException e) {
                LOG.warn("No script context is current: unable to determine callStack: {}", filename, e);
            }
        }
        return new ResourceLoadInfo(mediaType, filename, callStack);
    }

    private static LuaScriptContext getScriptContext(IContext context) {
        return (LuaScriptContext)context.getScriptContext();
    }

    /**
     * Loads a script in the main thread of the given context.
     *
     * @throws IOException If the script file can't be read.
     * @throws ScriptException If the script throws an exception.
     */
    public static void loadScript(IContext mainContext, FilePath scriptFilename)
            throws IOException, ScriptException {

        LuaScriptContext scriptContext = getScriptContext(mainContext);
        ILuaScriptThread mainThread = scriptContext.getMainThread();

        IContext oldContext = ContextUtil.setCurrentContext(mainContext);
        try {
            mainThread.call(scriptContext.loadScriptAsClosure(scriptFilename));
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
     */
    public static String eval(IContext context, String luaCode) throws ScriptException {
        Varargs result;
        IContext oldContext = ContextUtil.setCurrentContext(context);
        try {
            result = getScriptContext(context).getMainThread().eval(luaCode);
        } finally {
            ContextUtil.setCurrentContext(oldContext);
        }
        return result.tojstring();
    }

    /**
     * Runs arbitrary Lua code in the given thread.
     *
     * @throws ScriptException If the Lua code can't be parsed, or throws an exception.
     */
    public static String eval(IContextManager contextManager, ILuaScriptThread thread, String luaCode)
            throws ScriptException {

        Varargs result;
        IContext oldContext = ContextUtil.setCurrentContext(getContextForThread(contextManager, thread));
        try {
            result = thread.eval(luaCode);
        } finally {
            ContextUtil.setCurrentContext(oldContext);
        }
        return result.tojstring();
    }

    private static @Nullable IContext getContextForThread(IContextManager contextManager, IScriptThread thread) {
        for (IContext context : contextManager.getContexts()) {
            if (context.getScriptContext().getThreads().contains(thread)) {
                return context;
            }
        }
        throw new IllegalStateException("Thread doesn't exist: " + thread);
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
