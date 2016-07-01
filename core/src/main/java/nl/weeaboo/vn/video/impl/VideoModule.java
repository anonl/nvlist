package nl.weeaboo.vn.video.impl;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

import nl.weeaboo.common.Dim;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.core.impl.DefaultEnvironment;
import nl.weeaboo.vn.video.IVideo;
import nl.weeaboo.vn.video.IVideoModule;

public class VideoModule implements IVideoModule {

    protected static final String DEFAULT_VIDEO_FOLDER = "video/";

    private static final long serialVersionUID = VideoImpl.serialVersionUID;
    private static final Logger LOG = LoggerFactory.getLogger(VideoModule.class);

    protected final IEnvironment env;
    protected final VideoResourceLoader resourceLoader;

    private IVideo fullscreenMovie;
    private String videoFolder = DEFAULT_VIDEO_FOLDER;
    private Dim videoResolution;

    public VideoModule(DefaultEnvironment env) {
        this(env, new VideoResourceLoader(env));
    }
    public VideoModule(DefaultEnvironment env, VideoResourceLoader resourceLoader) {
        this.env = env;
        this.resourceLoader = resourceLoader;

        IRenderEnv renderEnv = env.getRenderEnv();
        videoResolution = renderEnv.getVirtualSize();
    }

    @Override
    public void destroy() {
    }

    @Override
    public void update() {
    }

    @Override
    public ResourceId resolveResource(String filename) {
        return resourceLoader.resolveResource(filename);
    }

    @Override
    public IVideo movie(ResourceLoadInfo loadInfo) throws IOException {
        checkIfMovieFinished();

    	Preconditions.checkState(fullscreenMovie == null, "A different movie is still playing");

        LOG.info("Attempt to play movie: videoFolder={}, path={}", videoFolder, loadInfo.getFilename());

        fullscreenMovie = createVideo(loadInfo);

        return fullscreenMovie;
    }

    private IVideo createVideo(ResourceLoadInfo loadInfo) {
        String filename = loadInfo.getFilename();
        resourceLoader.checkRedundantFileExt(filename);

        ResourceId resourceId = resourceLoader.resolveResource(filename);
        if (resourceId == null) {
            LOG.warn("Unable to find video file: {}", filename);
            return null;
        }

        resourceLoader.logLoad(resourceId, loadInfo);

        // Note: VideoAdapter currently
        IRenderEnv renderEnv = env.getRenderEnv();
        NativeVideo video = new NativeVideo(resourceLoader, resourceId.getCanonicalFilename(), renderEnv);
        return new Video(resourceId.getCanonicalFilename(), video);

    }

    @Override
    public IVideo getBlocking() {
        checkIfMovieFinished();
        return fullscreenMovie;
    }

    private void checkIfMovieFinished() {
        if (fullscreenMovie != null && fullscreenMovie.isStopped()) {
            fullscreenMovie = null;
        }
    }

    @Override
    public Collection<String> getVideoFiles(String folder) {
        return resourceLoader.getMediaFiles(folder);
    }

    protected void onVideoScaleChanged() {
    }

    protected double getVideoScale() {
        IRenderEnv renderEnv = env.getRenderEnv();
        return Math.min(renderEnv.getWidth() / (double)videoResolution.w,
                renderEnv.getHeight() / (double)videoResolution.h);
    }

    @Override
    public void setVideoFolder(String folder, Dim size) {
    	Preconditions.checkNotNull(folder);
    	Preconditions.checkNotNull(size);

        if (!videoFolder.equals(folder) || !videoResolution.equals(size)) {
            videoFolder = folder;
            videoResolution = size;

            resourceLoader.setResourceFolder(folder);
            onVideoScaleChanged();
        }
    }

}
