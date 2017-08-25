package nl.weeaboo.vn.impl.script.lua;

import javax.annotation.Nullable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.lua2.luajava.CoerceLuaToJava;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.styledtext.ETextAttribute;
import nl.weeaboo.styledtext.MutableTextStyle;
import nl.weeaboo.styledtext.StyleParseException;
import nl.weeaboo.styledtext.TextStyle;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.image.IImageModule;
import nl.weeaboo.vn.image.IScreenshot;
import nl.weeaboo.vn.image.ITexture;
import nl.weeaboo.vn.impl.save.Storage;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.script.ScriptException;

public final class LuaConvertUtil {

    /**
     * Gets an {@link ILayer} from a Lua vararg.
     *
     * @throws ScriptException If the Lua value stored at the given index isn't covertible to a layer.
     */
    public static @Nullable ILayer getLayerArg(Varargs args, int index) throws ScriptException {
        if (args.isuserdata(index)) {
            ILayer layer = args.touserdata(index, ILayer.class);
            if (layer != null) {
                return layer;
            }
        } else if (args.isnil(index)) {
            return null;
        }
        throw new ScriptException("Invalid layer arg: " + args.tojstring(1));
    }

    /**
     * Gets an {@link ITexture} from a Lua vararg.
     *
     * @throws ScriptException If the Lua value isn't convertible to a texture.
     */
    public static @Nullable ITexture getTextureArg(IImageModule imageModule, LuaValue luaValue)
            throws ScriptException {

        return getTextureArg(imageModule, luaValue, false);
    }

    /**
     * Gets an {@link ITexture} from a Lua value.
     *
     * @throws ScriptException If the Lua value isn't convertible to a texture.
     */
    public static @Nullable ITexture getTextureArg(IImageModule imageModule, LuaValue luaValue,
            boolean suppressLoadErrors) throws ScriptException {

        if (luaValue.isstring()) {
            // Texture filename
            ResourceLoadInfo loadInfo = getLoadInfo(luaValue);
            return imageModule.getTexture(loadInfo, suppressLoadErrors);
        } else if (luaValue.isuserdata()) {
            // Texture or screenshot object
            Object obj = luaValue.touserdata();
            if (obj instanceof ITexture) {
                return (ITexture)obj;
            } else if (obj instanceof IScreenshot) {
                IScreenshot ss = (IScreenshot)obj;
                if (!ss.isAvailable()) {
                    throw new ScriptException("Screenshot data isn't available yet");
                }
                return imageModule.createTexture(ss);
            } else {
                throw new ScriptException("Invalid argument: " + obj);
            }
        } else if (!luaValue.isnil()) {
            throw new ScriptException("Invalid argument: " + luaValue);
        }
        return null;
    }

    /**
     * Converts a Lua value to {@link ResourceLoadInfo}.
     */
    public static ResourceLoadInfo getLoadInfo(LuaValue luaValue) {
        return LuaScriptUtil.createLoadInfo(getPath(luaValue));
    }

    /**
     * @see #getPath(LuaValue)
     */
    public static FilePath getPath(Varargs args, int index) {
        return getPath(args.arg(index));
    }

    /**
     * Converts a Lua value to {@link FilePath}.
     */
    public static FilePath getPath(LuaValue luaValue) {
        return FilePath.of(luaValue.checkjstring());
    }

    /**
     * Converts a Lua value to {@link TextStyle}.
     * @throws ScriptException If the Lua value is inconvertible to a text style.
     */
    public static TextStyle getTextStyleArg(LuaValue val) throws ScriptException {
        TextStyle ts = val.touserdata(TextStyle.class);
        if (ts != null) {
            // The value is already a TextStyle
            return ts;
        }

        if (val.isstring()) {
            try {
                return TextStyle.fromString(val.tojstring());
            } catch (StyleParseException e) {
                throw new ScriptException("Unable to parse text style: " + val.tojstring(), e);
            }
        } else if (val.istable()) {
            LuaTable table = val.checktable();

            MutableTextStyle mts = new MutableTextStyle();
            for (LuaValue key : table.keys()) {
                ETextAttribute attribute = ETextAttribute.fromId(key.toString());
                if (attribute == null) {
                    continue;
                }

                LuaValue luaValue = table.get(key);
                Object javaValue = parseTextAttribute(attribute, luaValue);
                if (javaValue != null) {
                    mts.setAttribute(attribute, javaValue);
                }
            }
            return mts.immutableCopy();
        } else {
            return TextStyle.defaultInstance();
        }
    }

    /**
     * Converts a Lua value to a Java value compatible with the given text attribute type.
     */
    public static Object parseTextAttribute(ETextAttribute attribute, LuaValue luaValue) {
        if (luaValue.isstring() && !luaValue.isnumber()) {
            return attribute.valueFromString(luaValue.tojstring());
        } else {
            return CoerceLuaToJava.coerceArg(luaValue, attribute.getType());
        }
    }

    /**
     * Converts part of a Lua vararg to Java objects.
     */
    public static Object[] toObjectArray(Varargs luaArgs, int luaStartIndex) {
        // Don't check upper bound -- otherwise varargs after an optional arg are annoying to use
        Checks.checkRange(luaStartIndex, "luaStartIndex", 1);

        Object[] result = new Object[luaArgs.narg() + 1 - luaStartIndex];
        for (int n = 0; n < result.length; n++) {
            result[n] = CoerceLuaToJava.coerceArg(luaArgs.arg(luaStartIndex + n), Object.class);
        }
        return result;
    }

    /** Converts a Lua table to {@link Storage}. */
    public static Storage toStorage(LuaTable table) {
        Storage storage = new Storage();
        for (LuaValue subkey : table.keys()) {
            storage.set(subkey.tojstring(), LuaStorage.luaToStorage(table.get(subkey)));
        }
        return storage;
    }

}
