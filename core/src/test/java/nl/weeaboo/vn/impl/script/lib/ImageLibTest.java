package nl.weeaboo.vn.impl.script.lib;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.lua2.luajava.CoerceLuaToJava;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;

public class ImageLibTest extends AbstractLibTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        super.addInitializers(scriptEnv);

        scriptEnv.addInitializer(new ImageLib(env));
    }

    @Test
    public void testPreload() {
        loadScript("integration/image/preload");
    }

    @Test
    public void testGetImageFiles() {
        loadScript("integration/image/getimagefiles");

        // Lib function delegates to ImageModule
        String[] expected = env.getImageModule().getImageFiles(FilePath.of("button"))
                .stream()
                .map(FilePath::toString)
                .toArray(String[]::new);
        Assert.assertArrayEquals(expected,
                CoerceLuaToJava.coerceArg(LuaTestUtil.getGlobal("files"), String[].class));
    }

    @Test
    public void testColorTextures() {
        loadScript("integration/image/colortextures");
    }

}
