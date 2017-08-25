package nl.weeaboo.vn.impl.core;

import javax.annotation.Nullable;

import nl.weeaboo.vn.core.IContext;
import nl.weeaboo.vn.scene.IScreen;

public final class ContextUtil {

    private static final ThreadLocal<IContext> currentContext = new ThreadLocal<>();

    private ContextUtil() {
    }

    /**
     * @return The current context, or {@code null} if no context is current.
     * @see #setCurrentContext(IContext)
     */
    public static IContext getCurrentContext() {
        return currentContext.get();
    }

    /**
     * Makes the given context <em>current</em>.
     * @see IContext#onCurrent()
     * @see #getCurrentContext()
     */
    public static IContext setCurrentContext(IContext context) {
        IContext oldContext = getCurrentContext();
        currentContext.set(context);
        if (context != null) {
            context.onCurrent();
        }
        return oldContext;
    }

    /**
     * @return The screen of the current context, or {@code null} if no context is current.
     */
    public static @Nullable IScreen getCurrentScreen() {
        IContext context = getCurrentContext();
        if (context == null) {
            return null;
        }
        return context.getScreen();
    }

}
