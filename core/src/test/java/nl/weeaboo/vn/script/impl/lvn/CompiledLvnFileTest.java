package nl.weeaboo.vn.script.impl.lvn;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Lists;

import nl.weeaboo.vn.script.impl.lvn.CompiledLvnFile;
import nl.weeaboo.vn.script.impl.lvn.LvnLine;
import nl.weeaboo.vn.script.impl.lvn.LvnMode;

public class CompiledLvnFileTest {

    private static final String FILENAME = "test.lvn";

    @Test
    public void linesCount() {
        CompiledLvnFile compiled = compile(" ", "text", "#comment", "@code");
        Assert.assertEquals(1, compiled.countTextLines(false));
        Assert.assertEquals(2, compiled.countTextLines(true));
    }

    @Test
    public void wordsCount() {
        CompiledLvnFile compiled = compile(
                " one -- two ",
                "three\nfour\tfive.",
                "#comment doesn't count",
                "@code doesn't count");
        Assert.assertEquals(5, compiled.countTextWords());
    }

    private CompiledLvnFile compile(String... lines) {
        List<LvnLine> result = Lists.newArrayList();
        for (String line : lines) {
            LvnLine compiled;
            if (line.startsWith("@")) {
                compiled = new LvnLine(line, line.substring(1), LvnMode.CODE);
            } else if (line.startsWith("#")) {
                compiled = new LvnLine(line, line.substring(1), LvnMode.COMMENT);
            } else {
                compiled = new LvnLine(line, line, LvnMode.TEXT);
            }
            result.add(compiled);
        }
        return new CompiledLvnFile(FILENAME, result);
    }

}
