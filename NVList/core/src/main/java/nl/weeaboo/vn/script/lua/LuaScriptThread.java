package nl.weeaboo.vn.script.lua;

import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaValue;

import nl.weeaboo.lua2.LuaException;
import nl.weeaboo.lua2.link.LuaLink;
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

    @Deprecated
    @Override
    public boolean isFinished() {
        return luaLink.isFinished();
    }

    public void call(LuaScriptFunction func) throws ScriptException {
        func.call();
    }

    public void call(LuaClosure func) throws ScriptException {
        try {
            luaLink.call(func, LuaValue.NONE);
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

}
