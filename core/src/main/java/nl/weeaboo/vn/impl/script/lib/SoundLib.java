package nl.weeaboo.vn.impl.script.lib;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.MoreObjects;

import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.LuaNil;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.impl.script.lua.LuaConvertUtil;
import nl.weeaboo.vn.script.ScriptFunction;
import nl.weeaboo.vn.sound.ISound;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.ISoundModule;
import nl.weeaboo.vn.sound.SoundType;

public class SoundLib extends LuaLib {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(SoundLib.class);

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
        ResourceLoadInfo loadInfo = LuaConvertUtil.getLoadInfo(args.arg(1));
        SoundType stype = MoreObjects.firstNonNull(args.touserdata(2, SoundType.class), SoundType.SOUND);

        ISoundModule soundModule = env.getSoundModule();
        try {
            ISound sound = soundModule.createSound(stype, loadInfo);
            return LuajavaLib.toUserdata(sound, ISound.class);
        } catch (FileNotFoundException fnfe) {
            LOG.warn("Error starting sound: {}", loadInfo, fnfe);
        } catch (IOException ioe) {
            LOG.warn("Error starting sound: {}", loadInfo, ioe);
        }
        return LuaNil.NIL;
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
}
