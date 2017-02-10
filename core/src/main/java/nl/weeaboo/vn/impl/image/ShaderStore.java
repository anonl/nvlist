package nl.weeaboo.vn.impl.image;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import nl.weeaboo.filesystem.FilePath;
import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;

public class ShaderStore {

    private static final Logger LOG = LoggerFactory.getLogger(ShaderStore.class);

    private final StaticRef<IFileSystem> fileSystemRef = StaticEnvironment.FILE_SYSTEM;

    /**
     * Loads a shader from the file system ({@link StaticEnvironment#FILE_SYSTEM}).
     *
     * @throws IOException If the shader can't be loaded.
     */
    public ShaderProgram createShader(String filename) throws IOException {
        IFileSystem fileSystem = fileSystemRef.get();

        String vertexProgram = FileSystemUtil.readString(fileSystem,
                FilePath.of("shader/" + filename + ".vertex.glsl"));
        LOG.trace("Vertex shader loaded:\n{}", vertexProgram);

        String fragmentProgram = FileSystemUtil.readString(fileSystem,
                FilePath.of("shader/" + filename + ".fragment.glsl"));
        LOG.trace("Fragment shader loaded:\n{}", fragmentProgram);

        return createShaderFromSources(vertexProgram, fragmentProgram);
    }

    /**
     * Loads a shader from classpath resources.
     *
     * @throws IOException If compilation failed.
     */
    public ShaderProgram createShaderFromClasspath(Class<?> context, String baseResourcePath) throws IOException {
        String vertexPath = baseResourcePath + ".vert";
        URL vertexUrl = context.getResource(vertexPath);
        if (vertexUrl == null) {
            throw new FileNotFoundException("Resource: " + context + ", " + vertexPath);
        }

        String fragmentPath = baseResourcePath + ".frag";
        URL fragmentUrl = context.getResource(fragmentPath);
        if (fragmentUrl == null) {
            throw new FileNotFoundException("Resource: " + context + ", " + fragmentUrl);
        }

        String vertexProgram = Resources.toString(vertexUrl, Charsets.UTF_8);
        LOG.trace("Vertex shader loaded:\n{}", vertexProgram);

        String fragmentProgram = Resources.toString(fragmentUrl, Charsets.UTF_8);
        LOG.trace("Fragment shader loaded:\n{}", fragmentProgram);

        return createShaderFromSources(vertexProgram, fragmentProgram);
    }

    /**
     * Compiles a shader from vertex/fragment program sourcecode.
     *
     * @throws IOException If compilation failed.
     */
    public ShaderProgram createShaderFromSources(String vertexProgram, String fragmentProgram) throws IOException {
        ShaderProgram shader = new ShaderProgram(vertexProgram, fragmentProgram);
        if (!shader.isCompiled()) {
            String errorMessage = shader.getLog();
            shader.dispose();
            throw new IOException("Shader compilation error: " + errorMessage);
        }
        return shader;
    }

}
