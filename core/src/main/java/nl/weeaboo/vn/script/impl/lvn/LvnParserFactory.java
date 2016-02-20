package nl.weeaboo.vn.script.impl.lvn;

import nl.weeaboo.common.VersionNumber;

public final class LvnParserFactory {

    public static ILvnParser getParser(String engineVersion) {
        return getParser(VersionNumber.parse(engineVersion));
    }
    public static ILvnParser getParser(VersionNumber engineVersion) {
        if (engineVersion.compareTo("4.0") < 0) {
            return new LvnParser3();
        }
        return new LvnParser4();
    }

}
