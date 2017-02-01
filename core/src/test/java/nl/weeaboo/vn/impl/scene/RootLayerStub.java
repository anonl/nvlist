package nl.weeaboo.vn.impl.scene;

import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.impl.scene.Layer;
import nl.weeaboo.vn.impl.scene.SceneUtil;
import nl.weeaboo.vn.impl.test.CoreTestUtil;
import nl.weeaboo.vn.signal.RenderEnvChangeSignal;

public class RootLayerStub extends Layer {

    private static final long serialVersionUID = 1L;

    private IRenderEnv renderEnv = CoreTestUtil.BASIC_ENV;

    @Override
    public IRenderEnv getRenderEnv() {
        return renderEnv;
    }

    public void setRenderEnv(IRenderEnv env) {
        renderEnv = env;

        SceneUtil.sendSignal(this, new RenderEnvChangeSignal(env));
    }

}
