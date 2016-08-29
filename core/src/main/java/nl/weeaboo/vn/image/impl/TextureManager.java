package nl.weeaboo.vn.image.impl;

import java.io.Serializable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import nl.weeaboo.gdx.graphics.GdxTextureUtil;
import nl.weeaboo.gdx.res.GeneratedResourceStore;
import nl.weeaboo.gdx.res.IResource;
import nl.weeaboo.gdx.res.TransformedResource;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.core.impl.FileResourceLoader;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.core.impl.StaticRef;
import nl.weeaboo.vn.image.ITexture;

public class TextureManager implements Serializable {

    private static final long serialVersionUID = ImageImpl.serialVersionUID;

    private final StaticRef<TextureStore> textureStore = StaticEnvironment.TEXTURE_STORE;
    private final StaticRef<GeneratedResourceStore> generatedTextureStore = StaticEnvironment.GENERATED_RESOURCES;

    public IResource<TextureRegion> getTexture(FileResourceLoader loader, ResourceId resourceId) {
        String filename = loader.getAbsolutePath(resourceId.getFilePath());
        IResource<Texture> texture = textureStore.get().get(filename);
        if (texture == null) {
            return null;
        }
        // TODO: Resolve region based on sub-resource id
        return new RegionResource(texture);
    }

    public ITexture newTexture(IResource<TextureRegion> tr, double sx, double sy) {
        return new TextureAdapter(tr, sx, sy);
    }

    public IResource<TextureRegion> generateTextureRegion(IGdxTextureData texData) {
        GeneratedResourceStore generatedStore = generatedTextureStore.get();
        return new GeneratedRegionResource(generatedStore.register(texData));
    }

    public ITexture generateTexture(IGdxTextureData texData, double sx, double sy) {
        return newTexture(generateTextureRegion(texData), sx, sy);
    }

    private static class RegionResource extends TransformedResource<Texture, TextureRegion> {

        private static final long serialVersionUID = 1L;

        public RegionResource(IResource<Texture> inner) {
            super(inner);
        }

        @Override
        protected TextureRegion transform(Texture original) {
            return GdxTextureUtil.getDefaultRegion(original);
        }

    }

    private static class GeneratedRegionResource extends TransformedResource<IGdxTextureData, TextureRegion> {

        private static final long serialVersionUID = 1L;

        public GeneratedRegionResource(IResource<IGdxTextureData> inner) {
            super(inner);
        }

        @Override
        protected TextureRegion transform(IGdxTextureData original) {
            return original.toTextureRegion();
        }

    }

}
