package nl.weeaboo.vn.script.lvn;

import java.io.IOException;
import java.io.InputStream;

public interface ILvnParser {

	public ICompiledLvnFile parseFile(String filename, InputStream in) throws LvnParseException, IOException;

}
