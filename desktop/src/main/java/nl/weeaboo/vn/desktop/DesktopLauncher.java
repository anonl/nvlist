package nl.weeaboo.vn.desktop;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.badlogic.gdx.graphics.glutils.HdpiMode;
import com.google.common.collect.ImmutableList;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.prefsstore.Preference;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.desktop.debug.NvlistDebugLauncher;
import nl.weeaboo.vn.gdx.res.DesktopGdxFileSystem;
import nl.weeaboo.vn.impl.InitConfig;
import nl.weeaboo.vn.impl.Launcher;
import nl.weeaboo.vn.impl.core.NovelPrefsStore;

/**
 * Main entrypoint for desktop operating systems (Windows, Mac, Linux).
 */
public final class DesktopLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(DesktopLauncher.class);

    private final ImmutableList<String> args;

    public DesktopLauncher(String[] args) {
        this.args = ImmutableList.copyOf(args);
    }

    /**
     * Main entry point for desktop platforms (Windows, Linux, MacOS).
     */
    public static void main(String[] args) {
        try {
            InitConfig.init();

            new DesktopLauncher(args).start();
        } catch (Exception e) {
            LOG.error("Fatal error during init", e);
            System.exit(1);
        }
    }

    /**
     * @throws InitException If a fatal error occurs during initialization.
     */
    public void start() throws InitException {
        DesktopGdxFileSystem gdxFileSystem = openResourceFileSystem(new File("."));
        IWritableFileSystem outputFileSystem = new DesktopOutputFileSystem(FileType.Local, "save/");

        final Launcher launcher = new Launcher(gdxFileSystem, outputFileSystem) {

            private @Nullable NvlistDebugLauncher debugLauncher;

            @Override
            public void create() {
                DesktopGraphicsUtil.setWindowIcon(gdxFileSystem);
                NovelPrefsStore prefs = loadPreferences();
                DesktopGraphicsUtil.limitInitialWindowSize(Gdx.graphics);

                if (prefs.get(NovelPrefs.DEBUG)) {
                    debugLauncher = NvlistDebugLauncher.launch(prefs.get(NovelPrefs.DEBUG_ADAPTER_PORT),
                            Gdx.app::postRunnable);
                }

                super.create();

                if (prefs.get(NovelPrefs.FULLSCREEN) && !prefs.get(NovelPrefs.DEBUG)) {
                    /*
                     * If in debug mode, never start full-screen. Full-screen is annoying when you need to use
                     * a bunch of other windows/programs at the same time.
                     *
                     * Must activate fullscreen mode after creating the window, or else the window is created
                     * without window decorations.
                     */
                    Gdx.graphics.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
                }
            }

            @Override
            public void dispose() {
                super.dispose();

                if (debugLauncher != null) {
                    debugLauncher.close();
                }
            }
        };

        NovelPrefsStore prefs = launcher.loadPreferences();
        handleCommandlineOptions(prefs);

        Lwjgl3ApplicationConfiguration config = createConfig(launcher, prefs);
        // Note: The Lwjgl3Application constructor contains an infinite loop
        @SuppressWarnings("unused")
        Lwjgl3Application app = new Lwjgl3Application(launcher, config) {

        };
    }

    /**
     * Opens the virtual file system for reading game resources (res folder + .nvl files)
     */
    public static DesktopGdxFileSystem openResourceFileSystem(File projectFolder) {
        // Manually init Gdx.files (we need to load some resources to configure the application)
        Gdx.files = new Lwjgl3Files();

        return new DesktopGdxFileSystem(new File(projectFolder, "res"));
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

        config.setTitle(getWindowTitle(prefs));
        config.setWindowedMode(prefs.get(NovelPrefs.WIDTH), prefs.get(NovelPrefs.HEIGHT));
        config.setWindowListener(new Lwjgl3WindowAdapter() {
            @Override
            public boolean closeRequested() {
                return launcher.onCloseRequested();
            }
        });

        return config;
    }

    private static String getWindowTitle(NovelPrefsStore prefs) {
        String title = prefs.get(NovelPrefs.TITLE);
        if (prefs.get(NovelPrefs.DEBUG)) {
            title += " (debug)";
        }
        return title;
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
