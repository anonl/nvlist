package nl.weeaboo.vn.core;

/**
 * Animated effect.
 */
public interface IAnimation {

    /**
     * @return {@code true} if the animation is finished.
     */
    boolean isFinished();

    /**
     * Changes the relative speed of the animation, where {@code 1.0} is normal and {@code 0.5} is half-speed.
     *
     * @param s The new speed (must be positive).
     */
    public void setSpeed(double s);

}
