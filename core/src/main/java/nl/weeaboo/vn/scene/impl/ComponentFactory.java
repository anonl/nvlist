package nl.weeaboo.vn.scene.impl;

import java.io.Serializable;

import nl.weeaboo.vn.scene.IButton;
import nl.weeaboo.vn.scene.IImageDrawable;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.ITextDrawable;
import nl.weeaboo.vn.script.IScriptContext;

public class ComponentFactory implements Serializable {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    public IImageDrawable createImage(ILayer layer) {
        ImageDrawable image = new ImageDrawable();
        layer.add(image);
        return image;
    }

    public ITextDrawable createText(ILayer layer) {
        TextDrawable textDrawable = new TextDrawable();
        textDrawable.setSize(layer.getWidth(), layer.getHeight());
        layer.add(textDrawable);
        return textDrawable;
    }

    public IButton createButton(ILayer layer, IScriptContext scriptContext) {
        Button button = new Button(scriptContext.getEventDispatcher());
        layer.add(button);
        return button;
    }

}
