package nl.weeaboo.vn.impl.script.lvn;

import nl.weeaboo.common.VersionNumber;

public final class LvnParserFactory {

    /**
     * @see #getParser(VersionNumber)
     */
    public static ILvnParser getParser(String engineVersion) {
        return getParser(VersionNumber.parse(engineVersion));
    }

    /** Returns the appropriate {@link ILvnParser} instance for the given NVList engine version number. */
    public static ILvnParser getParser(VersionNumber engineVersion) {
        if (engineVersion.compareTo("4.0") < 0) {
            return new LvnParser3();
        }
        return new LvnParser4();
    }

}
