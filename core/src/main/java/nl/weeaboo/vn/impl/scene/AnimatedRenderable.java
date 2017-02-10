package nl.weeaboo.vn.impl.scene;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.IEventListener;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IAnimatedRenderable;
import nl.weeaboo.vn.scene.IDrawable;

public abstract class AnimatedRenderable extends AbstractRenderable implements IAnimatedRenderable {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private boolean prepared;
    private double time;
    private double duration;

    protected AnimatedRenderable(double duration) {
        this.duration = Checks.checkRange(duration, "duration", 0);
    }

    @Override
    public void onAttached(IEventListener cl) {
        super.onAttached(cl);

        checkedPrepare();
    }

    @Override
    public void onDetached(IEventListener cl) {
        super.onDetached(cl);

        checkedDispose();
    }

    @Override
    public void update() {
        super.update();

        if (!isFinished()) {
            setTime(time + 1);

            checkedPrepare();
            updateResources();

            if (isFinished()) {
                onFinished();
            }
        }
    }

    private void checkedPrepare() {
        if (!prepared) {
            prepareResources();
            prepared = true;
        }
    }

    private void checkedDispose() {
        if (prepared) {
            disposeResources();
            prepared = false;
        }
    }

    protected void onFinished() {
    }

    protected void prepareResources() {
    }

    protected void updateResources() {
    }

    protected void disposeResources() {
    }

    @Override
    public boolean isFinished() {
        return getNormalizedTime() >= 1.0;
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

    @Override
    protected final void render(IDrawBuffer drawBuffer, IDrawable parent, Area2D bounds) {
        if (isFinished()) {
            renderEnd(drawBuffer, parent, bounds);
        } else if (getNormalizedTime() <= 0) {
            renderStart(drawBuffer, parent, bounds);
        } else {
            checkedRenderIntermediate(drawBuffer, parent, bounds);
        }
    }

    private void checkedRenderIntermediate(IDrawBuffer drawBuffer, IDrawable parent, Area2D bounds) {
        checkedPrepare();

        renderIntermediate(drawBuffer, parent, bounds);
    }

    protected void renderStart(IDrawBuffer drawBuffer, IDrawable parent, Area2D bounds) {
        checkedRenderIntermediate(drawBuffer, parent, bounds);
    }

    protected abstract void renderIntermediate(IDrawBuffer drawBuffer, IDrawable parent, Area2D bounds);

    protected void renderEnd(IDrawBuffer drawBuffer, IDrawable parent, Area2D bounds) {
        checkedRenderIntermediate(drawBuffer, parent, bounds);
    }

}
