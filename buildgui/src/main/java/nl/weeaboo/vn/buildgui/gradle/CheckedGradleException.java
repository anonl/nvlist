package nl.weeaboo.vn.buildgui.gradle;

/**
 * Checked exception to wrap various types of runtime exceptions thrown by Gradle tooling API methods.
 */
public final class CheckedGradleException extends Exception {

    private static final long serialVersionUID = 1L;

    public CheckedGradleException(String message, RuntimeException cause) {
        super(message, cause);
    }

    public CheckedGradleException(String message) {
        super(message);
    }

}
