package nl.weeaboo.vn.script.impl.lua;

import java.util.List;

import nl.weeaboo.lua2.LuaException;
import nl.weeaboo.lua2.LuaUtil;
import nl.weeaboo.lua2.link.LuaLink;
import nl.weeaboo.lua2.vm.LuaClosure;
import nl.weeaboo.lua2.vm.LuaConstants;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.impl.Indirect;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;

public class LuaScriptThread implements IScriptThread {

    private static final long serialVersionUID = LuaImpl.serialVersionUID;

    final Indirect<LuaLink> luaLink;

    LuaScriptThread(LuaLink link) {
        this.luaLink = Indirect.of(link);
    }

    @Override
    public void destroy() {
        luaLink.get().destroy();
    }

    @Override
    public boolean isDestroyed() {
        return luaLink.get().isFinished();
    }

    public Varargs eval(String code) throws ScriptException {
        LuaLink link = luaLink.get();

        try {
            LuaClosure func = LuaUtil.compileForEval(code, link.getThread().getCallEnv());
            return link.call(func, LuaConstants.NONE);
        } catch (LuaException e) {
            throw LuaScriptUtil.toScriptException("Error in thread: " + this, e);
        }
    }

    public void call(String funcName, Object... args) throws ScriptException {
        LuaLink link = luaLink.get();

        try {
            link.call(funcName, args);
        } catch (LuaException e) {
            throw LuaScriptUtil.toScriptException("Error in thread: " + this, e);
        }
    }

    public void call(LuaScriptFunction func) throws ScriptException {
        LuaLink link = luaLink.get();

        func.call(link);
    }

    public void call(LuaClosure func) throws ScriptException {
        LuaLink link = luaLink.get();

        try {
            link.call(func, LuaConstants.NONE);
        } catch (LuaException e) {
            throw LuaScriptUtil.toScriptException("Error in thread: " + this, e);
        }
    }

    @Override
    public void update() throws ScriptException {
        LuaLink link = luaLink.get();

        try {
            link.update();
        } catch (LuaException e) {
            throw LuaScriptUtil.toScriptException("Error in thread: " + this, e);
        }
    }

    @Override
    public boolean isRunnable() {
        LuaLink link = luaLink.get();

        return link.isRunnable();
    }

    @Override
    public String toString() {
        LuaLink link = luaLink.get();

        return String.valueOf(link.getThread());
    }

    @Override
    public List<String> getStackTrace() {
        LuaLink link = luaLink.get();

        return LuaUtil.getLuaStack(link.getThread());
    }

}
