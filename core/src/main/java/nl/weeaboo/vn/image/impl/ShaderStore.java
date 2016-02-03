package nl.weeaboo.vn.image.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;

import nl.weeaboo.filesystem.FileSystemUtil;
import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.core.impl.StaticRef;

public class ShaderStore {

    private static final Logger LOG = LoggerFactory.getLogger(ShaderStore.class);

    private final StaticRef<IFileSystem> fileSystemRef = StaticEnvironment.FILE_SYSTEM;

    public ShaderProgram createShader(String filename) throws IOException {
        IFileSystem fileSystem = fileSystemRef.get();

        String vertexProgram = FileSystemUtil.readString(fileSystem, getVertexFilename(filename));
        LOG.trace("Vertex shader loaded:\n{}", vertexProgram);

        String fragmentProgram = FileSystemUtil.readString(fileSystem, getFragmentFilename(filename));
        LOG.trace("Fragment shader loaded:\n{}", fragmentProgram);

        ShaderProgram shader = new ShaderProgram(vertexProgram, fragmentProgram);
        if (!shader.isCompiled()) {
            String errorMessage = shader.getLog();
            shader.dispose();
            throw new IOException("Shader compilation error: " + errorMessage);
        }
        return shader;
    }

    private String getVertexFilename(String baseName) {
        return "shader/" + baseName + ".vertex.glsl";
    }
    private String getFragmentFilename(String baseName) {
        return "shader/" + baseName + ".fragment.glsl";
    }

}
