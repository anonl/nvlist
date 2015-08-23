package nl.weeaboo.vn.core.impl;

import nl.weeaboo.entity.PartRegistry;
import nl.weeaboo.entity.PartType;
import nl.weeaboo.vn.core.IButtonPart;
import nl.weeaboo.vn.core.IDrawablePart;
import nl.weeaboo.vn.core.IInputHandlerPart;
import nl.weeaboo.vn.core.ITransformablePart;
import nl.weeaboo.vn.image.IImagePart;
import nl.weeaboo.vn.script.IScriptPart;
import nl.weeaboo.vn.sound.ISoundPart;
import nl.weeaboo.vn.text.ITextPart;

public class BasicPartRegistry extends PartRegistry {

	private static final long serialVersionUID = 1L;

    public final PartType<IScriptPart> script;
    public final PartType<IInputHandlerPart> input;

    public final PartType<IDrawablePart> drawable;
    public final PartType<ITransformablePart> transformable;
    public final PartType<IImagePart> image;
    public final PartType<IButtonPart> button;
    // public final PartType<IButtonDrawablePart> buttonDrawable;
    public final PartType<ITextPart> text;

    public final PartType<ISoundPart> sound;

	public BasicPartRegistry() {
        script = register("script", IScriptPart.class);
        input = register("input", IInputHandlerPart.class);

        drawable = register("drawable", IDrawablePart.class);
        transformable = register("transformable", ITransformablePart.class);
        image = register("image", IImagePart.class);
        button = register("button", IButtonPart.class);
        // buttonDrawable = register("buttonDrawable", IButtonDrawable.class);
        text = register("text", ITextPart.class);

        sound = register("sound", ISoundPart.class);
	}

}
