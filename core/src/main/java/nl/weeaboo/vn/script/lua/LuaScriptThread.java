package nl.weeaboo.vn.script.lua;

import static org.luaj.vm2.LuaValue.NONE;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.luaj.vm2.LoadState;
import org.luaj.vm2.LuaClosure;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaThread;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import nl.weeaboo.common.StringUtil;
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

    public Varargs eval(String code) throws ScriptException {
        LuaClosure func = compileForEval(luaLink.getThread(), code);
        try {
            return luaLink.call(func, LuaValue.NONE);
        } catch (LuaException e) {
            throw LuaScriptUtil.toScriptException("Error in thread: " + this, e);
        }
    }

    private static LuaClosure compileForEval(LuaThread luaThread, String code) throws ScriptException {
        final String chunkName = "(eval)";
        final LuaValue env = luaThread.getfenv();
        try {
            ByteArrayInputStream bin = new ByteArrayInputStream(StringUtil.toUTF8("return " + code));

            Varargs result = NONE;
            try {
                // Try to evaluate as an expression
                result = LoadState.load(bin, chunkName, env);
            } catch (LuaError err) {
                // Try to evaluate as a statement, no value to return
                bin.reset();
                bin.skip(7); // Skip "return "
                result = LoadState.load(bin, chunkName, env);
            }

            LuaValue f = result.arg1();
            if (!f.isclosure()) {
                throw new LuaError(result.arg(2).tojstring());
            }
            return f.checkclosure();
        } catch (RuntimeException re) {
            throw new ScriptException("Error compiling script: " + code, re);
        } catch (IOException e) {
            throw new ScriptException("Error compiling script: " + code, e);
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

    @Override
    public String toString() {
        return String.valueOf(luaLink.getThread());
    }

    @Override
    public List<String> getStackTrace() {
        return LuaScriptUtil.getLuaStack(luaLink.getThread());
    }

}
