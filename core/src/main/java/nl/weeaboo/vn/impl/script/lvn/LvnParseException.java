package nl.weeaboo.vn.impl.script.lvn;

import nl.weeaboo.common.StringUtil;
import nl.weeaboo.filesystem.FilePath;

public final class LvnParseException extends Exception {

    private static final long serialVersionUID = 1L;

    public LvnParseException(FilePath filename, int line, String error) {
        super(StringUtil.formatRoot("Error parsing %s:%d -> %s", filename, line, error));
    }

}
