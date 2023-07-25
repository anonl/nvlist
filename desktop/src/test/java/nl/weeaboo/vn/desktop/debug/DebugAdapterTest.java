package nl.weeaboo.vn.desktop.debug;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;

import nl.weeaboo.vn.impl.test.NoExitSecurityManager;

public abstract class DebugAdapterTest {

    private Path originalScriptFolder;
    private SecurityManager oldSecurityManager;

    @Before
    public final void beforeDebugAdapterTest() {
        originalScriptFolder = NameMapping.scriptFolder;
        NameMapping.scriptFolder = Paths.get("src/test/resources/script").toAbsolutePath();

        oldSecurityManager = System.getSecurityManager();
        System.setSecurityManager(new NoExitSecurityManager());
    }

    @After
    public final void afterDebugAdapterTest() {
        NameMapping.scriptFolder = originalScriptFolder;
        System.setSecurityManager(oldSecurityManager);
    }

}
