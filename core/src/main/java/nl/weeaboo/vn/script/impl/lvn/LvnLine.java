package nl.weeaboo.vn.script.impl.lvn;

import nl.weeaboo.common.Checks;

final class LvnLine {

    private final String sourceLine;
    private final String compiledLine;
    private final LvnMode lineType;

    public LvnLine(String sourceLine, String compiledLine, LvnMode lineType) {
        this.sourceLine = Checks.checkNotNull(sourceLine);
        this.compiledLine = Checks.checkNotNull(compiledLine);
        this.lineType = Checks.checkNotNull(lineType);
    }

    public String getSourceLine() {
        return sourceLine;
    }

    public String getCompiledLine() {
        return compiledLine;
    }

    public LvnMode getType() {
        return lineType;
    }

}
