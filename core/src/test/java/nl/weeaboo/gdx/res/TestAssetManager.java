package nl.weeaboo.gdx.res;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.ClasspathFileHandleResolver;
import com.badlogic.gdx.graphics.Texture;

import nl.weeaboo.gdx.HeadlessGdx;
import nl.weeaboo.gdx.graphics.JngTextureLoader;
import nl.weeaboo.gdx.graphics.PremultTextureLoader;

public class TestAssetManager extends AssetManager {

    static {
        HeadlessGdx.init();
    }

    public TestAssetManager() {
        super(new ClasspathFileHandleResolver());

        setLoader(Texture.class, new PremultTextureLoader(getFileHandleResolver()));
        setLoader(Texture.class, ".jng", new JngTextureLoader(getFileHandleResolver()));
    }

}
