package nl.weeaboo.vn.test.integration;

import org.junit.After;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

import nl.weeaboo.gdx.test.junit.GdxLwjgl3TestRunner;
import nl.weeaboo.gdx.test.junit.GdxUiTest;
import nl.weeaboo.vn.Launcher;
import nl.weeaboo.vn.core.impl.Novel;

@RunWith(GdxLwjgl3TestRunner.class)
@Category(GdxUiTest.class)
public abstract class IntegrationTest {

    protected Novel novel;

    private Launcher launcher;

    @Before
    public final void beforeIntegration() {
        Launcher launcher = new Launcher();
        launcher.create();

        novel = launcher.getNovel();
    }

    @After
    public final void afterIntegration() {
        if (launcher != null) {
            launcher.dispose();
        }

        final Application app = Gdx.app;
        if (app != null) {
            app.exit();
        }
    }

}
