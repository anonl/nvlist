package nl.weeaboo.vn.script.impl.lua;

import java.util.List;

import nl.weeaboo.lua2.LuaException;
import nl.weeaboo.lua2.LuaUtil;
import nl.weeaboo.lua2.link.LuaLink;
import nl.weeaboo.lua2.vm.LuaClosure;
import nl.weeaboo.lua2.vm.LuaConstants;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;

public class LuaScriptThread implements IScriptThread {

    private static final long serialVersionUID = LuaImpl.serialVersionUID;

    final LuaLink luaLink;

    LuaScriptThread(LuaLink link) {
        this.luaLink = link;
    }

    @Override
    public void destroy() {
        luaLink.destroy();
    }

    @Override
    public boolean isDestroyed() {
        return luaLink.isFinished();
    }

    public Varargs eval(String code) throws ScriptException {
        try {
            LuaClosure func = LuaUtil.compileForEval(code, luaLink.getThread().getCallEnv());
            return luaLink.call(func, LuaConstants.NONE);
        } catch (LuaException e) {
            throw LuaScriptUtil.toScriptException("Error in thread: " + this, e);
        }
    }

    public void call(String funcName, Object... args) throws ScriptException {
        try {
            luaLink.call(funcName, args);
        } catch (LuaException e) {
            throw LuaScriptUtil.toScriptException("Error in thread: " + this, e);
        }
    }

    public void call(LuaScriptFunction func) throws ScriptException {
        func.call(luaLink);
    }

    public void call(LuaClosure func) throws ScriptException {
        try {
            luaLink.call(func, LuaConstants.NONE);
        } catch (LuaException e) {
            throw LuaScriptUtil.toScriptException("Error in thread: " + this, e);
        }
    }

    @Override
    public void update() throws ScriptException {
        try {
            luaLink.update();
        } catch (LuaException e) {
            throw LuaScriptUtil.toScriptException("Error in thread: " + this, e);
        }
    }

    @Override
    public boolean isRunnable() {
        return luaLink.isRunnable();
    }

    @Override
    public String toString() {
        return String.valueOf(luaLink.getThread());
    }

    @Override
    public List<String> getStackTrace() {
        return LuaUtil.getLuaStack(luaLink.getThread());
    }

}
