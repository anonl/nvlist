package nl.weeaboo.vn.impl.script.lua;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Checks;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.vm.LuaClosure;
import nl.weeaboo.lua2.vm.LuaConstants;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.impl.core.ContextUtil;
import nl.weeaboo.vn.impl.core.DestructibleElemList;
import nl.weeaboo.vn.impl.script.ScriptEventDispatcher;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptEventDispatcher;
import nl.weeaboo.vn.script.IScriptExceptionHandler;
import nl.weeaboo.vn.script.IScriptFunction;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;

public class LuaScriptContext implements IScriptContext {

    private static final long serialVersionUID = LuaImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(LuaScriptContext.class);

    private final LuaScriptEnv scriptEnv;
    private final LuaTable contextGlobals;
    private final IScriptEventDispatcher eventDispatcher;
    private final LuaScriptThread mainThread;
    private final LuaScriptThread eventThread;

    private final DestructibleElemList<LuaScriptThread> threads = new DestructibleElemList<>();

    public LuaScriptContext(LuaScriptEnv scriptEnv) {
        this.scriptEnv = Checks.checkNotNull(scriptEnv);
        this.contextGlobals = new LuaTable();

        eventDispatcher = new ScriptEventDispatcher();

        LuaRunState lrs = scriptEnv.getRunState();
        LuaTable globals = lrs.getGlobalEnvironment();

        eventThread = LuaScriptUtil.createPersistentThread(lrs, globals);
        threads.add(eventThread);

        mainThread = LuaScriptUtil.createPersistentThread(lrs, globals);
        threads.add(mainThread);
    }

    /**
     * Calls a no-arg function in a new thread.
     * @throws ScriptException If the function can't be called, or the thread can't be created.
     * @see #createThread(LuaClosure, Varargs)
     */
    public IScriptThread createThread(LuaClosure func) throws ScriptException {
        return createThread(func, LuaConstants.NONE);
    }

    /**
     * Calls a function in a new thread, passing {@code args} as the function arguments.
     * @throws ScriptException If the function can't be called, or the thread can't be created.
     */
    public IScriptThread createThread(LuaClosure func, Varargs args) throws ScriptException {
        return createThread(new LuaScriptFunction(func, args));
    }

    @Override
    public IScriptThread createThread(IScriptFunction func) throws ScriptException {
        LuaScriptFunction luaFunc = (LuaScriptFunction)func;

        LuaScriptThread thread = luaFunc.callInNewThread();
        threads.add(thread);
        return thread;
    }

    /**
     * @return The Lua table containing the system-global settings.
     */
    public LuaTable getGlobals() {
        return scriptEnv.getGlobals();
    }

    /**
     * @return The Lua table containing the context-global settings.
     */
    public LuaTable getContextGlobals() {
        return contextGlobals;
    }

    @Override
    public LuaScriptThread getMainThread() {
        return mainThread;
    }

    @Override
    public Collection<LuaScriptThread> getThreads() {
        return threads.getSnapshot();
    }

    @Override
    public IScriptEventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    @Override
    public void updateThreads(IContext context, IScriptExceptionHandler exceptionHandler) {
        IContext oldContext = ContextUtil.setCurrentContext(context);
        try {
            runEvents(exceptionHandler);
            runThreads(exceptionHandler);
        } finally {
            ContextUtil.setCurrentContext(oldContext);
        }
    }

    private void runEvents(IScriptExceptionHandler exceptionHandler) {
        List<IScriptFunction> eventWork = eventDispatcher.retrieveWork();
        for (IScriptFunction func : eventWork) {
            try {
                eventThread.call((LuaScriptFunction)func);
            } catch (ScriptException e) {
                LOG.warn("Exception while executing event: {}", func, e);
                exceptionHandler.onScriptException(eventThread, e);
            }
        }
    }

    private void runThreads(IScriptExceptionHandler exceptionHandler) {
        for (LuaScriptThread thread : threads) {
            try {
                thread.update();
            } catch (ScriptException e) {
                LOG.warn("Exception while executing thread: {}", thread, e);
                exceptionHandler.onScriptException(thread, e);
            }
        }
    }

}
