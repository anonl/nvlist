package nl.weeaboo.vn.impl.script.lvn;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;

import nl.weeaboo.common.StringUtil;
import nl.weeaboo.filesystem.FilePath;

public class LvnParserTest {

    private static final Logger LOG = LoggerFactory.getLogger(LvnParserTest.class);
    private static final String scriptDir = "/script/syntax/";

    @Test
    public void syntaxTest3() throws LvnParseException, IOException {
        syntaxTest(3);
    }

    @Test
    public void syntaxTest4() throws LvnParseException, IOException {
        syntaxTest(4);
    }

    private static void syntaxTest(int version) throws IOException, LvnParseException {
        FilePath filename = FilePath.of("test");

        final ICompiledLvnFile lvnFile;
        final String contents;
        LOG.info(filename.toString());
        InputStream in = LvnParserTest.class.getResourceAsStream(scriptDir + filename + ".lvn");
        try {
            ILvnParser parser = LvnParserFactory.getParser(Integer.toString(version));
            lvnFile = parser.parseFile(filename, in);
            contents = lvnFile.getCompiledContents();
        } finally {
            in.close();
        }

        URL luaFileUrl = Resources.getResource(LvnParserTest.class, scriptDir + filename + version + ".lua");
        final byte[] checkBytes = Resources.toByteArray(luaFileUrl);

        LOG.debug(contents);

        Assert.assertEquals(StringUtil.fromUTF8(checkBytes, 0, checkBytes.length), contents);
        Assert.assertArrayEquals(checkBytes, StringUtil.toUTF8(contents));

        Assert.assertEquals(7, lvnFile.countTextLines(false));
        Assert.assertEquals(7 + 8, lvnFile.countTextLines(true));
    }


}
