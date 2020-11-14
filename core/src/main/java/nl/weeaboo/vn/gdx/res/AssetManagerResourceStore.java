package nl.weeaboo.vn.gdx.res;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;

import nl.weeaboo.common.Checks;
import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.impl.core.DurationLogger;
import nl.weeaboo.vn.impl.core.LruSet;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;

/**
 * Resource store which loads resources using an {@link AssetManager}.
 */
public class AssetManagerResourceStore<T> extends ResourceStore {

    private static final Logger LOG = LoggerFactory.getLogger(LoadingResourceStore.class);

    private final StaticRef<AssetManager> assetManager = StaticEnvironment.ASSET_MANAGER;
    private final Class<T> assetType;

    private final Set<FilePath> loading = new HashSet<>();
    private final LruSet<FilePath> invalid = new LruSet<>(128);

    public AssetManagerResourceStore(Class<T> type) {
        super(LOG);

        this.assetType = Checks.checkNotNull(type);
    }

    @Override
    public void clear() {
        invalid.clear();
    }

    /**
     * Request a preload of the given resource.
     */
    public void preload(FilePath absolutePath) {
        ELoadState loadState = getLoadState(absolutePath);
        if (loadState == ELoadState.ERROR) {
            LOG.debug("Blocked preload of broken resource: {}", absolutePath);
        } else if (loadState == ELoadState.UNLOADED) {
            startLoading(absolutePath, "preload");
        }
    }

    /** Starts asynchronously loading the resource from disk using {@link AssetManager} */
    protected void startLoading(FilePath absolutePath, String message) {
        LOG.debug("{}: {}", message, absolutePath);

        loading.add(absolutePath);

        AssetManager am = assetManager.get();
        am.load(absolutePath.toString(), assetType, getLoadParams(absolutePath));
    }

    /**
     * Loads the resource synchronously from disk using {@link AssetManager}
     * @see AssetManager#load(String, Class)
     */
    protected T loadResource(FilePath absolutePath) {
        DurationLogger dl = startLoadDurationLogger(LOG);

        ELoadState loadState = getLoadState(absolutePath);
        if (loadState == ELoadState.ERROR) {
            throw new IllegalStateException("Refusing to load broken resource " + absolutePath);
        } else if (loadState == ELoadState.UNLOADED) {
            startLoading(absolutePath, "Start loading");
        }

        // Finish loading resource
        AssetManager am = assetManager.get();
        String pathString = absolutePath.toString();
        am.finishLoadingAsset(pathString);
        loading.remove(absolutePath);

        T resource = am.get(pathString);

        invalid.remove(absolutePath);

        if (loadState == ELoadState.LOADED) {
            dl.logDuration("Loading resource (preloaded) '{}'", absolutePath);
        } else {
            dl.logDuration("Loading resource (cache miss) '{}'", absolutePath);
        }

        return resource;
    }

    /**
     * @see AssetManager#unload(String)
     */
    protected final void unloadResource(FilePath absolutePath) {
        AssetManager am = assetManager.get();

        String pathString = absolutePath.toString();
        if (am.isLoaded(pathString)) {
            LOG.debug("Unloading resource: {}", pathString);
            am.unload(pathString);
        }
    }

    /**
     * @param absolutePath The path to the resource that's being loaded (may not exist).
     */
    @Nullable
    protected AssetLoaderParameters<T> getLoadParams(FilePath absolutePath) {
        return null;
    }

    /**
     * Returns the current loading state of the resource with the given name.
     */
    protected final ELoadState getLoadState(FilePath absolutePath) {
        AssetManager am = assetManager.get();
        if (am.isLoaded(absolutePath.toString())) {
            loading.remove(absolutePath); // Remove stale entry if one exists
            return ELoadState.LOADED;
        } else if (invalid.contains(absolutePath)) {
            loading.remove(absolutePath); // Remove stale entry if one exists
            return ELoadState.ERROR;
        } else if (loading.contains(absolutePath)) {
            return ELoadState.PRELOADING;
        } else {
            loading.remove(absolutePath); // Remove stale entry if one exists
            return ELoadState.UNLOADED;
        }
    }

    @Override
    protected void loadError(FilePath path, Throwable cause) {
        invalid.add(path);
        super.loadError(path, cause);
    }

    protected enum ELoadState {
        UNLOADED, PRELOADING, LOADED, ERROR;
    }

}
