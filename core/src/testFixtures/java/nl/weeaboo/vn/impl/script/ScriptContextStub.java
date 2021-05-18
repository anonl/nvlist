package nl.weeaboo.vn.impl.script;

import java.util.Collection;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.impl.core.DestructibleElemList;
import nl.weeaboo.vn.impl.script.lua.LuaScriptThreadMock;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptEventDispatcher;
import nl.weeaboo.vn.script.IScriptExceptionHandler;
import nl.weeaboo.vn.script.IScriptFunction;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;

public class ScriptContextStub implements IScriptContext {

    private static final long serialVersionUID = 1L;

    private final DestructibleElemList<IScriptThread> threads = new DestructibleElemList<>();
    private final IScriptEventDispatcher scriptEventDispatcher = new ScriptEventDispatcher();

    private final IScriptThread mainThread;

    public ScriptContextStub() {
        this(new LuaScriptThreadMock());
    }

    public ScriptContextStub(IScriptThread mainThread) {
        this.mainThread = mainThread;

        threads.add(mainThread);
    }

    @Override
    public IScriptThread loadScriptInNewThread(FilePath path) throws ScriptException {
        throw new ScriptException("Not supported");
    }

    @Override
    public IScriptThread createThread(IScriptFunction func) throws ScriptException {
        throw new ScriptException("Not supported");
    }

    @Override
    public IScriptThread getMainThread() {
        return mainThread;
    }

    @Override
    public Collection<? extends IScriptThread> getThreads() {
        return threads;
    }

    @Override
    public void updateThreads(IContext context, IScriptExceptionHandler exceptionHandler) {
        for (IScriptThread thread : threads.getSnapshot()) {
            try {
                thread.update();
            } catch (ScriptException e) {
                exceptionHandler.onScriptException(thread, e);
            }
        }
    }

    @Override
    public IScriptEventDispatcher getEventDispatcher() {
        return scriptEventDispatcher;
    }

}
