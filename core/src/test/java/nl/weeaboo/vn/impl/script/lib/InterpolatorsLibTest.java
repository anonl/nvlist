package nl.weeaboo.vn.impl.script.lib;

import org.junit.Test;

import nl.weeaboo.gdx.test.ExceptionTester;
import nl.weeaboo.lua2.vm.LuaConstants;
import nl.weeaboo.lua2.vm.LuaInteger;
import nl.weeaboo.vn.core.Interpolators;
import nl.weeaboo.vn.impl.script.lib.InterpolatorsLib;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.script.ScriptException;

public class InterpolatorsLibTest extends AbstractLibTest {

    private InterpolatorsLib interpolatorsLib;

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        super.addInitializers(scriptEnv);

        interpolatorsLib = new InterpolatorsLib();
        scriptEnv.addInitializer(interpolatorsLib);
    }

    @Test
    public void interpolatorFromLuaFunction() {
        loadScript("integration/interpolators/luainterpolator.lvn");
    }

    /** The public static interpolators from {@link Interpolators} are also made available to Lua. */
    @Test
    public void defaultInterpolators() {
        loadScript("integration/interpolators/defaultinterpolators.lvn");
    }

    /** Attempt to create an interpolator from an invalid value */
    @Test
    public void interpolatorFromNil() {
        ExceptionTester exTester = new ExceptionTester();
        exTester.expect(ScriptException.class, () -> interpolatorsLib.get(LuaConstants.NONE));
        exTester.expect(ScriptException.class, () -> interpolatorsLib.get(LuaInteger.valueOf(0)));
    }

}
