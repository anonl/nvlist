package nl.weeaboo.vn.input;

import nl.weeaboo.vn.math.Matrix;
import nl.weeaboo.vn.math.Vec2;

/**
 * Provides access to user-input events.
 *
 * @param <K> Key identifier.
 */
public interface IBaseInput<K> {

    /**
     * Reset button press states.
     */
    void clearButtonStates();

    /**
     * Same as {@link #isPressed(Object, boolean)}, then marks the press as consumed so further calls
     *         return {@code false}.
     */
    boolean consumePress(K button);

    /**
     * Returns {@code true} if the specified button got pressed since the last frame.
     */
    boolean isJustPressed(K button);

    /**
     * Returns {@code true} if the specified button is currently pressed.
     */
    boolean isPressed(K button, boolean allowConsumedPress);

    /**
     * Returns the time in milliseconds that the specified button has been continuously held, or {@code 0} if
     *         that button isn't currently being held.
     */
    long getPressedTime(K button, boolean allowConsumedPress);

    /**
     * The X/Y-coordinates of the most recent mouse/touch position, transformed by the given transform.
     */
    Vec2 getPointerPos(Matrix transform);

    /**
     * @deprecated Use {@link #getPointerScrollXY()} instead.
     */
    @Deprecated
    int getPointerScroll();

    /**
     * The amount that the mouse scroll wheel was scrolled. Positive values indicate a down/right scroll,
     * negative values an up/left scroll.
     */
    Vec2 getPointerScrollXY();

    /**
     * Returns {@code true} if the user hasn't performed any type of input this frame (no button presses, no
     *         mouse/touch events).
     */
    boolean isIdle();

}
