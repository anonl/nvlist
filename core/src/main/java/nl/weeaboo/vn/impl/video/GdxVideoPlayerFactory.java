package nl.weeaboo.vn.impl.video;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.badlogic.gdx.video.VideoPlayerInitException;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.core.FileResourceLoader;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;

final class GdxVideoPlayerFactory implements IGdxVideoPlayerFactory {

    private final StaticRef<AssetManager> assetManager = StaticEnvironment.ASSET_MANAGER;

    private final FileResourceLoader resourceLoader;

    public GdxVideoPlayerFactory(FileResourceLoader resourceLoader) {
        this.resourceLoader = Checks.checkNotNull(resourceLoader);
    }

    @Override
    public FileHandle resolveFileHandle(FilePath filePath) {
        FileHandleResolver fileResolver = assetManager.get().getFileHandleResolver();
        FilePath absolutePath = resourceLoader.getAbsolutePath(filePath);
        return fileResolver.resolve(absolutePath.toString());
    }

    @Override
    public VideoPlayer createVideoPlayer() throws VideoPlayerInitException {
        return VideoPlayerCreator.createVideoPlayer();
    }

}
