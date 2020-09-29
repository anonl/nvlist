package nl.weeaboo.vn.impl.sound;

import java.util.Collection;

import javax.annotation.Nullable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.vn.core.MediaType;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.core.ResourceLoadInfo;
import nl.weeaboo.vn.impl.core.PreferenceStoreMock;
import nl.weeaboo.vn.impl.core.TestEnvironment;
import nl.weeaboo.vn.sound.ISound;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.SoundType;

public final class SoundModuleTest {

    private TestEnvironment env;
    private SoundControllerMock soundController;
    private SoundModule module;

    @Before
    public void before() {
        env = TestEnvironment.newInstance();
        soundController = new SoundControllerMock();
        module = new SoundModule(new SoundResourceLoader(env), soundController, new NativeAudioFactory());
    }

    @After
    public void after() {
        env.destroy();
    }

    /**
     * A few methods on {@link SoundModule} should call equivalent methods on an embedded
     * {@link ISoundController} object.
     */
    @Test
    public void testDelegateToSoundController() {
        Assert.assertEquals(soundController, module.getSoundController());

        // Update needs to call the update method on the sound controller
        module.update();
        soundController.consumeUpdateCount(1);

        // General NVList preferences for audio volume are forwarded to the sound controller
        PreferenceStoreMock prefsStore = new PreferenceStoreMock();
        prefsStore.set(NovelPrefs.MUSIC_VOLUME, .1);
        prefsStore.set(NovelPrefs.SOUND_EFFECT_VOLUME, .2);
        prefsStore.set(NovelPrefs.VOICE_VOLUME, .3);
        module.onPrefsChanged(prefsStore);
        assertMasterVolume(SoundType.MUSIC, .1);
        assertMasterVolume(SoundType.SOUND, .2);
        assertMasterVolume(SoundType.VOICE, .3);

        // Destroying the module stops all playing sounds
        module.destroy();
        soundController.consumeStopAllCount(1);
    }

    @Test
    public void testCreateSound() {
        // Returns null if the audio file doesn't exist
        Assert.assertNull(createSound("missing"));

        // Create a valid sound
        ISound sound = createSound("dummy.ogg");
        Assert.assertNotNull(sound);
        Assert.assertEquals(FilePath.of("dummy.ogg"), sound.getFilename());
    }

    @Test
    public void testGetDisplayName() {
        // If the file doesn't exist, returns null
        assertDisplayName("missing", null);

        // If the file doesn't have a display name, returns null
        assertDisplayName("dummy.ogg", null);

        // Happy flow: Sound with display name
        assertDisplayName("alpha.ogg", "Alpha");
    }

    @Test
    public void testGetSoundFiles() {
        Collection<FilePath> files = module.getSoundFiles(FilePath.empty());
        Assert.assertTrue(files.toString(),
                files.contains(FilePath.of("alpha.ogg")));
    }

    private void assertDisplayName(String path, @Nullable String expected) {
        Assert.assertEquals(expected, module.getDisplayName(FilePath.of(path)));
    }

    private @Nullable ISound createSound(String path) {
        ResourceLoadInfo loadInfo = new ResourceLoadInfo(MediaType.SOUND, FilePath.of(path));
        return module.createSound(SoundType.SOUND, loadInfo);
    }

    private void assertMasterVolume(SoundType type, double expectedValue) {
        Assert.assertEquals(expectedValue, soundController.getMasterVolume(type), 1e-3);
    }
}
