package nl.weeaboo.vn.impl.script.lua;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import nl.weeaboo.common.Checks;
import nl.weeaboo.lua2.vm.LuaClosure;
import nl.weeaboo.lua2.vm.LuaStackTraceElement;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.script.ScriptException;

public class LuaScriptThreadMock implements ILuaScriptThread {

    private static final long serialVersionUID = 1L;

    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();

    private int id = ID_GENERATOR.incrementAndGet();
    private String name = "t" + id;
    private boolean runnable = true;
    private boolean destroyed;
    private boolean paused;

    private @Nullable ILuaDebugHook debugHook;

    @Override
    public void update() {
    }

    @Override
    public boolean isRunnable() {
        return runnable;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void pause() {
        paused = true;
    }

    @Override
    public void resume() {
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }

    @Override
    public void destroy() {
        destroyed = true;
    }

    @Override
    public boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public int getThreadId() {
        return id;
    }

    @Override
    public List<String> getStackTrace() {
        return Lists.transform(stackTrace(), elem -> elem.getFileName() + ":" + elem.getLineNumber());
    }

    @Override
    public LuaStackTraceElement stackTraceElem(int offset) {
        List<LuaStackTraceElement> stackTrace = stackTrace();
        if (offset < 0 || offset >= stackTrace.size()) {
            return null;
        }
        return stackTrace.get(offset);
    }

    @Override
    public List<LuaStackTraceElement> stackTrace() {
        return ImmutableList.of();
    }

    @Override
    public Varargs eval(String code) throws ScriptException {
        throw new ScriptException("eval not implemented");
    }

    @Override
    public void call(String funcName, Object... args) throws ScriptException {
        throw new ScriptException("call not implemented");
    }

    @Override
    public void call(LuaScriptFunction func) throws ScriptException {
        throw new ScriptException("call not implemented");
    }

    @Override
    public void call(LuaClosure func) throws ScriptException {
        throw new ScriptException("call not implemented");
    }

    public @Nullable ILuaDebugHook getDebugHook() {
        return debugHook;
    }

    @Override
    public void installHook(ILuaDebugHook debugHook) {
        this.debugHook = Checks.checkNotNull(debugHook);
    }

}
