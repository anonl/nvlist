package nl.weeaboo.vn.impl.script.lua;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.weeaboo.common.Checks;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.vm.LuaClosure;
import nl.weeaboo.lua2.vm.LuaConstants;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IDestructible;
import nl.weeaboo.vn.impl.core.ContextUtil;
import nl.weeaboo.vn.impl.core.DestructibleElemList;
import nl.weeaboo.vn.impl.script.ScriptEventDispatcher;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptEventDispatcher;
import nl.weeaboo.vn.script.IScriptExceptionHandler;
import nl.weeaboo.vn.script.IScriptFunction;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;

/**
 * Default implementation of {@link IScriptContext}
 */
public class LuaScriptContext implements IScriptContext, IDestructible {

    private static final long serialVersionUID = LuaImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(LuaScriptContext.class);

    private final LuaScriptEnv scriptEnv;
    private final LuaTable contextGlobals;
    private final IScriptEventDispatcher eventDispatcher;
    private final LuaScriptThread mainThread;

    private final DestructibleElemList<LuaScriptThread> threads = new DestructibleElemList<>();

    public LuaScriptContext(LuaScriptEnv scriptEnv) {
        this.scriptEnv = Checks.checkNotNull(scriptEnv);
        this.contextGlobals = new LuaTable();

        eventDispatcher = new ScriptEventDispatcher();

        LuaRunState lrs = scriptEnv.getRunState();

        mainThread = LuaScriptUtil.createPersistentThread(lrs);
        threads.add(mainThread);
    }

    @Override
    public void destroy() {
        threads.destroyAll();
    }

    @Override
    public boolean isDestroyed() {
        return mainThread.isDestroyed();
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

    public IScriptExceptionHandler getDefaultExceptionHandler() {
        return scriptEnv.getExceptionHandler();
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
            runEvents(context, exceptionHandler);
            runThreads(context, exceptionHandler);
        } finally {
            ContextUtil.setCurrentContext(oldContext);
        }
    }

    private void runEvents(IContext context, IScriptExceptionHandler exceptionHandler) {
        Deque<IScriptFunction> eventWork = new ArrayDeque<>(eventDispatcher.retrieveWork());
        try {
            while (!eventWork.isEmpty() && context.isActive()) {
                IScriptFunction func = eventWork.removeFirst();
                try {
                    mainThread.call((LuaScriptFunction)func);
                } catch (ScriptException e) {
                    LOG.warn("Exception while executing event: {}", func, e);
                    exceptionHandler.onScriptException(mainThread, e);
                }
            }
        } finally {
            // Return unexecuted work to the front of the event queue
            while (!eventWork.isEmpty()) {
                eventDispatcher.prependEvent(eventWork.removeLast());
            }
        }
    }

    private void runThreads(IContext context, IScriptExceptionHandler exceptionHandler) {
        for (LuaScriptThread thread : threads) {
            if (!context.isActive()) {
                break;
            }

            try {
                thread.update();
            } catch (ScriptException e) {
                exceptionHandler.onScriptException(thread, e);
            }
        }
    }

}
