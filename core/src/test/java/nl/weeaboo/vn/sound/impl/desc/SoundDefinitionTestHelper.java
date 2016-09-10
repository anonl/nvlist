package nl.weeaboo.vn.sound.impl.desc;

import org.junit.Assert;

final class SoundDefinitionTestHelper {

    public SoundDefinition findDefById(Iterable<SoundDefinition> available, String filename) {
        for (SoundDefinition def : available) {
            if (def.getFilename().equals(filename)) {
                return def;
            }
        }
        throw new AssertionError("Not found: " + filename);
    }

    public void assertEquals(SoundDefinition expected, SoundDefinition actual) {
        Assert.assertEquals(expected.getFilename(), actual.getFilename());
        Assert.assertEquals(expected.getDisplayName(), actual.getDisplayName());
    }

    public SoundDefinition createSoundDef(String id) {
        return new SoundDefinition(id, id);
    }

}
