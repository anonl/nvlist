package nl.weeaboo.vn.buildtools.optimizer.sound;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.buildtools.file.IEncodedResource;
import nl.weeaboo.vn.sound.desc.ISoundDefinition;

public final class SoundWithDef {

    private final IEncodedResource audioData;
    private final ISoundDefinition def;

    public SoundWithDef(IEncodedResource audioData, ISoundDefinition def) {
        this.audioData = Checks.checkNotNull(audioData);
        this.def = Checks.checkNotNull(def);
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
    public ISoundDefinition getDef() {
        return def;
    }

}
