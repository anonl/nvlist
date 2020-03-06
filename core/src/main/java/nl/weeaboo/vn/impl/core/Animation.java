package nl.weeaboo.vn.impl.core;

import java.io.Serializable;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.IAnimation;
import nl.weeaboo.vn.core.IUpdateable;

public final class Animation implements IAnimation, IUpdateable, Serializable {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    private double time;
    private double duration;

    private double speed = 1.0;

    public Animation(double duration) {
        this.duration = Checks.checkRange(duration, "duration", 0);
    }

    @Override
    public boolean isFinished() {
        return getNormalizedTime() >= 1.0;
    }

    @Override
    public void setSpeed(double s) {
        this.speed = Checks.checkRange(s, "speed", 0.001);
    }

    @Override
    public void update() {
        if (!isFinished()) {
            setTime(time + speed);
        }
    }

    /**
     * @return The current time, normalized to the range {@code [0.0, 1.0]}.
     */
    public double getNormalizedTime() {
        double duration = getDuration();
        if (duration <= 0) {
            return 1.0;
        }
        return getTime() / duration;
    }

    /**
     * @return The current time in the range {@code [0.0, duration]}.
     * @see #getDuration()
     */
    public double getTime() {
        return time;
    }

    /**
     * Changes the current time.
     * @see #getTime()
     */
    public void setTime(double newTime) {
        time = Math.min(getDuration(), newTime);
    }

    /**
     * @return The total duration of the animation (in frames).
     */
    public double getDuration() {
        return duration;
    }

}
