package nl.weeaboo.vn.script.impl.lib;

import org.luaj.vm2.Varargs;

import nl.weeaboo.lua2.lib.LuajavaLib;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.impl.ContextUtil;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptFunction;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;
import nl.weeaboo.vn.script.impl.lua.LuaScriptUtil;

public class CoreLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    private final IEnvironment env;

    public CoreLib(IEnvironment env) {
        super(null); // Register all as global functions

        this.env = env;
    }

    /**
     * @param args ignored
     */
    @ScriptFunction
    public Varargs getCurrentContext(Varargs args) {
        return LuajavaLib.toUserdata(ContextUtil.getCurrentContext(), IContext.class);
    }

    /**
     * @param args ignored
     */
    @ScriptFunction
    public Varargs createContext(Varargs args) {
        IContextManager contextManager = env.getContextManager();
        IContext context = contextManager.createContext();
        return LuajavaLib.toUserdata(context, IContext.class);
    }

    /**
     * @param args
     *        <ol>
     *        <li>Function
     *        <li>Function args
     *        </ol>
     */
    @ScriptFunction
    public Varargs newThread(Varargs args) throws ScriptException {
        IContext context = ContextUtil.getCurrentContext();
        if (context == null) {
            throw new ScriptException("No context is current");
        }

        IScriptContext scriptContext = context.getScriptContext();
        IScriptFunction func = LuaScriptUtil.toScriptFunction(args, 1);
        IScriptThread thread = scriptContext.newThread(func);

        return LuajavaLib.toUserdata(thread, IScriptThread.class);
    }

}
