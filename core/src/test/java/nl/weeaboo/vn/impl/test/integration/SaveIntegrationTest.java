package nl.weeaboo.vn.impl.test.integration;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import nl.weeaboo.common.Dim;
import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.vn.impl.image.PixelTextureData;
import nl.weeaboo.vn.impl.image.ScreenshotMock;
import nl.weeaboo.vn.impl.image.TestImageUtil;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;
import nl.weeaboo.vn.save.ISaveFileHeader;
import nl.weeaboo.vn.save.IStorage;
import nl.weeaboo.vn.save.SaveFormatException;
import nl.weeaboo.vn.save.ThumbnailInfo;

@Category(GdxUiTest.class)
public final class SaveIntegrationTest extends IntegrationTest {

    @Test
    public void saveLoad() throws SaveFormatException, IOException {
        PixelTextureData textureData = TestImageUtil.newTestTextureData(10, 10);
        ScreenshotMock ss = new ScreenshotMock();
        ss.setPixels(textureData, Dim.of(1280, 720));
        LuaTestUtil.setGlobal("screenshot", ss);

        loadScript("integration/save/saveload.lvn");
        waitForAllThreads();

        ISaveFileHeader saveHeader = env.getSaveModule().readSaveHeader(1);

        // Thumbnail image is stored at the size specified in the Lua script
        ThumbnailInfo thumbnail = saveHeader.getThumbnail();
        Assert.assertEquals(Dim.of(100, 100), thumbnail.getImageSize());

        // UserData is stored properly
        IStorage userData = saveHeader.getUserData();
        Assert.assertEquals("value", userData.getString("key", "default"));
    }

    /**
     * Create/delete multiple save files
     */
    @Test
    public void manageSaves() {
        loadScript("integration/save/managesaves.lvn");

        waitForAllThreads();
    }

    @Test
    public void testQuickSaveLoad() {
        loadScript("integration/save/quicksaveload.lvn");
        LuaTestUtil.assertGlobal("x", 1);
    }

}
