package nl.weeaboo.vn.impl.script.lib;

import java.io.IOException;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.lua2.luajava.LuajavaLib;
import nl.weeaboo.lua2.vm.LuaNil;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.impl.script.lua.LuaConvertUtil;
import nl.weeaboo.vn.impl.script.lua.LuaScriptUtil;
import nl.weeaboo.vn.script.ScriptException;
import nl.weeaboo.vn.script.ScriptFunction;
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
     * @throws ScriptException If the video file can't be read.
     */
    @ScriptFunction
    public Varargs movie(Varargs args) throws ScriptException {
        FilePath filename = LuaConvertUtil.getPath(args, 1);
        ResourceLoadInfo loadInfo = LuaScriptUtil.createLoadInfo(filename);

        IVideoModule videoModule = env.getVideoModule();
        try {
            IVideo video = videoModule.movie(loadInfo);
            if (video == null) {
                return LuaNil.NIL;
            }

            return LuajavaLib.toUserdata(video, IVideo.class);
        } catch (IOException e) {
            throw new ScriptException("Error starting movie: " + filename, e);
        }
    }

}
