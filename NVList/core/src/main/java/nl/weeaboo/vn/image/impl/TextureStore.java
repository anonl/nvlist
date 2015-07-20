package nl.weeaboo.vn.image.impl;

import com.badlogic.gdx.graphics.Texture;

import nl.weeaboo.gdx.res.LoadingResourceStore;
import nl.weeaboo.vn.core.impl.StaticRef;

public class TextureStore extends LoadingResourceStore<Texture> {

    public TextureStore(StaticRef<TextureStore> selfId) {
        super(selfId, Texture.class);
    }

}
