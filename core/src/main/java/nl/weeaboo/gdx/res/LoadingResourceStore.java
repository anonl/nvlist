package nl.weeaboo.gdx.res;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.slf4j.LoggerFactory;

import com.badlogic.gdx.assets.AssetManager;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.core.impl.StaticRef;

public class LoadingResourceStore<T> extends AbstractResourceStore {

    private final StaticRef<? extends LoadingResourceStore<T>> selfId;
    private final StaticRef<AssetManager> assetManager = StaticEnvironment.ASSET_MANAGER;
    private final Class<T> assetType;

    private final CacheLoader<String, Ref<T>> loadingFunction = new LoadingFunction();
    private final AssetRemovalListener assetRemovalListener = new AssetRemovalListener();

    private LoadingCache<String, Ref<T>> cache;

    public LoadingResourceStore(StaticRef<? extends LoadingResourceStore<T>> selfId, Class<T> type) {
        super(LoggerFactory.getLogger("LoadingResourceStore<" + type.getSimpleName() + ">"));

        this.selfId = selfId;
        this.assetType = Checks.checkNotNull(type);

        cache = buildCache(CacheBuilder.newBuilder().expireAfterAccess(15, TimeUnit.SECONDS));
    }

    private LoadingCache<String, Ref<T>> buildCache(CacheBuilder<Object, Object> builder) {
        return builder.removalListener(assetRemovalListener)
                .build(loadingFunction);
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }

    protected T loadResource(String filename) {
        AssetManager am = assetManager.get();
        am.load(filename, assetType);
        am.finishLoadingAsset(filename);
        return am.get(filename);
    }

    protected void unloadResource(String filename, Ref<T> entry) {
        entry.invalidate();

        AssetManager am = assetManager.get();
        am.unload(filename);
    }

    public IResource<T> get(String filename) {
        Ref<T> entry = getEntry(filename);
        if (entry == null) {
            return null;
        }

        FileResource<T> resource = new FileResource<T>(selfId, filename);
        resource.set(entry);
        return resource;
    }

    protected Ref<T> getEntry(String filename) {
        try {
            return cache.get(filename);
        } catch (ExecutionException e) {
            loadError(filename, e.getCause());
            return null;
        }
    }

    public void setCache(CacheBuilder<Object, Object> builder) {
        cache = buildCache(builder);
    }

    private class LoadingFunction extends CacheLoader<String, Ref<T>> {

        @Override
        public Ref<T> load(String filename) {
            T resource;
            try {
                resource = loadResource(filename);
            } catch (RuntimeException re) {
                loadError(filename, re);
                resource = null;
            }
            return new Ref<T>(resource);
        }

    }

    private class AssetRemovalListener implements RemovalListener<String, Ref<T>> {

        @Override
        public void onRemoval(RemovalNotification<String, Ref<T>> notification) {
            unloadResource(notification.getKey(), notification.getValue());
        }

    }

}
