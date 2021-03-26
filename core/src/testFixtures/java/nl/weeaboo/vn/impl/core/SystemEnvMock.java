package nl.weeaboo.vn.impl.core;

import java.util.Objects;

import nl.weeaboo.vn.core.ISystemEnv;
import nl.weeaboo.vn.render.DisplayMode;

public class SystemEnvMock implements ISystemEnv {

    private boolean canExit;
    private boolean isTouchScreen;
    private DisplayMode displayMode = DisplayMode.FULL_SCREEN;

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

    @Override
    public DisplayMode getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(DisplayMode mode) {
        this.displayMode = Objects.requireNonNull(mode);
    }

    @Override
    public boolean isDisplayModeSupported(DisplayMode mode) {
        return true;
    }

}
