package nl.weeaboo.vn.impl.core;

import java.util.Locale;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.ISystemEnv;
import nl.weeaboo.vn.render.DisplayMode;

/**
 * Default implementation of {@link ISystemEnv}.
 */
public class SystemEnv implements ISystemEnv {

    private final ApplicationType appType;

    public SystemEnv(ApplicationType type) {
        this.appType = Checks.checkNotNull(type);
    }

    @Override
    public boolean canExit() {
        return appType == ApplicationType.Desktop || appType == ApplicationType.HeadlessDesktop;
    }

    @Override
    public boolean isTouchScreen() {
        return appType == ApplicationType.Android || appType == ApplicationType.iOS;
    }

    @Override
    public boolean isDisplayModeSupported(DisplayMode mode) {
        switch (mode) {
        case FULL_SCREEN:
            return true;
        case WINDOWED:
            return appType == ApplicationType.Desktop || appType == ApplicationType.HeadlessDesktop;
        default:
            return false;
        }
    }

    @Override
    public DisplayMode getDisplayMode() {
        return Gdx.graphics.isFullscreen() ? DisplayMode.FULL_SCREEN : DisplayMode.WINDOWED;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "%s[canExit=%s, isTouchScreen=%s]",
                getClass().getSimpleName(), canExit(), isTouchScreen());
    }

}
