package nl.weeaboo.vn.impl.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;

import com.google.common.collect.ImmutableList;

import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.ISystemModule;
import nl.weeaboo.vn.render.DisplayMode;

public class SystemModuleMock extends AbstractModule implements ISystemModule {

    private static final long serialVersionUID = 1L;

    private final IEnvironment env;
    private final AtomicInteger restartCount = new AtomicInteger();
    private final List<String> openedWebsites = new ArrayList<>();

    private transient SystemEnvMock systemEnv;

    public SystemModuleMock(IEnvironment env) {
        this.env = env;
    }

    @Override
    public void exit(boolean force) {
        if (!force) {
            SystemModule.callFunction(env, KnownScriptFunctions.ON_EXIT);
        }
    }

    @Override
    public boolean canExit() {
        return false;
    }

    @Override
    public void restart() {
        restartCount.incrementAndGet();
    }

    /**
     * Resets and checks the internal counter for the number of times that {@link #restart()} was called.
     */
    public void consumeRestartCount(int expected) {
        Assert.assertEquals(expected, restartCount.getAndSet(0));
    }

    @Override
    public void openWebsite(String url) {
        openedWebsites.add(url);
    }

    /**
     * Rests and checks the internal queue of websites opened using {@link #openWebsite(String)}.
     */
    public void consumeOpenedWebsites(String... expected) {
        ImmutableList<String> snapshot = ImmutableList.copyOf(openedWebsites);
        openedWebsites.clear();
        Assert.assertEquals(Arrays.asList(expected), snapshot);
    }

    @Override
    public SystemEnvMock getSystemEnv() {
        if (systemEnv == null) {
            systemEnv = new SystemEnvMock();
        }
        return systemEnv;
    }

    @Override
    public void setDisplayMode(DisplayMode mode) {
        getSystemEnv().setDisplayMode(mode);
    }

}
