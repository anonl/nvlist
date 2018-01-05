package nl.weeaboo.vn.impl.image;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.loaders.TextureLoader.TextureParameter;
import com.badlogic.gdx.graphics.Texture;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.gdx.graphics.GdxTextureUtil;
import nl.weeaboo.vn.gdx.res.GdxFileSystem;
import nl.weeaboo.vn.gdx.res.LoadingResourceStore;
import nl.weeaboo.vn.image.desc.IImageDefinition;
import nl.weeaboo.vn.impl.core.StaticRef;

public final class GdxTextureStore extends LoadingResourceStore<Texture> {

    private final ImageDefinitionCache cachedImageDefs;

    public GdxTextureStore(StaticRef<GdxTextureStore> selfId, GdxFileSystem fileSystem) {
        super(selfId, Texture.class);

        cachedImageDefs = new ImageDefinitionCache(fileSystem);
    }

    @CheckForNull
    public final IImageDefinition getImageDef(FilePath imagePath) {
        return cachedImageDefs.getImageDef(imagePath);
    }

    @Override
    @Nullable
    protected AssetLoaderParameters<Texture> getLoadParams(FilePath imagePath) {
        IImageDefinition imageDef = getImageDef(imagePath);
        if (imageDef == null) {
            return null;
        }

        TextureParameter params = new TextureParameter();

        params.minFilter = GdxTextureUtil.toGdxFilter(imageDef.getMinifyFilter());
        params.magFilter = GdxTextureUtil.toGdxFilter(imageDef.getMagnifyFilter());

        params.wrapU = GdxTextureUtil.toGdxWrap(imageDef.getTilingModeX());
        params.wrapV = GdxTextureUtil.toGdxWrap(imageDef.getTilingModeY());

        return params;
    }

}
