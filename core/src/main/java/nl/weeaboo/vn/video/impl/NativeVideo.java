package nl.weeaboo.vn.video.impl;

import java.io.IOException;
import java.io.Serializable;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.badlogic.gdx.video.VideoPlayerInitException;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.gdx.res.GeneratedResourceStore;
import nl.weeaboo.gdx.res.IResource;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.impl.FileResourceLoader;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.core.impl.StaticRef;

final class NativeVideo implements IVideoAdapter {

    private static final long serialVersionUID = VideoImpl.serialVersionUID;

    private final StaticRef<GeneratedResourceStore> resourceStore = StaticEnvironment.GENERATED_RESOURCES;
    private final StaticRef<AssetManager> assetManager = StaticEnvironment.ASSET_MANAGER;

    private FileResourceLoader resourceLoader;
    private final String filename;

    private boolean paused = false;
    private double volume = 1.0;
    private IRenderEnv renderEnv;

    private IResource<VideoPlayerResource> videoPlayerRef;

    public NativeVideo(FileResourceLoader resourceLoader, String filename, IRenderEnv renderEnv) {
        this.resourceLoader = Checks.checkNotNull(resourceLoader);
        this.filename = Checks.checkNotNull(filename);
        this.renderEnv = Checks.checkNotNull(renderEnv);
    }

    private FileHandle resolveFileHandle() {
        FileHandleResolver fileResolver = assetManager.get().getFileHandleResolver();
        return fileResolver.resolve(resourceLoader.getAbsolutePath(filename));
    }

    @Override
    public void prepare() {
    }

    @Override
    public void play() throws VideoPlayerInitException, IOException {
        initVideoPlayer();
        applyVolume();

        VideoPlayer player = getVideoPlayer();
        if (player != null) {
            player.play(resolveFileHandle());
        }
    }

    private void initVideoPlayer() throws VideoPlayerInitException {
        if (videoPlayerRef == null) {
            videoPlayerRef = resourceStore.get().register(new VideoPlayerResource());
        }
        videoPlayerRef.get().initVideoPlayer();
    }

    @Override
    public void pause() {
        VideoPlayer player = getVideoPlayer();
        if (player != null) {
            player.pause();
        }

        paused = true;
    }

    @Override
    public void resume() {
        paused = false;

        VideoPlayer player = getVideoPlayer();
        if (player != null) {
            player.resume();
        }
    }

    @Override
    public void stop() {
        VideoPlayer player = getVideoPlayer();
        if (player != null) {
            player.stop();
        }
    }

    @Override
    public boolean isPrepared() {
        VideoPlayer player = getVideoPlayer();
        return player != null && player.isBuffered();
    }

    @Override
    public boolean isPlaying() {
        VideoPlayer player = getVideoPlayer();
        return player != null && player.isPlaying();
    }

    @Override
    public boolean isPaused() {
        return paused;
    }

    @Override
    public void setVolume(double vol) {
        if (this.volume != vol) {
            this.volume = vol;

            applyVolume();
        }
    }

    private void applyVolume() {
        VideoPlayer player = getVideoPlayer();
        if (player != null) {
            player.setVolume((float)volume);
        }
    }

    private VideoPlayer getVideoPlayer() {
        if (videoPlayerRef == null) {
            return null;
        }
        return videoPlayerRef.get().getVideoPlayer();
    }

    @Override
    public void render() {
        Dim vsize = renderEnv.getVirtualSize();

        VideoPlayer player = getVideoPlayer();
        if (player != null) {
            player.resize(vsize.w, vsize.h);
            player.render();
        }
    }

    @Override
    public void setRenderEnv(IRenderEnv renderEnv) {
        this.renderEnv = Checks.checkNotNull(renderEnv);
    }

    private static final class VideoPlayerResource implements Serializable, Disposable {

        private static final long serialVersionUID = 1L;

        private transient VideoPlayer player;

        @Override
        public void dispose() {
            if (player != null) {
                player.dispose();
            }
        }

        public void initVideoPlayer() throws VideoPlayerInitException {
            if (player == null) {
                player = VideoPlayerCreator.createVideoPlayer();
            }
        }

        public VideoPlayer getVideoPlayer() {
            return player;
        }

    }

}
