package nl.weeaboo.vn.impl.script.lib;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import nl.weeaboo.lua2.vm.LuaConstants;
import nl.weeaboo.lua2.vm.LuaDouble;
import nl.weeaboo.lua2.vm.LuaNil;
import nl.weeaboo.lua2.vm.LuaString;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.styledtext.StyleParseException;
import nl.weeaboo.styledtext.TextStyle;

public final class BasicTagHandlerTest {

    private BasicTagHandler handler;
    private Set<String> untestedTags;

    @Before
    public void before() {
        handler = new BasicTagHandler();
        untestedTags = new HashSet<>(BasicTagHandler.getSupportedTags());
    }

    @Test
    public void testTags() throws StyleParseException {
        Assert.assertEquals(LuaConstants.NONE, handler.invoke(LuaString.valueOf("unsupported")));

        testHandler("b", TextStyle.BOLD);
        testHandler("i", TextStyle.ITALIC);
        testHandler("u", TextStyle.fromString("underline=true"));
        testHandler("font", LuaString.valueOf("myFont"), TextStyle.fromString("fontName=myFont"));
        testHandler("color", LuaString.valueOf("aabbccdd"), TextStyle.fromString("color=aabbccdd"));
        testHandler("size", LuaDouble.valueOf(12.5), TextStyle.fromString("fontSize=12.5"));
        testHandler("speed", LuaDouble.valueOf(12.5), TextStyle.fromString("speed=12.5"));
        testHandler("align", LuaString.valueOf("right"), TextStyle.fromString("align=right"));
        testHandler("center", TextStyle.fromString("align=center"));

        // Attempting to set a parameterized value with a nil value is a no-op
        testHandler("font", LuaNil.NIL, TextStyle.defaultInstance());

        Assert.assertEquals(ImmutableSet.of(), untestedTags);
    }

    private void testHandler(String tag, TextStyle expectedStyle) {
        testHandler(tag, LuaNil.NIL, expectedStyle);
    }

    private void testHandler(String tag, LuaValue tagParam, TextStyle expectedStyle) {
        untestedTags.remove(tag);

        Varargs result = handler.invoke(LuaString.valueOf(tag), LuaTable.listOf(new LuaValue[] {tagParam}));
        TextStyle actualStyle = result.arg(2).checkuserdata(TextStyle.class);
        Assert.assertEquals(expectedStyle, actualStyle);
    }

}
