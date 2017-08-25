package nl.weeaboo.vn.impl.video;

import java.io.IOException;
import java.io.Serializable;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerInitException;
import com.google.common.annotations.VisibleForTesting;

import nl.weeaboo.common.Checks;
import nl.weeaboo.common.Dim;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.gdx.res.GeneratedResourceStore;
import nl.weeaboo.vn.gdx.res.IResource;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;
import nl.weeaboo.vn.render.IRenderEnv;

final class NativeVideo implements INativeVideo {

    private static final long serialVersionUID = VideoImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(NativeVideo.class);

    private final StaticRef<GeneratedResourceStore> resourceStore = StaticEnvironment.GENERATED_RESOURCES;

    private final IGdxVideoPlayerFactory videoPlayerFactory;
    private final FilePath filePath;

    private boolean paused = false;
    private double volume = 1.0;
    private IRenderEnv renderEnv;

    private IResource<VideoPlayerResource> videoPlayerRef;

    public NativeVideo(IGdxVideoPlayerFactory videoPlayerFactory, FilePath filePath, IRenderEnv renderEnv) {
        this.videoPlayerFactory = Checks.checkNotNull(videoPlayerFactory);
        this.filePath = Checks.checkNotNull(filePath);
        this.renderEnv = Checks.checkNotNull(renderEnv);
    }

    @Override
    public void prepare() {
        try {
            initVideoPlayer();
        } catch (VideoPlayerInitException e) {
            LOG.debug("Prepare failed", e);
        }
    }

    @Override
    public void play() throws VideoPlayerInitException, IOException {
        initVideoPlayer();
        applyVolume();

        VideoPlayer player = getVideoPlayer();
        if (player != null) {
            player.play(videoPlayerFactory.resolveFileHandle(filePath));
        }
    }

    private void initVideoPlayer() throws VideoPlayerInitException {
        if (videoPlayerRef == null) {
            VideoPlayerResource res = new VideoPlayerResource(videoPlayerFactory);
            videoPlayerRef = resourceStore.get().register(res);
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
    public boolean isStopped() {
        return !isPlaying() && !isPaused();
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

    @VisibleForTesting
    @Nullable VideoPlayer getVideoPlayer() {
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

        private static final long serialVersionUID = VideoImpl.serialVersionUID;

        private final IGdxVideoPlayerFactory videoPlayerFactory;
        private transient VideoPlayer player;

        public VideoPlayerResource(IGdxVideoPlayerFactory videoPlayerFactory) {
            this.videoPlayerFactory = Checks.checkNotNull(videoPlayerFactory);
        }

        @Override
        public void dispose() {
            if (player != null) {
                player.dispose();
            }
        }

        public void initVideoPlayer() throws VideoPlayerInitException {
            if (player == null) {
                player = videoPlayerFactory.createVideoPlayer();
            }
        }

        public VideoPlayer getVideoPlayer() {
            return player;
        }

    }

}
