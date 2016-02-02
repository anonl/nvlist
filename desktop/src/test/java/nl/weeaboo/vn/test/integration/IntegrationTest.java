package nl.weeaboo.vn.test.integration;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import nl.weeaboo.vn.Launcher;
import nl.weeaboo.vn.core.impl.Novel;

public abstract class IntegrationTest {

    private static final int MAX_STARTUP_TIME_SEC = 30;

    protected Novel novel;

    private Thread initThread;

    @Before
    public void beforeIntegration() throws InterruptedException {
        final Semaphore initLock = new Semaphore(0);
        final Launcher launcher = new Launcher() {
            @Override
            public void create() {
                super.create();

                initLock.release();
            }
        };

        // Workaround for libGDX issue; Lwjgl3Application constructor contains an infinite loop (lolwut)
        initThread = new Thread() {
            @Override
            public void run() {
                Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

                @SuppressWarnings("unused")
                Lwjgl3Application app = new Lwjgl3Application(launcher, config);
            }
        };
        initThread.start();

        // Wait for init
        Assert.assertTrue(initLock.tryAcquire(1, MAX_STARTUP_TIME_SEC, TimeUnit.SECONDS));
        novel = launcher.getNovel();
    }

    @After
    public void afterIntegration() throws InterruptedException {
        final Application app = Gdx.app;
        if (app != null) {
            app.exit();
        }

        if (initThread != null) {
            initThread.join(10000);
        }
    }

}
