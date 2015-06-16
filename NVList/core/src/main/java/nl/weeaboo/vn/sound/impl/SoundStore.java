package nl.weeaboo.vn.sound.impl;

import java.io.Serializable;

public abstract class SoundStore implements Serializable {

    private static final long serialVersionUID = SoundImpl.serialVersionUID;

    public abstract String getDisplayName(String filename);

}
