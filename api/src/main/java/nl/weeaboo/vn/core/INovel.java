package nl.weeaboo.vn.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;

import nl.weeaboo.vn.render.IDrawBuffer;

public interface INovel extends IUpdateable {

    /**
     * @return The global environment.
     */
    IEnvironment getEnv();

    /**
     * Main entry point. Start script execution by calling the main script method
     * @throws InitException If a fatal error occurs during initialization.
     */
    void start(String mainFunctionName) throws InitException;

    /**
     * Restart the novel, starting over from the title screen.
     * @throws InitException If a fatal error occurs during initialization.
     */
    void restart() throws InitException;

    /**
     * Stops script execution and cleans up native resources.
     */
    void stop();

    /**
     * Read state from storage.
     *
     * @throws IOException If an I/O error occurs while reading from the input.
     * @throws ClassNotFoundException If a persisted object type can no longer be found, which may happen when
     *         attempting to load incompatible saved state.
     */
    void readAttributes(ObjectInputStream in) throws IOException, ClassNotFoundException;

    /**
     * Write the novel's current state to storage.
     *
     * @throws IOException If an I/O error occurs while writing data to the output.
     */
    void writeAttributes(ObjectOutput out) throws IOException;

    /** Draws all visible items into the supplied draw buffer. */
    void draw(IDrawBuffer drawbuffer);

    /** This method is called from the render thread, allowing you to access the OpenGL context. */
    void updateInRenderThread();

}
