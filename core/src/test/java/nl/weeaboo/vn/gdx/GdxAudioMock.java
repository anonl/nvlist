package nl.weeaboo.vn.gdx;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.backends.headless.mock.audio.MockAudio;
import com.badlogic.gdx.files.FileHandle;

final class GdxAudioMock extends MockAudio {

    @Override
    public Music newMusic(FileHandle file) {
        return new GdxMusicMock();
    }

}
