package nl.weeaboo.vn.signal;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.render.IRenderEnv;

public final class RenderEnvChangeSignal extends AbstractSignal {

    private final IRenderEnv renderEnv;

    public RenderEnvChangeSignal(IRenderEnv renderEnv) {
        this.renderEnv = Checks.checkNotNull(renderEnv);
    }

    /**
     * Returns the new rendering environment.
     */
    public IRenderEnv getRenderEnv() {
        return renderEnv;
    }

}
