package nl.weeaboo.vn.script.impl.lvn;

import java.io.IOException;
import java.io.InputStream;

public interface ILvnParser {

    ICompiledLvnFile parseFile(String filename, InputStream in) throws LvnParseException, IOException;

}
