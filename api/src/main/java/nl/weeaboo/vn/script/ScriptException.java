package nl.weeaboo.vn.script;

public class ScriptException extends Exception {

    private static final long serialVersionUID = 1L;

    public ScriptException(String message) {
        super(message);
    }

    public ScriptException(String message, Throwable cause) {
        super(message, cause);
    }

}
