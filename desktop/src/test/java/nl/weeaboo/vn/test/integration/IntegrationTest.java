package nl.weeaboo.vn.test.integration;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Before;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import nl.weeaboo.vn.Launcher;
import nl.weeaboo.vn.core.impl.Novel;

public abstract class IntegrationTest {

    protected LwjglApplication app;
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

        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        app = new LwjglApplication(launcher, config);

        initLock.tryAcquire(1, 5, TimeUnit.SECONDS); // Wait for init

        novel = launcher.getNovel();
    }

    @After
    public void afterIntegration() {
        app.exit();
    }

}
