package nl.weeaboo.vn.impl.script.lvn;

/**
 * Line type enum for LVN files.
 */
public enum LvnMode {
    TEXT(false),

    CODE(true),
    MULTILINE_CODE(false),

    COMMENT(true),
    MULTILINE_COMMENT(false);

    private final boolean singleLine;

    LvnMode(boolean singleLine) {
        this.singleLine = singleLine;
    }

    public boolean isSingleLine() {
        return singleLine;
    }

}