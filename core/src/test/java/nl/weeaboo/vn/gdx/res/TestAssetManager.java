package nl.weeaboo.vn.gdx.res;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver;

import nl.weeaboo.vn.gdx.HeadlessGdx;

public class TestAssetManager extends GdxAssetManager {

    static {
        HeadlessGdx.init();
    }

    public TestAssetManager() {
        this(new ClasspathFileHandleResolver());
    }

    public TestAssetManager(FileHandleResolver resolver) {
        super(resolver);
    }

}
