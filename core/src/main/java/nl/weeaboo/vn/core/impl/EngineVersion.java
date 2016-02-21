package nl.weeaboo.vn.core.impl;

import nl.weeaboo.common.VersionNumber;

public final class EngineVersion {

    private static final int VERSION_MAJOR = 4;
    private static final int VERSION_MINOR = 0;
    private static final VersionNumber VERSION = new VersionNumber(VERSION_MAJOR, VERSION_MINOR);

    /** The oldest target engine version we're still reasonably backwards-compatible with */
    private static final VersionNumber MIN_COMPAT_VERSION = new VersionNumber(4, 0);

    private EngineVersion() {
    }

    /**
     * @param engineMinVersion The minimum engine version the game script claims to support.
     * @param engineTargetVersion The newest engine version this game script was tested against.
     *        Backwards-compatibility hacks will treat the game script as if it were written for this
     *        particular engine version.
     * @throws UnsupportedVersionException if the engine version is too old, or if the script version is no
     *         longer supported.
     */
    public static void checkVersion(String engineMinVersion, String engineTargetVersion)
            throws UnsupportedVersionException {

        if (VERSION.compareTo(engineMinVersion) < 0) {
            // Our engine version number is too old to run the game
            throw new UnsupportedVersionException(String.format("Engine version number (%s) "
                    + "is below the minimum acceptable version (%s)", VERSION, engineMinVersion));
        }

        if (MIN_COMPAT_VERSION.compareTo(engineTargetVersion) > 0) {
            // This script targets an engine version that we're no longer backwards compatible with
            throw new UnsupportedVersionException(String.format(
                    "scriptTargetVersion is too old (%s), minimum is (%s)", engineTargetVersion,
                    MIN_COMPAT_VERSION));
        }

    }

    public static VersionNumber getEngineVersion() {
        return VERSION;
    }

}
