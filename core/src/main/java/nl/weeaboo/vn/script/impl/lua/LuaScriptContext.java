package nl.weeaboo.vn.script.impl.lua;

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
import nl.weeaboo.vn.core.impl.ContextUtil;
import nl.weeaboo.vn.core.impl.DestructibleElemList;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptEventDispatcher;
import nl.weeaboo.vn.script.IScriptFunction;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.impl.ScriptEventDispatcher;

public class LuaScriptContext implements IScriptContext {

    private static final long serialVersionUID = LuaImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(LuaScriptContext.class);

    private final LuaScriptEnv scriptEnv;
    private final LuaTable contextGlobals;
    private final IScriptEventDispatcher eventDispatcher;
    private final LuaScriptThread mainThread;
    private final LuaScriptThread eventThread;

    private final DestructibleElemList<LuaScriptThread> threads = new DestructibleElemList<LuaScriptThread>();

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

    public IScriptThread newThread(LuaClosure func) throws ScriptException {
        return newThread(func, LuaConstants.NONE);
    }
    public IScriptThread newThread(LuaClosure func, Varargs args) throws ScriptException {
        return newThread(new LuaScriptFunction(func, args));
    }

    @Override
    public IScriptThread newThread(IScriptFunction func) throws ScriptException {
        LuaScriptFunction luaFunc = (LuaScriptFunction)func;

        LuaScriptThread thread = luaFunc.callInNewThread();
        threads.add(thread);
        return thread;
    }

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
    public void updateThreads(IContext context) {
        IContext oldContext = ContextUtil.setCurrentContext(context);
        try {
            LuaTable globals = scriptEnv.getGlobals();
            globals.rawset("context", contextGlobals);

            runEvents();
            runThreads();
        } finally {
            ContextUtil.setCurrentContext(oldContext);
        }
    }

    private void runEvents() {
        List<IScriptFunction> eventWork = eventDispatcher.retrieveWork();
        for (IScriptFunction func : eventWork) {
            try {
               eventThread.call((LuaScriptFunction)func);
            } catch (ScriptException e) {
                LOG.warn("Exception while executing event: " + func, e);
            }
        }
    }

    private void runThreads() {
        for (LuaScriptThread thread : threads) {
            try {
                thread.update();
            } catch (ScriptException e) {
                LOG.warn("Exception while executing thread: " + thread, e);
            }
        }
    }

}
