package nl.weeaboo.vn.impl.script.lib;

import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IContextManager;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.impl.core.ContextUtil;
import nl.weeaboo.vn.impl.script.lua.LuaScriptUtil;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.script.IScriptFunction;
import nl.weeaboo.vn.script.IScriptThread;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;

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
     * @param args
     *        <ol>
     *        <li>(optional) Function
     *        <li>(optional) Function args
     *        </ol>
     */
    @ScriptFunction
    public Varargs createContext(Varargs args) {
        IContextManager contextManager = env.getContextManager();

        IScriptFunction func = LuaScriptUtil.toScriptFunction(args, 1);
        IContext context = contextManager.createContext(func);

        return LuajavaLib.toUserdata(context, IContext.class);
    }

    /**
     * @param args
     *        <ol>
     *        <li>Context
     *        <li>active
     *        </ol>
     */
    @ScriptFunction
    public void setContextActive(Varargs args) {
        IContext context = args.touserdata(1, IContext.class);
        boolean active = args.toboolean(2);

        IContextManager contextManager = env.getContextManager();
        contextManager.setContextActive(context, active);
    }

    /**
     * @param args
     *        <ol>
     *        <li>Function
     *        <li>Function args
     *        </ol>
     * @throws ScriptException If thread creation fails.
     */
    @ScriptFunction
    public Varargs newThread(Varargs args) throws ScriptException {
        IContext context = ContextUtil.getCurrentContext();
        if (context == null) {
            throw new ScriptException("No context is current");
        }

        IScriptContext scriptContext = context.getScriptContext();
        IScriptFunction func = LuaScriptUtil.toScriptFunction(args, 1);
        IScriptThread thread = scriptContext.createThread(func);

        return LuajavaLib.toUserdata(thread, IScriptThread.class);
    }

}
