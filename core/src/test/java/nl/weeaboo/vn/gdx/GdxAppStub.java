package nl.weeaboo.vn.gdx;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.ApplicationLogger;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.badlogic.gdx.backends.headless.HeadlessPreferences;
import com.badlogic.gdx.utils.Clipboard;

/** Stateless GDX application for use in unit tests */
final class GdxAppStub implements Application {

    private static final Logger LOG = LoggerFactory.getLogger(GdxAppStub.class);

    private final HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();

    private final ApplicationAdapter appListener;

    private ApplicationLogger appLogger;
    private int logLevel = LOG_INFO;

    public GdxAppStub() {
        appListener = new ApplicationAdapter() {
        };
    }

    @Override
    public ApplicationListener getApplicationListener() {
        return appListener;
    }

    @Override
    public Graphics getGraphics() {
        return Gdx.graphics;
    }

    @Override
    public Audio getAudio() {
        return Gdx.audio;
    }

    @Override
    public Input getInput() {
        return Gdx.input;
    }

    @Override
    public Files getFiles() {
        return Gdx.files;
    }

    @Override
    public Net getNet() {
        return Gdx.net;
    }

    @Override
    public void log(String tag, String message) {
        log(tag, message, null);
    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        if (getLogLevel() >= LOG_INFO) {
            LOG.info("[{}] {}", tag, message, exception);
        }
    }

    @Override
    public void error(String tag, String message) {
        error(tag, message, null);
    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        if (getLogLevel() >= LOG_ERROR) {
            LOG.error("[{}] {}", tag, message, exception);
        }
    }

    @Override
    public void debug(String tag, String message) {
        debug(tag, message, null);
    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        if (getLogLevel() >= LOG_DEBUG) {
            LOG.debug("[{}] {}", tag, message, exception);
        }
    }

    @Override
    public void setLogLevel(int logLevel) {
        this.logLevel = logLevel;
    }

    @Override
    public int getLogLevel() {
        return logLevel;
    }

    @Override
    public ApplicationType getType() {
        return ApplicationType.HeadlessDesktop;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public long getJavaHeap() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }

    @Override
    public long getNativeHeap() {
        return getJavaHeap();
    }

    @Override
    public Preferences getPreferences(String name) {
        return new HeadlessPreferences(name, config.preferencesDirectory);
    }

    @Override
    public Clipboard getClipboard() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void postRunnable(Runnable runnable) {
        runnable.run();
    }

    @Override
    public void exit() {
        // Doesn't have a lifecycle
    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        // Doesn't have a lifecycle
    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        // Doesn't have a lifecycle
    }

    @Override
    public void setApplicationLogger(ApplicationLogger applicationLogger) {
        this.appLogger = applicationLogger;
    }

    @Override
    public ApplicationLogger getApplicationLogger() {
        return appLogger;
    }

}
