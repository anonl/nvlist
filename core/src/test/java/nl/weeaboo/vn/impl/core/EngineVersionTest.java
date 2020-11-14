package nl.weeaboo.vn.impl.core;

import org.junit.Assert;
import org.junit.Test;

import nl.weeaboo.common.VersionNumber;

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

    @Test
    public void testVersionString() {
        VersionNumber version = EngineVersion.getEngineVersion();
        String fullVersion = EngineVersion.getEngineVersionString();
        Assert.assertTrue(fullVersion.startsWith(version.toString()));
    }
}
