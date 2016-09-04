package nl.weeaboo.vn.image.impl;

import com.badlogic.gdx.graphics.Texture;

import nl.weeaboo.gdx.res.LoadingResourceStore;
import nl.weeaboo.vn.core.impl.StaticRef;

public final class GdxTextureStore extends LoadingResourceStore<Texture> {

    public GdxTextureStore(StaticRef<GdxTextureStore> selfId) {
        super(selfId, Texture.class);
    }

}
