package nl.weeaboo.vn.buildtools.optimizer.sound;

import com.badlogic.gdx.utils.Disposable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.buildtools.file.IEncodedResource;
import nl.weeaboo.vn.impl.sound.desc.SoundDefinition;
import nl.weeaboo.vn.sound.desc.ISoundDefinition;

public final class SoundWithDef implements Disposable {

    private final IEncodedResource audioData;
    private final SoundDefinition def;

    public SoundWithDef(IEncodedResource audioData, ISoundDefinition def) {
        this.audioData = Checks.checkNotNull(audioData);
        this.def = SoundDefinition.from(def);
    }

    @Override
    public void dispose() {
        audioData.dispose();
    }

    /**
     * Returns the encoded audio data.
     */
    public IEncodedResource getAudioData() {
        return audioData;
    }

    /**
     * Returns the sound definition.
     */
    public SoundDefinition getDef() {
        return def;
    }

}
