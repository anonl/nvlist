package nl.weeaboo.vn.script.impl.lib;

import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.LuaBoolean;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.IPlayTimer;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.ISystemEnv;
import nl.weeaboo.vn.core.ISystemModule;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;
import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;

public class SystemLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    private final IEnvironment env;

    public SystemLib(IEnvironment env) {
        super("System");

        this.env = env;
    }

    @Override
    public void initEnv(LuaScriptEnv scriptEnv) throws ScriptException {
        super.initEnv(scriptEnv);

        IRenderEnv renderEnv = env.getRenderEnv();

        LuaTable globals = scriptEnv.getGlobals();
        globals.rawset("screenWidth", renderEnv.getWidth());
        globals.rawset("screenHeight", renderEnv.getHeight());
    }

    /**
     * @param args
     *        <ol>
     *        <li>force exit
     *        </ol>
     */
    @ScriptFunction
    public void exit(Varargs args) {
        boolean force = args.optboolean(1, false);

        ISystemModule system = env.getSystemModule();
        system.exit(force);
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
    public void restart(Varargs args) throws ScriptException {
        ISystemModule system = env.getSystemModule();
        try {
            system.restart();
        } catch (InitException e) {
            throw new ScriptException("Error restarting", e);
        }
    }

    /**
     * @param args
     *        <ol>
     *        <li>Website URL
     *        </ol>
     */
    @ScriptFunction
    public void openWebsite(Varargs args) {
        String url = args.tojstring(1);

        ISystemModule system = env.getSystemModule();
        system.openWebsite(url);
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

    /**
     * @param args not used
     * @return A {@link IPlayTimer} which can be queried for the total play time.
     */
    @ScriptFunction
    public Varargs getTimer(Varargs args) {
        IPlayTimer playTimer = env.getPlayTimer();

        return LuajavaLib.toUserdata(playTimer, IPlayTimer.class);
    }

}
