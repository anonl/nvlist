package nl.weeaboo.vn.impl.core;

import org.junit.Test;

import nl.weeaboo.vn.impl.core.EngineVersion;
import nl.weeaboo.vn.impl.core.UnsupportedVersionException;

public class EngineVersionTest {

    private final String currentVersion = EngineVersion.getEngineVersion().toString();

    @Test(expected = UnsupportedVersionException.class)
    public void scriptTooOld() throws UnsupportedVersionException {
        EngineVersion.checkVersion(currentVersion, "1.0");
    }

    @Test(expected = UnsupportedVersionException.class)
    public void engineTooOld() throws UnsupportedVersionException {
        EngineVersion.checkVersion("999.0", "999.0");
    }

    @Test
    public void versionOk() throws UnsupportedVersionException {
        EngineVersion.checkVersion(currentVersion, currentVersion);
        EngineVersion.checkVersion("1.0", "999.0");
    }

}
