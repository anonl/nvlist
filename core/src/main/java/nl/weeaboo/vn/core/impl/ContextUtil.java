package nl.weeaboo.vn.core.impl;

import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.scene.IScreen;

public final class ContextUtil {

    private static final ThreadLocal<IContext> currentContext = new ThreadLocal<IContext>();

    private ContextUtil() {
    }

    public static IContext getCurrentContext() {
        return currentContext.get();
    }

    public static IContext setCurrentContext(IContext context) {
        IContext oldContext = getCurrentContext();
        currentContext.set(context);
        return oldContext;
    }

    public static IScreen getCurrentScreen() {
        IContext context = getCurrentContext();
        if (context == null) {
            return null;
        }
        return context.getScreen();
    }

}
