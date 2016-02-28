package nl.weeaboo.vn.core.impl;

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

    public void setCanExit(boolean canExit) {
        this.canExit = canExit;
    }

    public void setTouchScreen(boolean isTouchScreen) {
        this.isTouchScreen = isTouchScreen;
    }

}
