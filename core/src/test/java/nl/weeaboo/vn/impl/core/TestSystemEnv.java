package nl.weeaboo.vn.impl.core;

import nl.weeaboo.vn.core.ISystemEnv;

public class TestSystemEnv implements ISystemEnv {

    private boolean canExit;
    private boolean isTouchScreen;

    @Override
    public boolean canExit() {
        return canExit;
    }

    @Override
    public boolean isTouchScreen() {
        return isTouchScreen;
    }

    /** Sets the value returned by {@link #canExit()}. */
    public void setCanExit(boolean canExit) {
        this.canExit = canExit;
    }

    /** Sets the value returned by {@link #isTouchScreen()}. */
    public void setTouchScreen(boolean isTouchScreen) {
        this.isTouchScreen = isTouchScreen;
    }

}
