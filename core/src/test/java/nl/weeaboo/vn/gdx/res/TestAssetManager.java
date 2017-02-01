package nl.weeaboo.vn.gdx.res;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;

import nl.weeaboo.vn.gdx.HeadlessGdx;
import nl.weeaboo.vn.gdx.graphics.ColorTextureLoader;
import nl.weeaboo.vn.gdx.graphics.JngTextureLoader;
import nl.weeaboo.vn.gdx.graphics.PremultTextureLoader;

public class TestAssetManager extends AssetManager {

    static {
        HeadlessGdx.init();
    }

    public TestAssetManager() {
        this(new ClasspathFileHandleResolver());
    }
    public TestAssetManager(FileHandleResolver resolver) {
        super(resolver);

        installDefaultLoaders(resolver);
    }

    private void installDefaultLoaders(FileHandleResolver resolver) {
        setLoader(Texture.class, new PremultTextureLoader(resolver));
        setLoader(Texture.class, ".color", new ColorTextureLoader(resolver));
        setLoader(Texture.class, ".jng", new JngTextureLoader(resolver));
    }

}
