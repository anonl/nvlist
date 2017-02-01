package nl.weeaboo.vn.impl.render;

import nl.weeaboo.vn.render.IAsyncRenderTask;

public abstract class AsyncRenderTask implements IAsyncRenderTask {

    private static final long serialVersionUID = RenderImpl.serialVersionUID;

    protected boolean isTransient;

    private boolean cancelled;

    @Override
    public void cancel() {
        cancelled = true;
    }

    @Override
    public boolean isFailed() {
        return cancelled;
    }

    @Override
    public final boolean isTransient() {
        return isTransient;
    }

}
