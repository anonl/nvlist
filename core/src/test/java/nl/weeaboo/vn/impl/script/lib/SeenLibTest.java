package nl.weeaboo.vn.impl.script.lib;

import org.junit.Test;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;
import nl.weeaboo.vn.stats.IResourceSeenLog;

public class SeenLibTest extends AbstractLibTest {

    @Override
    protected void addInitializers(LuaScriptEnv scriptEnv) {
        super.addInitializers(scriptEnv);

        scriptEnv.addInitializer(new SeenLib(env));
    }

    @Test
    public void testChoiceSeen() {
        loadScript("integration/seen/choice-seen");
    }

    @Test
    public void testLineSeen() {
        loadScript("integration/seen/line-seen");
    }

    @Test
    public void testMediaSeen() {
        IResourceSeenLog resourceLog = env.getStatsModule().getSeenLog().getResourceLog();
        resourceLog.markSeen(new ResourceId(MediaType.IMAGE, FilePath.of("a.png")));
        resourceLog.markSeen(new ResourceId(MediaType.SOUND, FilePath.of("alpha.ogg")));
        resourceLog.markSeen(new ResourceId(MediaType.VIDEO, FilePath.of("blank.webm")));

        loadScript("integration/seen/media-seen");

        LuaTestUtil.assertGlobal("seenImage", true);
        LuaTestUtil.assertGlobal("seenSound", true);
        LuaTestUtil.assertGlobal("seenVideo", true);
    }

}
