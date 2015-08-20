package nl.weeaboo.vn.core;

import java.io.Serializable;

/**
 * Interface for the part of an entity that is responsible for distributing input events (keyboard, mouse,
 * gamepad) to listeners.
 */
public interface IInputHandlerPart extends IInputListener {

    /** Adds an instance of the given input handler */
    public <I extends IInputListener & Serializable> void addInputListener(I handler);

    /** Tries to remove the first instance of the given input handler */
    public void removeInputListener(IInputListener handler);

}
