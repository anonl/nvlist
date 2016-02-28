package nl.weeaboo.vn.core.impl;

import org.junit.Test;

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
