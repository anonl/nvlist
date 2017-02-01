package nl.weeaboo.vn.impl.sound;

import com.badlogic.gdx.audio.Music;

import nl.weeaboo.vn.gdx.res.LoadingResourceStore;
import nl.weeaboo.vn.impl.core.StaticRef;

public final class GdxMusicStore extends LoadingResourceStore<Music> {

    public GdxMusicStore(StaticRef<GdxMusicStore> selfId) {
        super(selfId, Music.class);
    }

}
