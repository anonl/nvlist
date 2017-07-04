package nl.weeaboo.vn.gdx.res;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.graphics.Texture;

import nl.weeaboo.vn.gdx.graphics.ColorTextureLoader;
import nl.weeaboo.vn.gdx.graphics.JngTextureLoader;
import nl.weeaboo.vn.gdx.graphics.PremultTextureLoader;

public class GdxAssetManager extends AssetManager {

    public GdxAssetManager(FileHandleResolver fileHandleResolver) {
        super(fileHandleResolver);

        PremultTextureLoader.register(this);
        ColorTextureLoader.register(this);
        JngTextureLoader.register(this);
        Texture.setAssetManager(this);
    }

}
