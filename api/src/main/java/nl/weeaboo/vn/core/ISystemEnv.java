package nl.weeaboo.vn.core;

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

}
