package nl.weeaboo.vn.core.impl;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.ISkipState;
import nl.weeaboo.vn.core.SkipMode;

final class SkipState implements ISkipState {

    private static final long serialVersionUID = CoreImpl.serialVersionUID;

    private SkipMode skipMode = SkipMode.NONE;

    @Override
    public boolean isSkipping() {
        return getSkipMode() != SkipMode.NONE;
    }

    @Override
    public SkipMode getSkipMode() {
        return skipMode;
    }

    @Override
    public void setSkipMode(SkipMode mode) {
        skipMode = Checks.checkNotNull(mode);
    }

    @Override
    public final void stopSkipping() {
        setSkipMode(SkipMode.NONE);
    }

}
