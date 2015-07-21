package nl.weeaboo.vn.sound.impl;

import com.badlogic.gdx.audio.Music;

import nl.weeaboo.gdx.res.LoadingResourceStore;
import nl.weeaboo.vn.core.impl.StaticRef;

public class MusicStore extends LoadingResourceStore<Music> {

    public MusicStore(StaticRef<MusicStore> selfId) {
        super(selfId, Music.class);
    }

}
