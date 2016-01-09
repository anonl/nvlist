package nl.weeaboo.vn.test.integration;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import nl.weeaboo.vn.Launcher;
import nl.weeaboo.vn.core.impl.Novel;

public abstract class IntegrationTest {

    protected Lwjgl3Application app;
    protected Novel novel;

    @Before
    public void beforeIntegration() throws InterruptedException {
        final Semaphore initLock = new Semaphore(0);
        Launcher launcher = new Launcher() {
            @Override
            public void create() {
                super.create();

                initLock.release();
            }
        };

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        app = new Lwjgl3Application(launcher, config);

        initLock.tryAcquire(1, 5, TimeUnit.SECONDS); // Wait for init

        novel = launcher.getNovel();
    }

    @After
    public void afterIntegration() {
        app.exit();
    }

}
