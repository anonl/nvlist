package nl.weeaboo.vn.gdx.graphics;

import javax.annotation.Nullable;

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
import com.badlogic.gdx.graphics.glutils.KTXTextureData;
import com.badlogic.gdx.utils.Array;

/**
 * Based on {@link TextureLoader}, but loads converts to premultiplied alpha.
 */
public class PremultTextureLoader extends AsynchronousAssetLoader<Texture, TextureParameter> {

    private TextureLoaderInfo info = new TextureLoaderInfo();

    public PremultTextureLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    /** Registers this texture loader with the given asset manager. */
    public static void register(AssetManager assetManager) {
        FileHandleResolver resolver = assetManager.getFileHandleResolver();
        assetManager.setLoader(Texture.class, new PremultTextureLoader(resolver));
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

            info.data = loadTexData(file, format, genMipMaps);
        } else {
            info.data = parameter.textureData;
            info.texture = parameter.texture;
        }
        if (!info.data.isPrepared()) {
            info.data.prepare();
        }
    }

    protected TextureData loadTexData(FileHandle file, Format format, boolean genMipMaps) {
        if (file.name().endsWith(".ktx") || file.name().endsWith(".zktx")) {
            return new KTXTextureData(file, genMipMaps);
        }
        return new PremultFileTextureData(file, format, genMipMaps);
    }

    @Override
    public @Nullable Texture loadSync(AssetManager manager, String fileName, FileHandle file,
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

        if (parameter == null) {
            GdxTextureUtil.setDefaultTextureParams(texture);
        } else {
            texture.setFilter(parameter.minFilter, parameter.magFilter);
            texture.setWrap(parameter.wrapU, parameter.wrapV);
        }
        return texture;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public @Nullable Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
            TextureParameter parameter) {
        return null;
    }

    private static class TextureLoaderInfo {
        TextureData data;
        Texture texture;
    }

}
