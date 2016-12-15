package nl.weeaboo.vn.test.integration;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.gdx.res.DesktopGdxFileSystem;
import nl.weeaboo.gdx.test.junit.GdxLwjgl3TestRunner;
import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.vn.Launcher;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.impl.Novel;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;
import nl.weeaboo.vn.script.impl.lua.LuaScriptUtil;
import nl.weeaboo.vn.script.impl.lua.LuaTestUtil;
import nl.weeaboo.vn.test.integration.lua.LuaAssertLib;

@RunWith(GdxLwjgl3TestRunner.class)
@Category(GdxUiTest.class)
public abstract class IntegrationTest {

    protected Launcher launcher;
    protected Novel novel;
    protected IEnvironment env;

    @Before
    public final void beforeIntegration() throws ScriptException {
        launcher = new Launcher(new DesktopGdxFileSystem("", true));
        launcher.create();

        novel = launcher.getNovel();
        env = novel.getEnv();

        // Add assert lib
        new LuaAssertLib().initEnv((LuaScriptEnv)env.getScriptEnv());
    }

    @After
    public final void afterIntegration() {
        launcher.dispose();

        final Application app = Gdx.app;
        if (app != null) {
            app.exit();
        }

        // Clear static state
        StaticEnvironment.getInstance().clear();
    }

    protected void loadScript(String path) {
        loadScript(FilePath.of(path));
    }
    protected void loadScript(FilePath path) {
        IContext context = env.getContextManager().getPrimaryContext();
        try {
            LuaScriptUtil.loadScript(context, env.getScriptLoader(), path);
        } catch (IOException | ScriptException e) {
            throw new AssertionError(e);
        }
    }

    protected void waitForAllThreads() {
        LuaTestUtil.waitForAllThreads(env.getContextManager());
    }

}
