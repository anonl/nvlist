package nl.weeaboo.vn.gdx.res;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver;

import nl.weeaboo.vn.gdx.HeadlessGdx;

public class AssetManagerMock extends GdxAssetManager {

    static {
        HeadlessGdx.init();
    }

    public AssetManagerMock() {
        this(new ClasspathFileHandleResolver());
    }

    public AssetManagerMock(FileHandleResolver resolver) {
        super(resolver);
    }

}
