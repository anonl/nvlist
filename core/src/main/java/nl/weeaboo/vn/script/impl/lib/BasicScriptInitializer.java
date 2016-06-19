package nl.weeaboo.vn.script.impl.lib;

import nl.weeaboo.lua2.LuaUtil;
import nl.weeaboo.lua2.lib.BaseLib;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.SkipMode;
import nl.weeaboo.vn.core.VerticalAlign;
import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.impl.lua.ILuaScriptEnvInitializer;
import nl.weeaboo.vn.script.impl.lua.LuaPrefsAdapter;
import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;

public class BasicScriptInitializer implements ILuaScriptEnvInitializer {

    private static final long serialVersionUID = 1L;

    @Override
    public void initEnv(LuaScriptEnv env) throws ScriptException {
        LuaTable globals = env.getGlobals();

        BaseLib.loadFile("builtin/stdlib").arg1().call();

        // Enums
        registerTypes(globals,
            BlendMode.class,
            KeyCode.class,
            MediaType.class,
            SkipMode.class,
            VerticalAlign.class
        );

        LuaPrefsAdapter prefsAdapter = new LuaPrefsAdapter();
        globals.rawset("prefs", prefsAdapter.createPrefsTable());
    }

    private void registerTypes(LuaTable globals, Class<?>... types) {
        for (Class<?> type : types) {
            LuaUtil.registerClass(globals, type);
        }
    }

}
