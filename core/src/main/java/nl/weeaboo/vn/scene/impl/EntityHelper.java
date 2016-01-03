package nl.weeaboo.vn.scene.impl;

import java.io.Serializable;

import nl.weeaboo.vn.scene.IButton;
import nl.weeaboo.vn.scene.IImageDrawable;
import nl.weeaboo.vn.scene.ILayer;
import nl.weeaboo.vn.scene.ITextDrawable;
import nl.weeaboo.vn.script.IScriptContext;
import nl.weeaboo.vn.text.ITextRenderer;
import nl.weeaboo.vn.text.impl.TextRenderer;

public class EntityHelper implements Serializable {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private ITextRenderer createTextRenderer() {
        return new TextRenderer();
    }

    public IImageDrawable createImage(ILayer layer) {
        ImageDrawable image = new ImageDrawable();
        layer.add(image);
        return image;
    }

    public ITextDrawable createText(ILayer layer) {
        TextDrawable textDrawable = new TextDrawable(createTextRenderer());
        layer.add(textDrawable);
        return textDrawable;
    }

    public IButton createButton(ILayer layer, IScriptContext scriptContext) {
        // TODO Implement
        throw new RuntimeException("Not implemented");

        // Button button = new Button(scriptContext.getEventDispatcher(), createTextRenderer());
        // layer.add(button);
        // return button;
    }

}
