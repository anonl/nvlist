package nl.weeaboo.vn.desktop;

import java.io.File;
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
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowAdapter;
import com.google.common.collect.ImmutableList;

import joptsimple.OptionException;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.prefsstore.Preference;
import nl.weeaboo.vn.core.InitException;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.gdx.res.DesktopGdxFileSystem;
import nl.weeaboo.vn.impl.InitConfig;
import nl.weeaboo.vn.impl.Launcher;
import nl.weeaboo.vn.impl.core.NovelPrefsStore;
import nl.weeaboo.vn.input.INativeInput;
import nl.weeaboo.vn.input.KeyCode;

public final class DesktopLauncher {

    private static final Logger LOG = LoggerFactory.getLogger(DesktopLauncher.class);

    private final ImmutableList<String> args;

    private Dim windowedSize;

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
                DesktopGraphicsUtil.setWindowIcon(gdxFileSystem);
                windowedSize = DesktopGraphicsUtil.limitInitialWindowSize(Gdx.graphics);

                super.create();
            }

            @Override
            public void resize(int width, int height) {
                super.resize(width, height);

                if (!Gdx.graphics.isFullscreen()) {
                    windowedSize = Dim.of(width, height);
                }
            }

            @Override
            protected void handleInput(INativeInput input) {
                super.handleInput(input);

                DesktopLauncher.this.handleInput(input);
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

    private void handleInput(INativeInput input) {
        // Fullscreen toggle
        if (input.isPressed(KeyCode.ALT_LEFT, true) && input.consumePress(KeyCode.ENTER)) {
            if (!Gdx.graphics.isFullscreen()) {
                LOG.debug("Switch to fullscreen mode");
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            } else {
                LOG.debug("Switch to windowed mode: {}x{}", windowedSize.w, windowedSize.h);
                Gdx.graphics.setWindowedMode(windowedSize.w, windowedSize.h);
            }

            // GDX clears internal press state, so we should do the same
            input.clearButtonStates();
        }
    }

}
