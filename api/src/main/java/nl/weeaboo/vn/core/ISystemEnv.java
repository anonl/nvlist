package nl.weeaboo.vn.core;

public interface ISystemEnv {

    /**
     * @return {@code true} if the app is able to close itself.
     */
    boolean canExit();

    /**
     * @return {@code true} when running on a touchscreen device.
     */
    boolean isTouchScreen();

}
