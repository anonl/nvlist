package nl.weeaboo.vn.script.impl.lvn;

import java.io.IOException;
import java.io.InputStream;

import nl.weeaboo.filesystem.FilePath;

public interface ILvnParser {

    ICompiledLvnFile parseFile(FilePath filename, InputStream in) throws LvnParseException, IOException;

}
