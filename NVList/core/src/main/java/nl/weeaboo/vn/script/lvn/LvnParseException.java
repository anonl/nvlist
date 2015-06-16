package nl.weeaboo.vn.script.lvn;

@SuppressWarnings("serial")
public class LvnParseException extends Exception {

	public LvnParseException(String filename, int line, String error) {
		super(String.format("Error parsing %s:%d -> %s", filename, line, error));
	}
	
}
