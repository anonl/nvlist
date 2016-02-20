package nl.weeaboo.vn.script.impl.lvn;

public final class LvnParseException extends Exception {

    private static final long serialVersionUID = 1L;

    public LvnParseException(String filename, int line, String error) {
		super(String.format("Error parsing %s:%d -> %s", filename, line, error));
	}

}
