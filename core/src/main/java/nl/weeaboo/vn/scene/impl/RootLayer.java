package nl.weeaboo.vn.scene.impl;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.IRenderEnv;
import nl.weeaboo.vn.scene.IScreen;

final class RootLayer extends Layer {

    private static final long serialVersionUID = SceneImpl.serialVersionUID;

    private final IScreen screen;

    public RootLayer(IScreen screen) {
        this.screen = Checks.checkNotNull(screen);
    }

    public IScreen getScreen() {
        return screen;
    }

    @Override
    public IRenderEnv getRenderEnv() {
        return screen.getRenderEnv();
    }

}
