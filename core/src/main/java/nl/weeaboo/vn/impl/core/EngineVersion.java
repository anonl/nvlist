package nl.weeaboo.vn.impl.core;

import nl.weeaboo.common.VersionNumber;

/**
 * Game engine version.
 */
public final class EngineVersion {

    private static final String VERSION_STRING = "4.9.4";
    private static final VersionNumber VERSION = VersionNumber.parse(
            // Take only major.minor part of the version number
            VERSION_STRING.replaceAll("(\\d+\\.\\d+).*", "$1"));

    /** The oldest target engine version we're still mostly backwards-compatible with */
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

    /**
     * Returns the current version.
     */
    public static VersionNumber getEngineVersion() {
        return VERSION;
    }

    /**
     * Returns the full version number as a string. To compare versions for compatibility, use
     * {@link #getEngineVersion()} instead.
     */
    public static String getEngineVersionString() {
        return VERSION_STRING;
    }
}
