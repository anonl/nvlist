package nl.weeaboo.vn.desktop;

import java.io.File;
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
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration.HdpiMode;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Filter;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.prefsstore.Preference;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.gdx.graphics.PixmapUtil;
import nl.weeaboo.vn.gdx.res.DesktopGdxFileSystem;
import nl.weeaboo.vn.impl.InitConfig;
import nl.weeaboo.vn.impl.Launcher;
import nl.weeaboo.vn.impl.core.NovelPrefsStore;

public final class DesktopLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(DesktopLauncher.class);

    private final ImmutableList<String> args;

    public DesktopLauncher(String[] args) {
        this.args = ImmutableList.copyOf(args);
    }

    /**
     * Main entry point for desktop platforms (Windows, Linux, MacOS).
     * @throws InitException If a fatal error occurs during initialization.
     */
    public static void main(String[] args) throws InitException {
        InitConfig.init();

        new DesktopLauncher(args).start();
    }

    /**
     * @throws InitException If a fatal error occurs during initialization.
     */
    public void start() throws InitException {
        DesktopGdxFileSystem gdxFileSystem = openResourceFileSystem(new File("."));
        IWritableFileSystem outputFileSystem = new DesktopOutputFileSystem(FileType.Local, "save/");

        final Launcher launcher = new Launcher(gdxFileSystem, outputFileSystem) {
            @Override
            public void create() {
                setWindowIcon(gdxFileSystem);

                super.create();
            }
        };

        NovelPrefsStore prefs = launcher.loadPreferences();
        handleCommandlineOptions(prefs);

        Lwjgl3ApplicationConfiguration config = createConfig(launcher, prefs);
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

    public static DesktopGdxFileSystem openResourceFileSystem(File projectFolder) {
        // Manually init Gdx.files (we need to load some resources to configure the application)
        Gdx.files = new Lwjgl3Files();

        StringBuilder path = new StringBuilder();
        path.append(projectFolder.toString().replace('\\', '/'));
        if (path.length() > 0) {
            path.append('/');
        }
        path.append("res/");
        return new DesktopGdxFileSystem(path.toString());
    }

    private static void setWindowIcon(IFileSystem fileSystem) {
        // Oddly, the only public way to get a reference to the main window is through the graphics object...
        Lwjgl3Graphics graphics = (Lwjgl3Graphics)Gdx.graphics;
        Lwjgl3Window window = graphics.getWindow();

        // Try to load icons in various sizes
        List<Pixmap> pixmaps = Lists.newArrayList();
        try {
            FilePath path = FilePath.of("icon.png");
            try {
                byte[] bytes = FileSystemUtil.readBytes(fileSystem, path);

                LOG.info("Loading icon: {}", path);
                Pixmap fullSize = new Pixmap(bytes, 0, bytes.length);
                /*
                 * Convert to RGBA8888 (libGDX will do this later anyway, doing it now makes resize behavior
                 * more predictable)
                 */
                fullSize = PixmapUtil.convert(fullSize, Format.RGBA8888, true);
                pixmaps.add(fullSize);

                // Derive smaller-sized versions of the icon (if needed)
                Pixmap previousLevel = fullSize;
                while (previousLevel.getWidth() > 16) {
                    Dim targetSize = Dim.of(previousLevel.getWidth() / 2, previousLevel.getHeight() / 2);

                    LOG.debug("Creating resized icon: {}", targetSize);
                    Pixmap pixmap = PixmapUtil.resizedCopy(previousLevel, targetSize, Filter.BiLinear);
                    pixmaps.add(0, pixmap);
                    previousLevel = pixmap;
                }
            } catch (FileNotFoundException fnfe) {
                // File doesn't exist
            } catch (IOException ioe) {
                LOG.warn("Error loading icon: {}", path, ioe);
            }

            window.setIcon(Iterables.toArray(pixmaps, Pixmap.class));
        } finally {
            for (Pixmap pixmap : pixmaps) {
                pixmap.dispose();
            }
        }
    }

    private Lwjgl3ApplicationConfiguration createConfig(Launcher launcher, NovelPrefsStore prefs) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.useVsync(false);

        /*
         * We have to use HdpiMode.Pixels here. The default mode, HdpiMode.Logical, automatically scales 'logical'
         * pixels (window coordinates) to 'raw' pixels (OpenGL backbuffer coordinates). This causes problems when trying
         * to use Viewport to render to something other than the main window (like a FBO).
         */
        config.setHdpiMode(HdpiMode.Pixels);

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

    private void handleCommandlineOptions(NovelPrefsStore prefs) throws InitException {
        LOG.info("Commandline args: {}", args);

        List<Preference<?>> declaredPrefs = NovelPrefsStore.getDeclaredPrefs(NovelPrefs.class);

        OptionParser optionParser = new OptionParser();
        for (Preference<?> pref : declaredPrefs) {
            optionParser.accepts("P" + pref.getKey()).withRequiredArg();
        }

        OptionSet options;
        try {
            options = optionParser.parse(args.toArray(new String[0]));
        } catch (OptionException oe) {
            try {
                optionParser.printHelpOn(System.out);
            } catch (IOException e) {
                LOG.error("Error printing supported commandline options", e);
            }
            throw new InitException("Error parsing commandline options", oe);
        }

        // Allow setting preference values from the commandline
        for (Preference<?> pref : declaredPrefs) {
            Object value = options.valueOf("P" + pref.getKey());
            if (value != null) {
                setPref(prefs, pref, value.toString());
            }
        }
    }

    private <T> void setPref(NovelPrefsStore prefs, Preference<T> pref, String value) {
        LOG.info("Set preference: {}={}", pref.getKey(), value);
        prefs.set(pref, pref.fromString(value));
    }

}
