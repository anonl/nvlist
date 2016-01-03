package nl.weeaboo.vn.scene.signal;

import nl.weeaboo.vn.core.IRenderEnv;

public final class RenderEnvChangeSignal extends AbstractSignal {

    private final IRenderEnv renderEnv;

    public RenderEnvChangeSignal(IRenderEnv renderEnv) {
        this.renderEnv = renderEnv;
    }

    public IRenderEnv getRenderEnv() {
        return renderEnv;
    }

}
