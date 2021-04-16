package nl.weeaboo.vn.buildtools.optimizer.sound;

import java.io.IOException;

import nl.weeaboo.vn.buildtools.file.IEncodedResource;
import nl.weeaboo.vn.impl.sound.desc.SoundDefinition;
import nl.weeaboo.vn.sound.desc.ISoundDefinition;

/**
 * Encoded audio resource.
 */
public final class EncodedSound implements IEncodedResource {

    private final IEncodedResource encodedSound;
    private SoundDefinition soundDefinition;

    public EncodedSound(IEncodedResource encodedSound, ISoundDefinition def) {
        this.encodedSound = encodedSound;
        this.soundDefinition = SoundDefinition.from(def);
    }

    @Override
    public void dispose() {
        encodedSound.dispose();
    }

    @Override
    public byte[] readBytes() throws IOException {
        return encodedSound.readBytes();
    }

    /** Returns the {@link ISoundDefinition} accompanying the sound. */
    public SoundDefinition getDef() {
        return soundDefinition;
    }

    /**
     * @see #getDef()
     */
    public void setDef(SoundDefinition def) {
        soundDefinition = def;
    }

    @Override
    public long getFileSize() throws IOException {
        return encodedSound.getFileSize();
    }

}
