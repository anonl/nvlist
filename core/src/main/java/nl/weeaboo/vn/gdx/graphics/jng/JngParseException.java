package nl.weeaboo.vn.gdx.graphics.jng;

import java.io.IOException;

public final class JngParseException extends IOException {

    private static final long serialVersionUID = 1L;

    public JngParseException(String message) {
        super(message);
    }

    public JngParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
