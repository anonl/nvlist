package nl.weeaboo.vn.sound.impl;

import com.badlogic.gdx.audio.Music;

import nl.weeaboo.gdx.res.LoadingResourceStore;
import nl.weeaboo.vn.core.impl.StaticRef;

public final class GdxMusicStore extends LoadingResourceStore<Music> {

    public GdxMusicStore(StaticRef<GdxMusicStore> selfId) {
        super(selfId, Music.class);
    }

}
