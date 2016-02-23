package nl.weeaboo.vn.script.impl.lib;

import org.luaj.vm2.LuaTable;

import nl.weeaboo.lua2.LuaUtil;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.core.KeyCode;
import nl.weeaboo.vn.core.VerticalAlign;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.impl.lua.ILuaScriptEnvInitializer;
import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;

public class BasicScriptInitializer implements ILuaScriptEnvInitializer {

    @Override
    public void initEnv(LuaScriptEnv env) throws ScriptException {
        LuaTable globals = env.getGlobals();

        // Enums
        registerTypes(globals,
            BlendMode.class,
            KeyCode.class,
            VerticalAlign.class
        );
    }

    private void registerTypes(LuaTable globals, Class<?>... types) {
        for (Class<?> type : types) {
            LuaUtil.registerClass(globals, type);
        }
    }

}
