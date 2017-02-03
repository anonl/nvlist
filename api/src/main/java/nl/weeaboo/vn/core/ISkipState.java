package nl.weeaboo.vn.core;

import java.io.Serializable;

import nl.weeaboo.vn.input.IInput;

/** Skip, pause, auto read state. */
public interface ISkipState extends Serializable {

    /** {@code true} if currently skipping. */
    boolean isSkipping();

    /** {@code true} if the text line should be skipped according to the current skip mode. */
    boolean shouldSkipLine(boolean lineRead);

    /** The current skip mode. */
    SkipMode getSkipMode();

    /** Changes the current skip mode. */
    void setSkipMode(SkipMode mode);

    /**
     * Increases the skip level to the specified mode. If the skip level is already at that level or higher,
     * nothing is changed.
     */
    void skip(SkipMode mode);

    /**
     * Equivalent to {@code setSkipMode(SkipMode.NONE)}
     *
     * @see #setSkipMode(SkipMode)
     */
    void stopSkipping();

    /** Process user input. */
    void handleInput(IInput input);

}
