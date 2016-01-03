package nl.weeaboo.vn.core.impl;

import java.io.Serializable;

import nl.weeaboo.vn.scene.impl.Screen;
import nl.weeaboo.vn.script.IScriptContext;

public final class ContextArgs implements Cloneable, Serializable {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    public Screen screen;
    public IScriptContext scriptContext;

    @Override
    public ContextArgs clone() {
        try {
            return (ContextArgs)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
