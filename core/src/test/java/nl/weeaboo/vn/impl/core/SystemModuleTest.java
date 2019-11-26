package nl.weeaboo.vn.impl.core;

import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.core.InitException;

public class SystemModuleTest {

    private final StaticRef<INovel> novelRef = StaticEnvironment.NOVEL;
    private final StaticRef<IPreferenceStore> prefsRef = StaticEnvironment.PREFS;

    private TestSystemEnv systemEnv;
    private TestSystemModule systemModule;

    @Before
    public void before() {
        TestEnvironment env = TestEnvironment.newInstance();
        systemEnv = env.getSystemEnv();
        systemModule = new TestSystemModule(env);
    }

    @Test
    public void canExit() {
        // SystemModule takes its canExit value from the system env
        systemEnv.setCanExit(true);
        Assert.assertEquals(true, systemModule.canExit());
        systemEnv.setCanExit(false);
        Assert.assertEquals(false, systemModule.canExit());
    }

    @Test
    public void testExit() {
        // doExit is always immediately called if force==true
        systemModule.exit(true);
        Assert.assertEquals(1, systemModule.consumeDoExitCount());
        Assert.assertEquals(null, systemModule.consumeLastCallled());

        // If calling the exit handler function fails, calls doExit
        systemModule.exit(false);
        Assert.assertEquals(1, systemModule.consumeDoExitCount());
        Assert.assertEquals(KnownScriptFunctions.ON_EXIT, systemModule.consumeLastCallled());

        // Define exit handler function, then that is called and doExit isn't
        systemModule.addFunction(KnownScriptFunctions.ON_EXIT);
        systemModule.exit(false);
        Assert.assertEquals(0, systemModule.consumeDoExitCount());
        Assert.assertEquals(KnownScriptFunctions.ON_EXIT, systemModule.consumeLastCallled());
        // doExit is still called if force==true
        systemModule.exit(true);
        Assert.assertEquals(1, systemModule.consumeDoExitCount());
        Assert.assertEquals(null, systemModule.consumeLastCallled());
    }

    /** Restart throws an init exception if no novel is set */
    @Test(expected = InitException.class)
    public void testRestartNoNovel() throws InitException {
        Assert.assertNull(novelRef.getIfPresent());
        systemModule.restart();
    }

    @Test
    public void prefsChanged() {
        IPreferenceStore prefs = prefsRef.get();
        systemModule.onPrefsChanged(prefs);
        // The appropriate script function is called
        Assert.assertEquals(KnownScriptFunctions.ON_PREFS_CHANGE, systemModule.consumeLastCallled());
    }

    private static final class TestSystemModule extends SystemModule {

        private static final long serialVersionUID = 1L;

        private final AtomicInteger doExitCalled = new AtomicInteger();
        private final AtomicReference<String> lastCalled = new AtomicReference<>();
        private final Set<String> existingFunctions = Sets.newHashSet();

        public TestSystemModule(IEnvironment env) {
            super(env);
        }

        public void addFunction(String functionName) {
            existingFunctions.add(functionName);
        }

        @Override
        protected void doExit() {
            doExitCalled.incrementAndGet();
            super.doExit();
        }

        public int consumeDoExitCount() {
            return doExitCalled.getAndSet(0);
        }

        @Override
        protected boolean callFunction(String functionName) {
            lastCalled.set(functionName);
            return existingFunctions.contains(functionName);
        }

        public @Nullable String consumeLastCallled() {
            return lastCalled.getAndSet(null);
        }

    }

}
