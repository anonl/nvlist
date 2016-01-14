package nl.weeaboo.vn.core;

import java.io.Serializable;

public interface IInput extends Serializable {

    /**
     * @param keycode The key to check/change the state of.
     * @return Same as {@link #isKeyPressed(int)}, but makes further calls to {@link #isKeyPressed(int)} and
     *         {@link #consumeKey(int)} return false.
     */
	boolean consumeKey(int keycode);

	/**
	 * @param keycode The key to check the state of.
	 * @return {@core true} if the specified key is currently pressed.
	 */
	boolean isKeyHeld(int keycode, boolean allowConsumedPress);

    /**
     * @return The time in milliseconds that the specified key has been continuously held, or {@code 0} if
     *         that key isn't currently being held.
     */
	long getKeyHeldTime(int keycode, boolean allowConsumedPress);

	/**
	 * @param keycode The key to check the state of.
	 * @return {@code true} if the specified key got pressed since the last frame.
	 */
	boolean isKeyPressed(int keycode);

	double getMouseX();
	double getMouseY();

	/**
	 * @see #consumeKey(int)
	 */
	boolean consumeMouse();
	boolean isMouseHeld(boolean allowConsumedPress);
	long getMouseHeldTime(boolean allowConsumedPress);
	boolean isMousePressed();
	int getMouseScroll();

    /**
     * @return {@code true} if the user hasn't performed any type of input this frame (no button presses, no
     *         mouse moves).
     */
	boolean isIdle();

}

