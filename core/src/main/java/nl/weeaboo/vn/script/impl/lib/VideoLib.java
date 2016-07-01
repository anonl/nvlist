package nl.weeaboo.vn.script.impl.lib;

import java.io.IOException;

import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.LuaNil;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;
import nl.weeaboo.vn.script.impl.lua.LuaScriptUtil;
import nl.weeaboo.vn.video.IVideo;
import nl.weeaboo.vn.video.IVideoModule;

public class VideoLib extends LuaLib {

    private static final long serialVersionUID = 1L;

    private final IEnvironment env;

    public VideoLib(IEnvironment env) {
        super("Video");

        this.env = env;
    }

    /**
     * Starts playback of a full-screen video.
     *
     * @param args
     *        <ol>
     *        <li>Filename
     *        </ol>
     */
    @ScriptFunction
    public Varargs movie(Varargs args) throws ScriptException {
        String filename = args.checkjstring(1);
        ResourceLoadInfo loadInfo = LuaScriptUtil.createLoadInfo(filename);

        IVideoModule videoModule = env.getVideoModule();
        try {
            IVideo video = videoModule.movie(loadInfo);
            if (video == null) {
                return LuaNil.NIL;
            }

            video.start();
            return LuajavaLib.toUserdata(video, IVideo.class);
        } catch (IOException e) {
            throw new ScriptException("Error starting movie: " + filename, e);
        }
    }

}
