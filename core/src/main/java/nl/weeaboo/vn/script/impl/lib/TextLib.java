package nl.weeaboo.vn.script.impl.lib;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import nl.weeaboo.collections.IntMap;
import nl.weeaboo.common.StringUtil;
import nl.weeaboo.lua2.LuaRunState;
import nl.weeaboo.lua2.LuaUtil;
import nl.weeaboo.lua2.compiler.LoadState;
import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.LuaInteger;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.LuaThread;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.styledtext.MutableStyledText;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.ITextDrawable;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;
import nl.weeaboo.vn.script.impl.lua.LuaConvertUtil;
import nl.weeaboo.vn.script.impl.lua.LuaScriptEnv;
import nl.weeaboo.vn.script.impl.lvn.RuntimeTextParser;
import nl.weeaboo.vn.script.impl.lvn.RuntimeTextParser.ParseResult;

public class TextLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    private final IEnvironment env;
    private final LuaScriptEnv scriptEnv;

    public TextLib(IEnvironment env, LuaScriptEnv scriptEnv) {
        super("Text");

        this.env = env;
        this.scriptEnv = scriptEnv;
    }

    /**
     * @param args
     *        <ol>
     *        <li>(optional) Parent layer
     *        <li>(optional) Initial text
     *        </ol>
     * @return A text drawable
     */
    @ScriptFunction
    public Varargs createTextDrawable(Varargs args) throws ScriptException {
        ILayer layer = LuaConvertUtil.getLayerArg(args, 1);
        if (layer == null) {
            layer = LuaConvertUtil.getActiveLayer();
        }

        IImageModule imageModule = env.getImageModule();
        ITextDrawable textDrawable = imageModule.createTextDrawable(layer);

        // Set initial text
        textDrawable.setBounds(0, 0, layer.getWidth(), layer.getHeight());
        if (!args.isnil(2)) {
            StyledText stext = args.touserdata(2, StyledText.class);
            if (stext != null) {
                textDrawable.setText(stext);
            } else {
                textDrawable.setText(args.tojstring(2));
            }
        }

        return LuajavaLib.toUserdata(textDrawable, ITextDrawable.class);
    }

    /**
     * @param args
     *        <ol>
     *        <li>table of text attributes
     *        </ol>
     * @return text style
     */
    @ScriptFunction
    public Varargs createStyle(Varargs args) throws ScriptException {
        TextStyle textStyle = LuaConvertUtil.getTextStyleArg(args.arg(1));
        return LuajavaLib.toUserdata(textStyle, TextStyle.class);
    }

    /**
     * Creates a new text style by merging two existing styles.
     *
     * @param args
     *        <ol>
     *        <li>base text style
     *        <li>overriding text style
     *        </ol>
     * @return text style
     */
    @ScriptFunction
    public Varargs extendStyle(Varargs args) throws ScriptException {
        TextStyle baseStyle = LuaConvertUtil.getTextStyleArg(args.arg(1));
        TextStyle extStyle = LuaConvertUtil.getTextStyleArg(args.arg(2));
        TextStyle merged = TextStyle.extend(baseStyle, extStyle);
        return LuajavaLib.toUserdata(merged, TextStyle.class);
    }

    /**
     * @param args
     *        <ol>
     *        <li>string or styled text
     *        <li>(optional) text style
     *        </ol>
     * @return styled text
     */
    @ScriptFunction
    public Varargs createStyledText(Varargs args) throws ScriptException {
        StyledText st = args.touserdata(1, StyledText.class);
        if (st != null) {
            TextStyle style = LuaConvertUtil.getTextStyleArg(args.arg(2));
            if (style != null) {
                MutableStyledText mts = st.mutableCopy();
                mts.setBaseStyle(style);
                st = mts.immutableCopy();
            }
        } else {
            String text = (args.isnil(1) ? "" : args.tojstring(1));
            TextStyle style = LuaConvertUtil.getTextStyleArg(args.arg(2));
            st = new StyledText(text, style);
        }
        return LuajavaLib.toUserdata(st, StyledText.class);
    }

    /**
     * @param args
     *        <ol>
     *        <li>text to parse
     *        <li>(optional) table of triggers
     *        </ol>
     * @return (parsed text, triggers table)
     */
    @ScriptFunction
    public Varargs parseText(Varargs args) throws ScriptException {
        RuntimeTextParser textParser = getRuntimeTextParser();
        ParseResult res = textParser.parse(args.arg(1).tojstring());

        LuaTable oldTriggers = args.opttable(2, null);
        LuaTable newTriggers = new LuaTable();

        LuaRunState lrs = LuaRunState.getCurrent();
        IntMap<String> commandMap = res.getCommands();

        LuaValue oldTableIndex = LuaInteger.valueOf(0);
        for (int n = 0; n < commandMap.size(); n++) {
            LuaValue func = null;

            LuaValue env = lrs.getGlobalEnvironment();

            LuaThread thread = lrs.getRunningThread();
            if (thread != null) {
                env = thread.getCallEnv();
            }

            if (oldTriggers != null) {
                Varargs ipair = oldTriggers.inext(oldTableIndex);
                oldTableIndex = ipair.arg(1);
                func = ipair.arg(2);
                env = oldTriggers.getfenv();
            }

            if (func == null) {
                /*
                 * If no compiled trigger function given, compile it here. This means we may lose access to
                 * any local variables.
                 */
                String str = commandMap.valueAt(n);
                ByteArrayInputStream bin = new ByteArrayInputStream(StringUtil.toUTF8(str));
                try {
                    func = LoadState.load(bin, "~trigger" + n, env);
                } catch (IOException ioe) {
                    throw new ScriptException("Error compiling trigger function", ioe);
                }
            }

            newTriggers.rawset(commandMap.keyAt(n), func);
        }

        LuaValue resultText = LuajavaLib.toUserdata(res.getText(), StyledText.class);
        return LuaValue.varargsOf(resultText, newTriggers);
    }

    /**
     * @param args
     *        <ol>
     *        <li>table to register basic tag handler functions into
     *        </ol>
     * @return The modified input table
     */
    @ScriptFunction
    public Varargs registerBasicTagHandlers(Varargs args) {
        LuaTable table = args.checktable(1);
        BasicTagHandler h = new BasicTagHandler();
        for (String tag : BasicTagHandler.getSupportedTags()) {
            table.rawset(tag, h);
        }
        return table;
    }

    @ScriptFunction
    public Varargs parseLuaLiteral(Varargs args) {
        LuaValue val = args.arg1();
        if (val.isnil()) {
            return val;
        }
        return LuaUtil.parseLuaLiteral(val.tojstring());
    }

    private RuntimeTextParser getRuntimeTextParser() {
        return new RuntimeTextParser(scriptEnv.getGlobals());
    }

}
