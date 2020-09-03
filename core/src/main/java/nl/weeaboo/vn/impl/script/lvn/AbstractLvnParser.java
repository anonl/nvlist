package nl.weeaboo.vn.impl.script.lvn;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import nl.weeaboo.common.StringUtil;
import nl.weeaboo.filesystem.FilePath;

abstract class AbstractLvnParser implements ILvnParser {

    @Override
    public ICompiledLvnFile parseFile(FilePath filename, String input) throws LvnParseException, IOException {
        return parseFile(filename, new ByteArrayInputStream(StringUtil.toUTF8(input)));
    }

}
