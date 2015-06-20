package nl.weeaboo.vn.script.lvn;

import static nl.weeaboo.vn.LvnTestUtil.deserializeObject;
import static nl.weeaboo.vn.LvnTestUtil.serializeObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import com.google.common.io.Resources;

import nl.weeaboo.collections.IntMap;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.lua2.lib.LuajavaLib;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.vn.script.lvn.RuntimeTextParser.ParseResult;
import nl.weeaboo.vn.script.lvn.TextParser.Token;

public class LvnParserTest {

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
        String filename = "test";

        final ICompiledLvnFile lvnFile;
        final String contents;
        System.err.println(filename);
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

        System.out.println(contents);

        Assert.assertEquals(StringUtil.fromUTF8(checkBytes, 0, checkBytes.length), contents);
        Assert.assertArrayEquals(checkBytes, StringUtil.toUTF8(contents));

        Assert.assertEquals(7, lvnFile.countTextLines(false));
        Assert.assertEquals(7+8, lvnFile.countTextLines(true));
	}

	@Test
	public void textParserTest() throws IOException, ClassNotFoundException {
		String input = "Text with [embedded()] code and {tag a,b,c,d}embedded tags{/tag} and ${stringifiers} too.";

		TextParser parser = new TextParser();

		System.out.println("----------------------------------------");
		for (Token token : parser.tokenize(input)) {
			System.out.println(token);
		}
		System.out.println("----------------------------------------");

		LuaTable debugTable = createDebugFunctions();

		RuntimeTextParser runtimeParser = new RuntimeTextParser(debugTable);

		// Serialize->Deserialize to make sure that doesn't break anything
        runtimeParser = deserializeObject(serializeObject(runtimeParser), RuntimeTextParser.class);

		ParseResult parseResult = runtimeParser.parse(input);
		StyledText stext = parseResult.getText();
		System.out.println(stext);
		for (int n = 0; n < stext.length(); n++) {
			TextStyle style = stext.getStyle(n);
			if (style != null && style.getTags().length > 0) {
				System.out.print('*');
			} else {
				System.out.print(' ');
			}
		}
		System.out.println();
		IntMap<String> commandMap = parseResult.getCommands();
		for (int n = 0; n < commandMap.size(); n++) {
			System.out.println(commandMap.keyAt(n) + ": " + commandMap.valueAt(n));
		}
		System.out.println("----------------------------------------");

	}

    @SuppressWarnings("serial")
	private static LuaTable createDebugFunctions() {
	    LuaTable table = new LuaTable();
	    table.set(RuntimeTextParser.F_STRINGIFY, new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue arg) {
                System.out.println("stringify: " + arg);
                return arg;
            }
        });
        table.set(RuntimeTextParser.F_TAG_OPEN, new VarArgFunction() {
            @Override
            public Varargs invoke(Varargs args) {
                System.out.println("tagOpen: " + args.arg1() + " " + args.arg(2));
                return varargsOf(valueOf(""), LuajavaLib.toUserdata(TextStyle.withTags(1337), TextStyle.class));
            }
        });
        table.set(RuntimeTextParser.F_TAG_CLOSE, new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue name, LuaValue args) {
                System.out.println("tagClose: " + name + " " + args);
                return valueOf("");
            }
        });
        return table;
	}

}
