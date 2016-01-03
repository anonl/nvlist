package nl.weeaboo.vn.script.lua;

import org.luaj.vm2.Varargs;

import nl.weeaboo.lua2.lib.LuajavaLib;
import nl.weeaboo.styledtext.StyledText;
import nl.weeaboo.vn.core.impl.DefaultEnvironment;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.ITextDrawable;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;

public class TextLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    private final DefaultEnvironment env;

    public TextLib(DefaultEnvironment env) {
        super("Text");

        this.env = env;
    }

    /**
     * @param args
     *        <ol>
     *        <li>Parent layer
     *        <li>Initial text
     *        </ol>
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

}
