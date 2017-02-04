package nl.weeaboo.vn.impl.script.lvn;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Resources;

import nl.weeaboo.collections.IntMap;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.lua2.lib.OneArgFunction;
import nl.weeaboo.lua2.lib.TwoArgFunction;
import nl.weeaboo.lua2.lib.VarArgFunction;
import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.test.SerializeTester;
import nl.weeaboo.vn.impl.script.lvn.RuntimeTextParser.ParseResult;
import nl.weeaboo.vn.impl.script.lvn.TextParser.Token;

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

    @Test
    public void textParserTest() {
        String input = "Text with [embedded()] code and {tag a,b,c,d}embedded tags{/tag} and ${stringifiers} too.";

        TextParser parser = new TextParser();

        LOG.debug("----------------------------------------");
        for (Token token : parser.tokenize(input)) {
            LOG.debug(token.toString());
        }
        LOG.debug("----------------------------------------");

        LuaTable debugTable = createDebugFunctions();

        RuntimeTextParser runtimeParser = new RuntimeTextParser(debugTable);

        // Serialize->Deserialize to make sure that doesn't break anything
        runtimeParser = SerializeTester.reserialize(runtimeParser);

        ParseResult parseResult = runtimeParser.parse(input);
        IntMap<String> commandMap = parseResult.getCommands();
        for (int n = 0; n < commandMap.size(); n++) {
            LOG.debug(commandMap.keyAt(n) + ": " + commandMap.valueAt(n));
        }
        LOG.debug("----------------------------------------");

    }

    @SuppressWarnings("serial")
    private static LuaTable createDebugFunctions() {
        LuaTable table = new LuaTable();
        table.set(RuntimeTextParser.F_STRINGIFY, new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                LOG.debug("stringify: " + arg);
                return arg;
            }
        });
        table.set(RuntimeTextParser.F_TAG_OPEN, new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                LOG.debug("tagOpen: " + args.arg1() + " " + args.arg(2));
                return varargsOf(valueOf(""), LuajavaLib.toUserdata(TextStyle.defaultInstance(), TextStyle.class));
            }
        });
        table.set(RuntimeTextParser.F_TAG_CLOSE, new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue name, LuaValue args) {
                LOG.debug("tagClose: " + name + " " + args);
                return valueOf("");
            }
        });
        return table;
    }

}
