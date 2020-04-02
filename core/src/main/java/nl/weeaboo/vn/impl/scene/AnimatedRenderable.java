package nl.weeaboo.vn.impl.scene;

import nl.weeaboo.common.Area2D;
import nl.weeaboo.vn.core.IAnimation;
import nl.weeaboo.vn.core.IEventListener;
import nl.weeaboo.vn.impl.core.Animation;
import nl.weeaboo.vn.render.IDrawBuffer;
import nl.weeaboo.vn.scene.IDrawable;
import nl.weeaboo.vn.scene.IRenderable;

/**
 * Base implementation for classes implementing both {@link IRenderable} and {@link IAnimation}.
 */
public abstract class AnimatedRenderable extends AbstractRenderable implements IAnimation {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private final Animation animation;

    private boolean prepared;

    protected AnimatedRenderable(double duration) {
        animation = new Animation(duration);
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
            animation.update();

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
        return animation.isFinished();
    }

    /**
     * @see Animation#getNormalizedTime()
     */
    public final double getNormalizedTime() {
        return animation.getNormalizedTime();
    }

    /**
     * @see Animation#getTime()
     */
    public final double getTime() {
        return animation.getTime();
    }

    /**
     * @see Animation#setTime(double)
     */
    public void setTime(double newTime) {
        animation.setTime(newTime);
    }

    /**
     * @see Animation#getDuration()
     */
    public double getDuration() {
        return animation.getDuration();
    }

    @Override
    public void setSpeed(double s) {
        animation.setSpeed(s);
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
