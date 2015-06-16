package nl.weeaboo.gdx.res;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver;

import nl.weeaboo.gdx.HeadlessGdx;

public class TestAssetManager extends AssetManager {

    static {
        HeadlessGdx.init();
    }
    
    public TestAssetManager() {
        super(new ClasspathFileHandleResolver());
    }
    
}
