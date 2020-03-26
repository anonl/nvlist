package nl.weeaboo.vn.impl.sound;

import com.badlogic.gdx.audio.Music;
import com.google.common.collect.ImmutableSet;

import nl.weeaboo.vn.gdx.res.LoadingResourceStore;
import nl.weeaboo.vn.impl.core.StaticRef;

/**
 * Low-level resource loader for music.
 */
public final class GdxMusicStore extends LoadingResourceStore<Music> {

    public GdxMusicStore(StaticRef<GdxMusicStore> selfId) {
        super(selfId, Music.class);
    }

    /**
     * Returns the set of file extensions (without the '.' prefix) supported by the load methods of this
     * class.
     */
    public static ImmutableSet<String> getSupportedFileExts() {
        return ImmutableSet.of("ogg", "mp3");
    }

}
