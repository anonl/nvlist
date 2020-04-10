package nl.weeaboo.vn.impl.image;

import nl.weeaboo.vn.image.IScreenshot;

/**
 * Empty {@link IScreenshot}.
 */
public final class EmptyScreenshot extends AbstractScreenshot {

    private static final long serialVersionUID = 1L;

    private static final EmptyScreenshot INSTANCE = new EmptyScreenshot();

    private EmptyScreenshot() {
        super((short)0, false);
        cancel();
    }

    /** Constructor function. */
    public static EmptyScreenshot getInstance() {
        return INSTANCE;
    }

}
