package nl.weeaboo.vn.gdx.graphics;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;

/**
 * Based on {@link TextureLoader}. Loads textures based on .jng image files.
 */
public class JngTextureLoader extends PremultTextureLoader {

    public JngTextureLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    /** Registers this texture loader with the given asset manager. */
    public static void register(AssetManager assetManager) {
        FileHandleResolver resolver = assetManager.getFileHandleResolver();
        assetManager.setLoader(Texture.class, ".jng", new JngTextureLoader(resolver));
    }

    @Override
    protected TextureData loadTexData(FileHandle file, Format format, boolean genMipMaps) {
        return new JngTextureData(file, format, genMipMaps);
    }

}
