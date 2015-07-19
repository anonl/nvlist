package nl.weeaboo.vn.core.impl;

import java.io.Serializable;

import nl.weeaboo.entity.Entity;
import nl.weeaboo.vn.core.ILayer;
import nl.weeaboo.vn.core.IScreen;
import nl.weeaboo.vn.image.impl.ImagePart;
import nl.weeaboo.vn.script.impl.ScriptPart;

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

}
