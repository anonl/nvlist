package nl.weeaboo.vn.scene.impl;

import nl.weeaboo.vn.NvlTestUtil;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.scene.signal.RenderEnvChangeSignal;

public class RootLayerStub extends Layer {

    private static final long serialVersionUID = 1L;

    private IRenderEnv renderEnv = NvlTestUtil.BASIC_ENV;

    @Override
    public IRenderEnv getRenderEnv() {
        return renderEnv;
    }

    public void setRenderEnv(IRenderEnv env) {
        renderEnv = env;

        SceneUtil.sendSignal(this, new RenderEnvChangeSignal(env));
    }

}
