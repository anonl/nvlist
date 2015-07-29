package nl.weeaboo.vn.core.impl;

import java.io.Serializable;

import nl.weeaboo.entity.Entity;
import nl.weeaboo.vn.core.ILayer;
import nl.weeaboo.vn.core.IScreen;
import nl.weeaboo.vn.image.impl.ImagePart;
import nl.weeaboo.vn.script.impl.ScriptPart;
import nl.weeaboo.vn.sound.ISoundController;
import nl.weeaboo.vn.sound.SoundType;
import nl.weeaboo.vn.sound.impl.IAudioAdapter;
import nl.weeaboo.vn.sound.impl.SoundPart;
import nl.weeaboo.vn.text.impl.TextPart;

public class EntityHelper implements Serializable {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    private final BasicPartRegistry pr;

    public EntityHelper(BasicPartRegistry pr) {
        this.pr = pr;
    }

    public Entity createScriptableEntity(IScreen screen) {
        Entity e = screen.createEntity();
        addScriptParts(e);
        return e;
    }

    public Entity createScriptableEntity(ILayer layer) {
        Entity e = layer.createEntity();
        addScriptParts(e);
        return e;
    }

    public Entity addScriptParts(Entity e) {
        ScriptPart script = new ScriptPart();

        e.setPart(pr.script, script);

        return e;
    }

    public Entity addImageParts(Entity e, ILayer layer) {
        TransformablePart transformable = new TransformablePart();
        ImagePart image = new ImagePart(transformable);

        e.setPart(pr.drawable, transformable);
        e.setPart(pr.transformable, transformable);
        e.setPart(pr.image, image);

        layer.add(e); // Updates drawable.parent field

        return e;
    }

    public SoundPart addSoundPart(Entity e, ISoundController sctrl, SoundType stype, String normalized,
            IAudioAdapter audio) {

        SoundPart soundPart = new SoundPart(sctrl, stype, normalized, audio);

        e.addPart(pr.sound, soundPart);

        return soundPart;
    }

    public TextPart addTextPart(Entity e, ILayer layer) {
        TextPart textPart = new TextPart();

        e.addPart(pr.drawable, textPart);
        e.addPart(pr.text, textPart);

        layer.add(e); // Updates drawable.parent field

        return textPart;
    }

}
