package nl.weeaboo.vn.input;

import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.math.Vec2;

/**
 * @param <K> Key identifier.
 */
public interface IBaseInput<K> {

    /**
     * Reset button press states.
     */
    void clearButtonStates();

    /**
     * @return Same as {@link #isPressed(Object, boolean)}, then marks the press as consumed so further calls
     *         return {@code false}.
     */
    boolean consumePress(K button);

    /**
     * @return {@code true} if the specified button got pressed since the last frame.
     */
    boolean isJustPressed(K button);

    /**
     * @return {@code true} if the specified button is currently pressed.
     */
    boolean isPressed(K button, boolean allowConsumedPress);

    /**
     * @return The time in milliseconds that the specified button has been continuously held, or {@code 0} if
     *         that button isn't currently being held.
     */
    long getPressedTime(K button, boolean allowConsumedPress);

    /**
     * The X/Y-coordinates of the most recent mouse/touch position, transformed by the given transform.
     */
    Vec2 getPointerPos(Matrix transform);

    /**
     * The amount that the mouse scroll wheel was scrolled. Positive values indicate a downward scroll,
     * negative values an upward scroll.
     */
    int getPointerScroll();

    /**
     * @return {@code true} if the user hasn't performed any type of input this frame (no button presses, no
     *         mouse/touch events).
     */
    boolean isIdle();

}
