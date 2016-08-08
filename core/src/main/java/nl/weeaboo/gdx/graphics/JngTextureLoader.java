package nl.weeaboo.gdx.graphics;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.TextureLoader;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.TextureData;
import com.badlogic.gdx.utils.Array;

/**
 * Based on {@link TextureLoader}. Loads textures based on .jng image files.
 */
public class JngTextureLoader extends AsynchronousAssetLoader<Texture, TextureParameter> {

    private TextureLoaderInfo info = new TextureLoaderInfo();

    protected JngTextureLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    public static void register(AssetManager assetManager) {
        FileHandleResolver resolver = assetManager.getFileHandleResolver();
        assetManager.setLoader(Texture.class, ".jng", new JngTextureLoader(resolver));
    }

    @Override
    public void loadAsync(AssetManager manager, String fileName, FileHandle file,
            TextureParameter parameter) {

        if (parameter == null || parameter.textureData == null) {
            Format format = null;
            boolean genMipMaps = false;
            info.texture = null;

            if (parameter != null) {
                format = parameter.format;
                genMipMaps = parameter.genMipMaps;
                info.texture = parameter.texture;
            }

            info.data = new JngTextureData(file, genMipMaps);
        } else {
            info.data = parameter.textureData;
            info.texture = parameter.texture;
        }
        if (!info.data.isPrepared()) {
            info.data.prepare();
        }
    }

    @Override
    public Texture loadSync(AssetManager manager, String fileName, FileHandle file,
            TextureParameter parameter) {
        if (info == null) {
            return null;
        }

        Texture texture = info.texture;
        if (texture != null) {
            texture.load(info.data);
        } else {
            texture = new Texture(info.data);
        }
        if (parameter != null) {
            texture.setFilter(parameter.minFilter, parameter.magFilter);
            texture.setWrap(parameter.wrapU, parameter.wrapV);
        }
        return texture;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
            TextureParameter parameter) {
        return null;
    }

    private static class TextureLoaderInfo {
        TextureData data;
        Texture texture;
    }

}
