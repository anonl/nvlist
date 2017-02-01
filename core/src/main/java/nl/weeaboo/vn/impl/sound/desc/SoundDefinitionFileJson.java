package nl.weeaboo.vn.impl.sound.desc;

final class SoundDefinitionFileJson {

    public String version;
    public SoundDefinitionJson[] sounds;

    static final class SoundDefinitionJson {

        public String file;
        public String displayName;

    }

}
