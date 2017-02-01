package nl.weeaboo.vn.impl.image;

import com.badlogic.gdx.graphics.Texture;

import nl.weeaboo.vn.gdx.res.LoadingResourceStore;
import nl.weeaboo.vn.impl.core.StaticRef;

public final class GdxTextureStore extends LoadingResourceStore<Texture> {

    public GdxTextureStore(StaticRef<GdxTextureStore> selfId) {
        super(selfId, Texture.class);
    }

}
