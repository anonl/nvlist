package nl.weeaboo.vn.script.lua;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.luaj.vm2.LuaThread;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.DebugLib;

import nl.weeaboo.lua2.LuaException;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.link.LuaLink;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.core.impl.ContextUtil;
import nl.weeaboo.vn.script.IScriptLoader;
import nl.weeaboo.vn.script.ScriptException;

public final class LuaScriptUtil {

    private static final int DEFAULT_STACK_LIMIT = 8;
    private static final String LVN_PATTERN = ".lvn:";

    private LuaScriptUtil() {
    }

    public static boolean isLvnFile(String filename) {
        return filename.endsWith(".lvn");
    }

    public static LuaScriptFunction toScriptFunction(Varargs args, int offset) {
        return new LuaScriptFunction(args.checkclosure(offset), args.subargs(offset+1));
    }

    public static ScriptException toScriptException(String message, LuaException e) {
        ScriptException se = new ScriptException(message + ": " + e.getMessage());
        se.setStackTrace(e.getStackTrace());
        se.initCause(e.getCause());
        return se;
    }

    public static LuaScriptThread createPersistentThread(LuaRunState runState) {
        LuaLink luaLink = new LuaLink(runState);
        luaLink.setPersistent(true);
        return new LuaScriptThread(luaLink);
    }

    public static List<String> getLuaStack() {
        return getLuaStack(LuaThread.getRunning());
    }

    public static List<String> getLuaStack(LuaThread thread) {
        if (thread == null) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<String>();
        for (int level = 0; level < DEFAULT_STACK_LIMIT; level++) {
            String line = DebugLib.fileline(thread, level);
            if (line == null) {
                break;
            }
            result.add(line);
        }
        return result;
    }

    public static String getNearestLvnSrcloc(LuaScriptThread thread) {
        return getNearestLvnSrcloc(thread.luaLink.getThread());
    }

    private static String getNearestLvnSrcloc(LuaThread thread) {
        if (thread == null) {
            return null;
        }
        for (int level = 0; level < DEFAULT_STACK_LIMIT; level++) {
            String line = DebugLib.fileline(thread, level);
            if (line == null) {
                break;
            }

            if (line.contains(LVN_PATTERN)) {
                return line;
            }
        }
        return null;
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
        return new ResourceLoadInfo(filename, getLuaStack());
    }

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

    private static LuaScriptContext getScriptContext(IContext context) {
        return (LuaScriptContext)context.getScriptContext();
    }

    public static void callFunction(IContext mainContext, String functionName, Object... args)
            throws ScriptException {

        LuaScriptContext scriptContext = getScriptContext(mainContext);
        LuaScriptThread mainThread = scriptContext.getMainThread();

        IContext oldContext = ContextUtil.setCurrentContext(mainContext);
        try {
            mainThread.call(functionName, args);
        } finally {
            ContextUtil.setCurrentContext(oldContext);
        }
    }

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

}
