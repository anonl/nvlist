package nl.weeaboo.vn.gdx.res;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver;

public class AssetManagerMock extends GdxAssetManager {

    public AssetManagerMock() {
        this(new ClasspathFileHandleResolver());
    }

    public AssetManagerMock(FileHandleResolver resolver) {
        super(resolver);
    }

}
