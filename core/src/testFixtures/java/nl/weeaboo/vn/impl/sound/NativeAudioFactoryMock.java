package nl.weeaboo.vn.impl.sound;

import java.util.ArrayDeque;
import java.util.Queue;

import com.badlogic.gdx.audio.Music;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.ResourceId;
import nl.weeaboo.vn.gdx.GdxMusicMock;

class NativeAudioFactoryMock implements INativeAudioFactory {

    private static final long serialVersionUID = 1L;

    private final Queue<GdxMusicMock> gdxMusics = new ArrayDeque<>();

    @Override
    public INativeAudio createNativeAudio(FilePath filePath) {
        return new NativeAudio(this, filePath);
    }

    @Override
    public Music newGdxMusic(FilePath filePath) {
        GdxMusicMock music = gdxMusics.poll();
        if (music == null) {
            music = new GdxMusicMock();
        }
        return music;
    }

    public void setNextGdxMusic(GdxMusicMock gdxMusic) {
        gdxMusics.add(gdxMusic);
    }

    @Override
    public void preloadNormalized(ResourceId resourceId) {
    }

    @Override
    public void clearCaches() {
    }

}
