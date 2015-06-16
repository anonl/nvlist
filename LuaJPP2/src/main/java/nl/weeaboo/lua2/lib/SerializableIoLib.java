package nl.weeaboo.lua2.lib;

import java.io.FileNotFoundException;
import java.io.IOException;

import nl.weeaboo.lua2.io.LuaSerializable;

import org.luaj.vm2.lib.IoLib;

@LuaSerializable
public class SerializableIoLib extends IoLib {

	private static final long serialVersionUID = -3479467845682001638L;

	@Override
	protected File wrapStdin() throws IOException {
		throw new IOException("Unable to access stdin");
	}

	@Override
	protected File wrapStdout() throws IOException {
		throw new IOException("Unable to access stdout");
	}

	@Override
	protected File openFile(String filename, boolean readMode, boolean appendMode, boolean updateMode,
			boolean binaryMode) throws IOException
	{
		throw new FileNotFoundException(filename);
	}

	@Override
	protected File tmpFile() throws IOException {
		throw new FileNotFoundException("Unable to create temp file");
	}

	@Override
	protected File openProgram(String prog, String mode) throws IOException {
		throw new FileNotFoundException(prog);
	}

}
