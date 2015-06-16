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

public class LoadingResourceStore<T> extends AbstractResourceStore {
    
    private final Class<T> assetType;
    private final AssetManager assetManager;
    
    private final CacheLoader<String, Ref<T>> loadingFunction = new LoadingFunction();
    private final AssetRemovalListener assetRemovalListener = new AssetRemovalListener();
    
    private LoadingCache<String, Ref<T>> cache;

    public LoadingResourceStore(Class<T> type, AssetManager assetManager) {
        super(LoggerFactory.getLogger("LoadingResourceStore<" + type.getSimpleName() + ">"));

        this.assetType = Checks.checkNotNull(type);
        this.assetManager = Checks.checkNotNull(assetManager);
                
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
        assetManager.load(filename, assetType);
        assetManager.finishLoadingAsset(filename);
        return assetManager.get(filename);
    }
    
    protected void unloadResource(String filename, Ref<T> entry) {
        entry.invalidate();
        
        assetManager.unload(filename);
    }
    
    public IResource<T> get(String filename) {
        CachedResource<T> resource = new CachedResource<T>(this, filename);
        resource.set(getEntry(filename));
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
