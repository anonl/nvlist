package nl.weeaboo.vn.core;

public interface IAnimation {

    /**
     * @return {@code true} if the animation is finished.
     */
    boolean isFinished();

    /**
     * Changes the relative speed of the animation, where {@code 1.0} is normal and {@code 0.5} is hald-speed.
     *
     * @param s The new speed (must be positive).
     */
    public void setSpeed(double s);

}
