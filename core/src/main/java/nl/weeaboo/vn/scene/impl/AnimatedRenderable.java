package nl.weeaboo.vn.scene.impl;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.IEventListener;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IDrawable;

public abstract class AnimatedRenderable extends AbstractRenderable {

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
            time = Math.min(getDuration(), time + 1);

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

    public boolean isFinished() {
        return getNormalizedTime() >= 1.0;
    }

    public double getNormalizedTime() {
        double duration = getDuration();
        if (duration <= 0) {
            return 1.0;
        }
        return getTime() / duration;
    }

    public double getTime() {
        return time;
    }

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
