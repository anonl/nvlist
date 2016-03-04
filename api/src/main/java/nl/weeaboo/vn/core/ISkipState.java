package nl.weeaboo.vn.core;

import java.io.Serializable;

/** Skip, pause, auto read state */
public interface ISkipState extends Serializable {

    /** @return {@code true} if currently skipping. */
    boolean isSkipping();

    /** @return The current skip mode */
    SkipMode getSkipMode();

    /** Changes the current skip mode */
    void setSkipMode(SkipMode mode);

    /**
     * Equivalent to {@code setSkipMode(SkipMode.NONE)}
     *
     * @see #setSkipMode(SkipMode)
     */
    void stopSkipping();

}
