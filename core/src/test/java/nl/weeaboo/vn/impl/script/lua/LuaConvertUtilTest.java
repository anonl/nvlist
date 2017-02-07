package nl.weeaboo.vn.impl.script.lua;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.lua2.luajava.CoerceJavaToLua;
import nl.weeaboo.lua2.vm.LuaNil;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.image.ImageModuleStub;
import nl.weeaboo.vn.impl.image.TestScreenshot;
import nl.weeaboo.vn.impl.image.TestTexture;
import nl.weeaboo.vn.impl.scene.Layer;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.script.ScriptException;

public class LuaConvertUtilTest {

    private ImageModuleStub imageModule;

    @Before
    public void before() {
        LuaTestUtil.newRunState();

        imageModule = new ImageModuleStub();
    }

    @Test
    public void getLayerArg() throws ScriptException {
        final ILayer dummyLayer = new Layer(null);

        assertLayerArg(dummyLayer, 1);
        // Different indices work
        assertLayerArg(dummyLayer, 2);
        // Null values work
        assertLayerArg(null, 1);
        // Incompatible types throw an exception
        assertLayerArgException(Integer.valueOf(7), 1);
        assertLayerArgException(new Object(), 1);
    }

    @Test
    public void getTextureArg() throws ScriptException {
        final ITexture dummyTexture = new TestTexture();

        assertTextureArg(dummyTexture, 1);
        // Different indices work
        assertTextureArg(dummyTexture, 2);

        // Strings work
        assertTextureArgNotNull("filename.jpg", 1);
        // Lua considers numbers convertible to string
        assertTextureArgNotNull(7, 1);

        // Screenshots throw an exception if not available
        TestScreenshot screenshot = new TestScreenshot();
        assertTextureArgException(screenshot, 1);
        // Screenshots work if they are available
        screenshot.setPixels(10, 10);
        assertTextureArgNotNull(screenshot, 1);

        // Null works
        assertTextureArg(null, 1);

        // Incompatible types throw an exception
        assertTextureArgException(true, 1);
        assertTextureArgException(new Object(), 1);
    }

    private void assertTextureArgException(Object javaValue, int index) {
        try {
            assertTextureArg(javaValue, index);
            Assert.fail("Expected an exception");
        } catch (ScriptException se) {
            // Expected
        }
    }

    private void assertTextureArgNotNull(Object javaValue, int index) throws ScriptException {
        Varargs varargs = createVararg(javaValue, index);
        Assert.assertNotNull(LuaConvertUtil.getTextureArg(imageModule, varargs.arg(index + 1)));
    }

    private void assertTextureArg(Object javaValue, int index) throws ScriptException {
        Varargs varargs = createVararg(javaValue, index);
        Assert.assertEquals(javaValue, LuaConvertUtil.getTextureArg(imageModule, varargs.arg(index + 1)));
    }

    private void assertLayerArgException(Object javaValue, int index) {
        try {
            assertLayerArg(javaValue, index);
            Assert.fail("Expected an exception");
        } catch (ScriptException se) {
            // Expected
        }
    }

    private void assertLayerArg(Object javaValue, int index) throws ScriptException {
        Varargs varargs = createVararg(javaValue, index);
        Assert.assertEquals(javaValue, LuaConvertUtil.getLayerArg(varargs, index + 1));
    }

    private static Varargs createVararg(Object javaValue, int index) {
        LuaValue[] vals = new LuaValue[index + 1];
        Arrays.fill(vals, LuaNil.NIL);
        vals[index] = CoerceJavaToLua.coerce(javaValue);
        return LuaValue.varargsOf(vals);
    }

}
