package nl.weeaboo.vn.save;

import java.io.IOException;

public final class SaveFormatException extends IOException {

    private static final long serialVersionUID = 1L;

    public SaveFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public SaveFormatException(String message) {
        super(message);
    }
    
}
