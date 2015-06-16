package nl.weeaboo.vn.image.impl;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import nl.weeaboo.gdx.res.GeneratedResourceStore;
import nl.weeaboo.gdx.res.IResource;
import nl.weeaboo.gdx.res.LoadingResourceStore;
import nl.weeaboo.vn.image.ITexture;

public class TextureManager {

    private final LoadingResourceStore<TextureRegion> textureStore;
    private final GeneratedResourceStore generatedResourceStore;

    public TextureManager(AssetManager assetManager) {
        textureStore = new LoadingResourceStore<TextureRegion>(TextureRegion.class, assetManager);
        generatedResourceStore = new GeneratedResourceStore();
    }

    public IResource<TextureRegion> getTexture(String filename) {
        return textureStore.get(filename);
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
    
}
