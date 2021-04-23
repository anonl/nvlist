package nl.weeaboo.vn.desktop;

import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.InMemoryFileSystem;
import nl.weeaboo.gdx.test.junit.GdxLwjgl3TestRunner;
import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.impl.core.Novel;
import nl.weeaboo.vn.impl.core.NovelPrefsStore;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.script.ThrowingScriptExceptionHandler;
import nl.weeaboo.vn.impl.script.lua.LuaScriptEnv;
import nl.weeaboo.vn.impl.script.lua.LuaScriptUtil;
import nl.weeaboo.vn.impl.script.lua.LuaTestUtil;
import nl.weeaboo.vn.impl.test.FileSystemMock;
import nl.weeaboo.vn.impl.test.integration.lua.LuaAssertLib;
import nl.weeaboo.vn.script.ScriptException;

@RunWith(GdxLwjgl3TestRunner.class)
@Category(GdxUiTest.class)
public abstract class DesktopIntegrationTest {

    protected DesktopLauncher launcher;
    protected Novel novel;

    @BeforeClass
    public static final void beforeAllIntegration() {
        HeadlessGdx.init();
    }

    @Before
    public final void beforeIntegration() throws ScriptException {
        if (getClass().getAnnotation(Category.class) == null) {
            throw new IllegalStateException("pitest doesn't support category inheritance, so a category annotation"
                    + "must be repeated on every test class");
        }

        Lwjgl3NativesLoader.load();

        launcher = new DesktopLauncher(FileSystemMock.newGdxFileSystem(), new InMemoryFileSystem(false));

        NovelPrefsStore prefs = launcher.loadPreferences();
        prefs.set(NovelPrefs.DEBUG, true);
        launcher.create();

        novel = launcher.getNovel();
        IEnvironment env = getEnv();
        env.getScriptEnv().setExceptionHandler(ThrowingScriptExceptionHandler.INSTANCE);

        // Add assert lib
        new LuaAssertLib().initEnv((LuaScriptEnv)env.getScriptEnv());
    }

    @After
    public final void afterIntegration() {
        launcher.dispose();

        // Clear static state
        StaticEnvironment.getInstance().clear();
    }

    @AfterClass
    public static final void afterAllIntegration() {
        HeadlessGdx.clear();
    }

    protected IEnvironment getEnv() {
        return novel.getEnv();
    }

    protected void loadScript(String path) {
        loadScript(FilePath.of(path));
    }

    protected void loadScript(FilePath path) {
        IContext context = getEnv().getContextManager().getPrimaryContext();
        try {
            LuaScriptUtil.loadScript(context, getEnv().getScriptEnv().getScriptLoader(), path);
        } catch (IOException | ScriptException e) {
            throw new AssertionError(e);
        }
    }

    protected void waitForAllThreads() {
        LuaTestUtil.waitForAllThreads(novel);
    }

    protected Lwjgl3Window getWindow() {
        return ((Lwjgl3Graphics)Gdx.graphics).getWindow();
    }

}
