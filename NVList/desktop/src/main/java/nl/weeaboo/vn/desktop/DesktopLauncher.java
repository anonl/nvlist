package nl.weeaboo.vn.desktop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import nl.weeaboo.vn.Launcher;

public class DesktopLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(DesktopLauncher.class);

	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "NVList";
        config.width = 1280;
        config.height = 720;
        // config.vSyncEnabled = true;
        config.addIcon("icon128.png", FileType.Internal);
        config.addIcon("icon32.png", FileType.Internal);
        config.addIcon("icon16.png", FileType.Internal);

        LwjglApplication app = new LwjglApplication(new Launcher(), config);
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
