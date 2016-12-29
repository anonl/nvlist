package nl.weeaboo.vn.desktop;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.gdx.res.DesktopGdxFileSystem;
import nl.weeaboo.vn.InitConfig;
import nl.weeaboo.vn.Launcher;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.core.impl.NovelPrefsStore;

public class DesktopLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(DesktopLauncher.class);

    public static void main(String[] arg) {
//        TODO: Pass two pieces of information to the launcher:
//            - gameId
//            - virtual output toggle

        InitConfig.init();

        // Manually init Gdx.files (we need to load some resources to configure the application)
        Gdx.files = new Lwjgl3Files();
        // TODO #33: Use game ID as arcBaseName
        final DesktopGdxFileSystem gdxFileSystem = new DesktopGdxFileSystem();

        IWritableFileSystem outputFileSystem = new DesktopOutputFileSystem(FileType.Local, "save/");
        // TODO #33: Use in-memory filesystem for dev mode, real filesystem for production code
        //IWritableFileSystem outputFileSystem = new InMemoryFileSystem(false);

        final Launcher launcher = new Launcher(gdxFileSystem, outputFileSystem) {
            @Override
            public void create() {
                setWindowIcon(gdxFileSystem);

                super.create();
            }
        };

        Lwjgl3ApplicationConfiguration config = createConfig(launcher);
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

    private static void setWindowIcon(IFileSystem fileSystem) {
        // Oddly, the only public way to get a reference to the main window is through the graphics object...
        Lwjgl3Graphics graphics = (Lwjgl3Graphics)Gdx.graphics;
        Lwjgl3Window window = graphics.getWindow();

        // Try to load icons in various sizes
        List<Pixmap> pixmaps = Lists.newArrayList();
        try {
            for (String sub : new String[] {"", "16", "32", "48", "64", "128", "256", "512"}) {
                FilePath path = FilePath.of("icon" + sub + ".png");
                try {
                    byte[] bytes = FileSystemUtil.readBytes(fileSystem, path);

                    LOG.info("Loading icon: {}", path);
                    pixmaps.add(new Pixmap(bytes, 0, bytes.length));
                } catch (FileNotFoundException fnfe) {
                    // File doesn't exist
                } catch (IOException ioe) {
                    LOG.warn("Error loading icon: {}", path, ioe);
                }
            }


            window.setIcon(Iterables.toArray(pixmaps, Pixmap.class));
        } finally {
            for (Pixmap pixmap : pixmaps) {
                pixmap.dispose();
            }
        }
    }

    private static Lwjgl3ApplicationConfiguration createConfig(Launcher launcher) {
        NovelPrefsStore prefs = launcher.loadPreferences();

        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle(prefs.get(NovelPrefs.TITLE));
        config.setWindowedMode(prefs.get(NovelPrefs.WIDTH), prefs.get(NovelPrefs.HEIGHT));
        config.setWindowListener(new Lwjgl3WindowAdapter() {
            @Override
            public boolean closeRequested() {
                return launcher.onCloseRequested();
            }
        });

        return config;
    }
}
