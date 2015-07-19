package nl.weeaboo.vn.image.impl;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import nl.weeaboo.gdx.res.GeneratedResourceStore;
import nl.weeaboo.gdx.res.IResource;
import nl.weeaboo.gdx.res.LoadingResourceStore;
import nl.weeaboo.gdx.res.TransformedResource;
import nl.weeaboo.vn.image.ITexture;

public class TextureManager {

    private final LoadingResourceStore<Texture> textureStore;
    private final GeneratedResourceStore generatedResourceStore;

    public TextureManager(AssetManager assetManager) {
        textureStore = new LoadingResourceStore<Texture>(Texture.class, assetManager);
        generatedResourceStore = new GeneratedResourceStore();
    }

    public IResource<TextureRegion> getTexture(String filename) {
        IResource<Texture> texture = textureStore.get(filename);
        if (texture == null) {
            return null;
        }
        return new RegionResource(texture);
    }

    public ITexture newTexture(IResource<TextureRegion> tr, double sx, double sy) {
        return new TextureAdapter(tr, sx, sy);
    }

    public IResource<TextureRegion> generateTextureRegion(PixelTextureData texData) {
        Texture texture = new Texture(texData.getPixels());
        TextureRegion texRect = new TextureRegion(texture);
        return generatedResourceStore.register(texRect, texture);
    }

    public ITexture generateTexture(PixelTextureData texData, double sx, double sy) {
        IResource<TextureRegion> tr = generateTextureRegion(texData);
        return newTexture(tr, sx, sy);
    }

    private static class RegionResource extends TransformedResource<Texture, TextureRegion> {

        public RegionResource(IResource<Texture> inner) {
            super(inner);
        }

        @Override
        protected TextureRegion transform(Texture original) {
            return new TextureRegion(original);
        }

    }

}
