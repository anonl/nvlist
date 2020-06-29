package nl.weeaboo.vn.core;

import nl.weeaboo.vn.render.DisplayMode;

/**
 * Exposes properties of the system environment we're running in.
 */
public interface ISystemEnv {

    /**
     * {@code true} if the app is able to close itself.
     */
    boolean canExit();

    /**
     * {@code true} when running on a touchscreen device.
     */
    boolean isTouchScreen();

    /**
     * Returns {@code true} if the given display mode is currently supported. Available modes may depend on
     * the platform (desktop, mobile).
     */
    boolean isDisplayModeSupported(DisplayMode mode);

    /**
     * Returns the current display mode.
     *
     * @see ISystemModule#setDisplayMode(DisplayMode)
     */
    DisplayMode getDisplayMode();

}
