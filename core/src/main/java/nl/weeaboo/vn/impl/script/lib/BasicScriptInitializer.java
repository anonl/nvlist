package nl.weeaboo.vn.impl.script.lib;

import nl.weeaboo.lua2.LuaException;
import nl.weeaboo.lua2.LuaUtil;
import nl.weeaboo.lua2.compiler.ScriptLoader;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.BlendMode;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.SkipMode;
import nl.weeaboo.vn.core.VerticalAlign;
import nl.weeaboo.vn.impl.script.lua.ILuaScriptEnvInitializer;
import nl.weeaboo.vn.impl.script.lua.LuaPrefsAdapter;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.render.DisplayMode;
import nl.weeaboo.vn.render.IRenderEnv;
import nl.weeaboo.vn.scene.ButtonViewState;
import nl.weeaboo.vn.sound.SoundType;

/**
 * Registers some basic types and functionality within Lua.
 */
public class BasicScriptInitializer implements ILuaScriptEnvInitializer {

    private static final long serialVersionUID = 1L;

    private final IEnvironment env;

    public BasicScriptInitializer(IEnvironment env) {
        this.env = env;
    }

    @Override
    public void initEnv(LuaScriptEnv scriptEnv) {
        LuaTable globals = scriptEnv.getGlobals();

        Varargs loadResult = ScriptLoader.loadFile("builtin/stdlib");
        if (loadResult.isnil(1)) {
            throw new LuaException(loadResult.tojstring(2));
        }
        loadResult.arg1().call();

        // Enums
        registerTypes(globals,
                BlendMode.class,
                KeyCode.class,
                MediaType.class,
                SkipMode.class,
                VerticalAlign.class,
                SoundType.class,
                DisplayMode.class,
                ButtonViewState.class
        );

        LuaPrefsAdapter prefsAdapter = new LuaPrefsAdapter();
        globals.rawset("prefs", prefsAdapter.createPrefsTable());

        IRenderEnv renderEnv = env.getRenderEnv();
        globals.rawset("screenWidth", renderEnv.getWidth());
        globals.rawset("screenHeight", renderEnv.getHeight());
    }

    private void registerTypes(LuaTable globals, Class<?>... types) {
        for (Class<?> type : types) {
            LuaUtil.registerClass(globals, type);
        }
    }

}
