package nl.weeaboo.vn.script.impl.lib;

import org.luaj.vm2.LuaBoolean;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import nl.weeaboo.lua2.lib.LuajavaLib;
import nl.weeaboo.vn.core.ISystemEnv;
import nl.weeaboo.vn.core.ISystemModule;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.core.impl.DefaultEnvironment;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;

public class SystemLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    private final DefaultEnvironment env;

    public SystemLib(DefaultEnvironment env) {
        super("System");

        this.env = env;
    }

    /**
     * @param args
     *        <ol>
     *        <li>force exit
     *        </ol>
     */
    @ScriptFunction
    public Varargs exit(Varargs args) {
        boolean force = args.optboolean(1, false);

        ISystemModule system = env.getSystemModule();
        system.exit(force);
        return LuaValue.NONE;
    }

    /**
     * @param args not used
     */
    @ScriptFunction
    public Varargs canExit(Varargs args) {
        ISystemModule system = env.getSystemModule();
        return LuaBoolean.valueOf(system.canExit());
    }

    /**
     * @param args not used
     */
    @ScriptFunction
    public Varargs restart(Varargs args) throws ScriptException {
        ISystemModule system = env.getSystemModule();
        try {
            system.restart();
        } catch (InitException e) {
            throw new ScriptException("Error restarting", e);
        }
        return LuaValue.NONE;
    }

    /**
     * @param args
     *        <ol>
     *        <li>Website URL
     *        </ol>
     */
    @ScriptFunction
    public Varargs openWebsite(Varargs args) {
        String url = args.tojstring(1);

        ISystemModule system = env.getSystemModule();
        system.openWebsite(url);
        return LuaValue.NONE;
    }

    /**
     * @param args not used
     * @return An {@link ISystemEnv} object containing information about the external system.
     */
    @ScriptFunction
    public Varargs getEnv(Varargs args) {
        ISystemModule system = env.getSystemModule();

        return LuajavaLib.toUserdata(system.getSystemEnv(), ISystemEnv.class);
    }

}
