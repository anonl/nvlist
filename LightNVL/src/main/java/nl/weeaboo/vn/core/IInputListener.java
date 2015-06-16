package nl.weeaboo.vn.core;

public interface IInputListener {

    /**
     * Allows this input handler to process the given input. The coordinates of the given input objects are
     * generally normalized to the coordinate system used by this object.
     *
     * @param mouseContains {@code true} if the mouse coordinates in the input object lie within the visual
     *        bounds of this part. If this input listener has no accompanying visual part, this will always
     *        be false.
     */
    public void handleInput(IInput input, boolean mouseContains);

}
