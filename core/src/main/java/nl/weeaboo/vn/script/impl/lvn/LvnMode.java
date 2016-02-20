package nl.weeaboo.vn.script.impl.lvn;

/**
 * Line type enum for LVN files.
 */
enum LvnMode {
	TEXT(false),

	CODE(true),
	MULTILINE_CODE(false),

	COMMENT(true),
	MULTILINE_COMMENT(false);

	private final boolean singleLine;

	private LvnMode(boolean singleLine) {
		this.singleLine = singleLine;
	}

    public boolean isSingleLine() {
        return singleLine;
    }

}