package nl.weeaboo.vn.image.impl;

public final class EmptyScreenshot extends AbstractScreenshot {

    private static final long serialVersionUID = 1L;

    private static final EmptyScreenshot INSTANCE = new EmptyScreenshot();

    private EmptyScreenshot() {
        super((short)0, false);
    }

    public static EmptyScreenshot getInstance() {
        return INSTANCE;
    }

}
