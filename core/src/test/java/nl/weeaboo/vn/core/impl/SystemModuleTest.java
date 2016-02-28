package nl.weeaboo.vn.core.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.settings.IPreferenceStore;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.core.InitException;

public class SystemModuleTest {

    private final StaticRef<INovel> novelRef = StaticEnvironment.NOVEL;
    private final StaticRef<IPreferenceStore> prefsRef = StaticEnvironment.PREFS;

    private TestSystemEnv systemEnv;
    private SystemModuleMock systemModule;

    @Before
    public void before() {
        TestEnvironment env = TestEnvironment.newInstance();
        systemEnv = env.getSystemEnv();
        systemModule = new SystemModuleMock(env);
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

}
