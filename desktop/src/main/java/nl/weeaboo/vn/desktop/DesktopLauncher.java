package nl.weeaboo.vn.desktop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;

import nl.weeaboo.gdx.res.DesktopGdxFileSystem;
import nl.weeaboo.vn.Launcher;

public class DesktopLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(DesktopLauncher.class);

    public static void main(String[] arg) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("NVList");
        config.setWindowedMode(1280, 720);

        // TODO: The relative paths change between development and release
        config.setWindowIcon(FileType.Internal,
                "res/icon128.png",
                "res/icon32.png",
                "res/icon16.png");

        DesktopGdxFileSystem gdxFileSystem = new DesktopGdxFileSystem("res/", true);

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
