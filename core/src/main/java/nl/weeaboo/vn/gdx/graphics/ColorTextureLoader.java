package nl.weeaboo.vn.gdx.graphics;

import javax.annotation.Nullable;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.SynchronousAssetLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;

import nl.weeaboo.common.StringUtil;
import nl.weeaboo.vn.render.RenderUtil;

/**
 * Generates solid-color textures
 */
public class ColorTextureLoader extends SynchronousAssetLoader<Texture, ColorTextureLoader.Parameters> {

    public static final String BLANK = getFilename(0x00000000);
    public static final String WHITE = getFilename(0xFFFFFFFF);

    public ColorTextureLoader(FileHandleResolver resolver) {
        super(resolver);
    }

    /** Registers this loader with the given asset manager. */
    public static void register(AssetManager assetManager) {
        FileHandleResolver resolver = assetManager.getFileHandleResolver();
        assetManager.setLoader(Texture.class, ".color", new ColorTextureLoader(resolver));
    }

    @Override
    public Texture load(AssetManager assetManager, String fileName, FileHandle file, Parameters parameter) {
        int colorARGB = parseARGB8888(file.nameWithoutExtension());

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(RenderUtil.argb2rgba(RenderUtil.premultiplyAlpha(colorARGB)));
        pixmap.fill();
        return new Texture(pixmap);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public @Nullable Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
            Parameters parameter) {

        return null;
    }

    private static int parseARGB8888(String string) throws NumberFormatException {
        int argb = (int)(Long.parseLong(string, 16) & 0xFFFFFFFFL);
        if (string.length() < 8) {
            argb = 0xFF000000 | argb;
        }
        return argb;
    }

    /** Returns the file name required to load a solid-color texture filled with the given ARGB8888 color. */
    public static String getFilename(int argb) {
        return StringUtil.formatRoot("%08x.color", argb);
    }

    public static class Parameters extends AssetLoaderParameters<Texture> {
    }

}
