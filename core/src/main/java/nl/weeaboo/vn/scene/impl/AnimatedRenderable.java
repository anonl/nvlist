package nl.weeaboo.vn.scene.impl;

import nl.weeaboo.vn.core.IEventListener;

public abstract class AnimatedRenderable extends AbstractRenderable {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private boolean prepared;
    private double time;
    private double duration;

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

            if (isFinished()) {
                onFinished();
            } else {
                checkedPrepare();
                updateResources();
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
        checkedDispose();
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

}
