package nl.weeaboo.vn.impl.script.lvn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.testing.SerializableTester;

import nl.weeaboo.collections.IntMap;
import nl.weeaboo.lua2.lib.OneArgFunction;
import nl.weeaboo.lua2.lib.TwoArgFunction;
import nl.weeaboo.lua2.lib.VarArgFunction;
import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.LuaFunction;
import nl.weeaboo.lua2.vm.LuaNil;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.styledtext.MutableStyledText;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.vn.impl.script.lvn.RuntimeTextParser.ParseResult;

public final class RuntimeTextParserTest {

    private static final Logger LOG = LoggerFactory.getLogger(RuntimeTextParserTest.class);

    private final StringifierFunction stringifierFunction = new StringifierFunction();
    private final TagOpenFunction tagOpenFunction = new TagOpenFunction();
    private final TagCloseFunction tagCloseFunction = new TagCloseFunction();

    private LuaTable debugTable;
    private RuntimeTextParser runtimeParser;

    @Before
    public void before() {
        debugTable = createDebugFunctions(stringifierFunction, tagOpenFunction, tagCloseFunction);
        runtimeParser = new RuntimeTextParser(debugTable);
    }

    @Test
    public void testSerialize() {
        runtimeParser = SerializableTester.reserialize(runtimeParser);

        ParseResult parseResult = runtimeParser.parse("Test");
        Assert.assertEquals("Test", parseResult.getText().toString());
    }

    @Test
    public void textParserTest() {
        String input = "Text with [embedded()] code and {tag=a,b,c,d}embedded tags{/tag} and ${stringifiers} too.";

        ParseResult parseResult = runtimeParser.parse(input);

        IntMap<String> commandMap = parseResult.getCommands();
        Assert.assertEquals("embedded()", commandMap.get(10));
    }

    @Test
    public void testOpenTagParser() {
        assertTagOpenParse("{tag}", "tag");
        assertTagOpenParse("{tag=a}", "tag", "a");
        assertTagOpenParse("{tag=a,b}", "tag", "a", "b");

        // Whitespace is skipped
        assertTagOpenParse("{ tag = a , b }", "tag", "a", "b");

        // Trailing comma is ignored
        assertTagOpenParse("{tag=a,b,}", "tag", "a", "b");

        // Empty argument sequences are ignored
        assertTagOpenParse("{tag=a,,b}", "tag", "a", "b");

        // Tag names can only contain letters -- symbols aren't allowed. Invalid tags are ignored.
        ParseResult result = runtimeParser.parse("{tag%}");
        Assert.assertEquals(StyledText.EMPTY_STRING, result.getText());
    }

    @Test
    public void testCloseTagParser() {
        // Single tag + closing tag
        assertParsedText("{b}bold{/b}normal",
                TextStyle.BOLD, "bold", TextStyle.defaultInstance(), "normal");

        // Nested tags
        assertParsedText("{b}bold{i}bolditalic{/i}bold{/b}",
                TextStyle.BOLD, "bold", TextStyle.BOLD_ITALIC, "bolditalic", TextStyle.BOLD, "bold");

        // Mismatched tags
        assertParsedText("{b}bold{i}bolditalic{/b}italic{/i}",
                TextStyle.BOLD, "bold", TextStyle.BOLD_ITALIC, "bolditalic", TextStyle.ITALIC, "italic");

        // Closing tag without a matching opening tag
        assertParsedText("{b}bold{/a}bold",
                TextStyle.BOLD, "bold", TextStyle.BOLD, "bold");
    }

    @Test
    public void testStringifier() {
        stringifierFunction.result = new StyledText("abc");
        assertStringifier("x${test}x", new StyledText("xabcx"));
        assertStringifier("x$test", new StyledText("xabc"));

        StyledText bold = new StyledText("bold", TextStyle.BOLD);
        stringifierFunction.result = bold;
        assertStringifier("${test}", bold);
    }

    @Test
    public void testMissingHandlers() {
        debugTable.rawset(RuntimeTextParser.F_STRINGIFY, LuaNil.NIL);
        debugTable.rawset(RuntimeTextParser.F_TAG_OPEN, LuaNil.NIL);
        debugTable.rawset(RuntimeTextParser.F_TAG_CLOSE, LuaNil.NIL);

        // If not tag/stringifier handlers are registered, the tags/stringifiers are simply ignored
        assertStringifier("{b}${test}{/b}", StyledText.EMPTY_STRING);
    }

    private void assertStringifier(String lineToParse, StyledText expected) {
        ParseResult result = runtimeParser.parse(lineToParse);

        Assert.assertEquals(expected, result.getText());
    }

    private void assertParsedText(String lineToParse, Object... styleOrStrings) {
        ParseResult result = runtimeParser.parse(lineToParse);

        MutableStyledText mst = new MutableStyledText();
        TextStyle curStyle = TextStyle.defaultInstance();
        for (Object styleOrString : styleOrStrings) {
            if (styleOrString instanceof TextStyle) {
                curStyle = (TextStyle)styleOrString;
            } else {
                mst.append(new StyledText((String)styleOrString, curStyle));
            }
        }

        Assert.assertEquals(mst.immutableCopy(), result.getText());
    }

    private void assertTagOpenParse(String lineToParse, String expectedTag, String... expectedValues) {
        runtimeParser.parse(lineToParse);

        Varargs args = tagOpenFunction.tagOpenArgs.remove(0);
        Assert.assertEquals(Arrays.asList(), tagOpenFunction.tagOpenArgs);

        Assert.assertEquals(expectedTag, args.arg(1).tojstring());
        LuaTable actualParams = args.arg(2).checktable();
        for (int n = 0; n < expectedValues.length; n++) {
            Assert.assertEquals(expectedValues[n], actualParams.rawget(1 + n).tojstring());
        }
    }

    private static LuaTable createDebugFunctions(LuaFunction stringifierFunction, LuaFunction tagOpenFunction,
            LuaFunction tagCloseFunction) {

        LuaTable table = new LuaTable();
        table.set(RuntimeTextParser.F_STRINGIFY, stringifierFunction);
        table.set(RuntimeTextParser.F_TAG_OPEN, tagOpenFunction);
        table.set(RuntimeTextParser.F_TAG_CLOSE, tagCloseFunction);
        return table;
    }

    private static final class StringifierFunction extends OneArgFunction {

        private static final long serialVersionUID = 1L;

        @Nullable StyledText result = null;

        @Override
        public LuaValue call(LuaValue arg) {
            LOG.debug("stringify: " + arg);

            if (result == null) {
                return arg;
            } else {
                return LuajavaLib.toUserdata(result, StyledText.class);
            }
        }
    }

    private static final class TagOpenFunction extends VarArgFunction {

        private static final long serialVersionUID = 1L;

        private final List<Varargs> tagOpenArgs = new ArrayList<>();

        @Override
        public Varargs invoke(Varargs args) {
            String tag = args.arg1().tojstring();

            LOG.debug("tagOpen: " + tag + " " + args.arg(2));
            tagOpenArgs.add(args);

            TextStyle style = TextStyle.defaultInstance();
            if (Objects.equals(tag, "b")) {
                style = TextStyle.BOLD;
            } else if (Objects.equals(tag, "i")) {
                style = TextStyle.ITALIC;
            }

            return varargsOf(valueOf(""), LuajavaLib.toUserdata(style, TextStyle.class));
        }
    }

    private static final class TagCloseFunction extends TwoArgFunction {

        private static final long serialVersionUID = 1L;

        @Override
        public LuaValue call(LuaValue name, LuaValue args) {
            LOG.debug("tagClose: " + name + " " + args);
            return valueOf("");
        }
    }

}
