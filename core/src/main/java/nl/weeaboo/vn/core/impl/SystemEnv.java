package nl.weeaboo.vn.core.impl;

import java.util.Locale;

import com.badlogic.gdx.Application.ApplicationType;

import nl.weeaboo.common.Checks;
import nl.weeaboo.vn.core.ISystemEnv;

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
    public String toString() {
        return String.format(Locale.ROOT, "%s[canExit=%s, isTouchScreen=%s]",
                getClass().getSimpleName(), canExit(), isTouchScreen());
    }

}
