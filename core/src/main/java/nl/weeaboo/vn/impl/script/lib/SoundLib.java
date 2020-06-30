package nl.weeaboo.vn.impl.script.lib;

import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.LuaConstants;
import nl.weeaboo.lua2.vm.LuaDouble;
import nl.weeaboo.lua2.vm.LuaNil;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.impl.script.lua.LuaConvertUtil;
import nl.weeaboo.vn.script.ScriptFunction;
import nl.weeaboo.vn.sound.ISound;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.ISoundModule;
import nl.weeaboo.vn.sound.SoundType;

/**
 * Lua "Sound" library.
 */
public class SoundLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    private final IEnvironment env;

    public SoundLib(IEnvironment env) {
        super("Sound");

        this.env = env;
    }

    /**
     * @param args
     *        <ol>
     *        <li>filename
     *        <li>(optional) Sound type (sound effect, music, voice clip)
     *        </ol>
     * @return A new sound object, or {@code nil} if the sound couldn't be loaded.
     */
    @ScriptFunction
    public Varargs create(Varargs args) {
        ResourceLoadInfo loadInfo = LuaConvertUtil.getLoadInfo(MediaType.SOUND, args.arg(1));
        SoundType stype = args.optuserdata(2, SoundType.class, SoundType.SOUND);

        ISoundModule soundModule = env.getSoundModule();
        ISound sound = soundModule.createSound(stype, loadInfo);
        if (sound == null) {
            return LuaNil.NIL;
        }

        return LuajavaLib.toUserdata(sound, ISound.class);
    }

    /**
     * @param args
     *        <ol>
     *        <li>Sound channel
     *        </ol>
     * @return The sound object currently playing in the specified channel, or {@code nil} if no sound if
     *         playing in that channel.
     */
    @ScriptFunction
    public Varargs findByChannel(Varargs args) {
        int channel = args.checkint(1);

        ISoundController sc = getSoundController();
        ISound sound = sc.get(channel);
        if (sound == null) {
            return LuaNil.NIL;
        }
        return LuajavaLib.toUserdata(sound, ISound.class);
    }

    private ISoundController getSoundController() {
        return env.getSoundModule().getSoundController();
    }


    /**
     * @param args
     *        <ol>
     *        <li>Sound type (sound effect, music, voice clip)
     *        </ol>
     * @return The current master volume (between {@code 0.0} and {@code 1.0}) for the given sound type.
     */
    @ScriptFunction
    public Varargs getMasterVolume(Varargs args) {
        SoundType stype = args.optuserdata(1, SoundType.class, SoundType.SOUND);

        ISoundController sc = getSoundController();
        return LuaDouble.valueOf(sc.getMasterVolume(stype));
    }

    /**
     * @param args
     *        <ol>
     *        <li>Sound type (sound effect, music, voice clip)
     *        <li>New master volume (between {@code 0.0} and {@code 1.0}).
     *        </ol>
     */
    @ScriptFunction
    public Varargs setMasterVolume(Varargs args) {
        SoundType stype = args.optuserdata(1, SoundType.class, SoundType.SOUND);
        double volume = args.checkdouble(2);

        ISoundController sc = getSoundController();
        sc.setMasterVolume(stype, volume);

        return LuaConstants.NONE;
    }
}
