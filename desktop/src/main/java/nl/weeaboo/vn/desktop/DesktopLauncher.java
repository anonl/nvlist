package nl.weeaboo.vn.desktop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;

import nl.weeaboo.gdx.res.DesktopGdxFileSystem;
import nl.weeaboo.vn.Launcher;

public class DesktopLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(DesktopLauncher.class);

    public static void main(String[] arg) {
        // Manually init Gdx.files (we need to load some resources to configure the application)
        Gdx.files = new Lwjgl3Files();

        // TODO #33: Use game ID as arcBaseName
        DesktopGdxFileSystem gdxFileSystem = new DesktopGdxFileSystem();

        // TODO #33: Init the full filesystem here, including file write support

        // TODO #33: Load NovelPrefs so we can pass the title/resolution to the app config
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("NVList");
        config.setWindowedMode(1280, 720);

        // TODO: The relative paths change between development and release
        config.setWindowIcon(FileType.Internal,
                "res/icon128.png",
                "res/icon32.png",
                "res/icon16.png");

        final Launcher launcher = new Launcher(gdxFileSystem);
        config.setWindowListener(new Lwjgl3WindowAdapter() {
            @Override
            public boolean closeRequested() {
                return launcher.onCloseRequested();
            }
        });

        Lwjgl3Application app = new Lwjgl3Application(launcher, config);
        app.addLifecycleListener(new LifecycleListener() {

            @Override
            public void resume() {
                LOG.info("App resume");
            }

            @Override
            public void pause() {
                LOG.info("App pause");
            }

            @Override
            public void dispose() {
                LOG.info("App dispose");
            }
        });
	}
}
