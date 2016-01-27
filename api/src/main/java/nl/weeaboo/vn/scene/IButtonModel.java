package nl.weeaboo.vn.scene;

import java.io.Serializable;

public interface IButtonModel extends Serializable {

    /**
     * Buttons store internally if they've been pressed. This method returns if that has been the case and
     * clears the internal pressed flag.
     */
    boolean consumePress();

    /**
     * @return {@code true} if the mouse is hovering over this button.
     */
    boolean isRollover();

    void setRollover(boolean rollover);

    /**
     * @return {@code true} if this button is currently being pressed.
     */
    boolean isPressed();

    void setPressed(boolean pressed);

    /**
     * @return {@code true} if this button is enabled.
     */
    boolean isEnabled();

    /**
     * Enables or disables the button. A disabled button can't be pressed.
     */
    void setEnabled(boolean e);

    /**
     * @return {@code true} if {@link #setSelected(boolean)} has been used to manually set the selected state,
     *         or if this button is a toggle button and is currently toggled (pressed).
     */
    boolean isSelected();

    /**
     * Changes the selected state for this button. For toggle buttons, the selected state determines whether
     * the buttons stays pressed or not.
     */
    void setSelected(boolean s);

    /**
     * @return {@code true} if this button is a toggle button.
     */
    boolean isToggle();

    /**
     * Changes if this button functions as a regular button or a toggle button (stays selected when pressed).
     */
    void setToggle(boolean t);

}
