package nl.weeaboo.vn.gdx.graphics;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.IntBuffer;

import javax.annotation.Nullable;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.profiling.GL20Profiler;

public class MockGL extends GL20Profiler {

    private int textureId;
    private int shaderId;
    private int programId;

    private MockGL() {
        super(null);
    }

    /**
     * Creates a new mock GL20 instance that simulates just enough to not crash during testing.
     */
    public static GL20 newInstance() {
        final MockGL mockGL = new MockGL();
        return (GL20)Proxy.newProxyInstance(MockGL.class.getClassLoader(),
            new Class<?>[] { GL20.class },
            new MockGLInvocationHandler(mockGL));
    }

    @Override
    public int glGenTexture() {
        return ++textureId;
    }

    @Override
    public int glCreateShader(int type) {
        return ++shaderId;
    }

    @Override
    public int glCreateProgram() {
        return ++programId;
    }

    @Override
    public void glGetShaderiv(int shader, int pname, IntBuffer params) {
        switch (pname) {
        case GL_COMPILE_STATUS:
            params.put(0, 1);
            return;
        default:
            super.glGetShaderiv(shader, pname, params);
        }
    }

    @Override
    public void glGetProgramiv(int program, int pname, IntBuffer params) {
        switch (pname) {
        case GL_LINK_STATUS:
            params.put(0, 1);
            return;
        case GL_ACTIVE_UNIFORMS:
        case GL_ACTIVE_ATTRIBUTES:
            params.put(0, 0);
            return;
        default:
            super.glGetProgramiv(program, pname, params);
        }
    }

    @Override
    public int glGetUniformLocation(int program, String name) {
        // Return a positive integer (try to avoid multiple params returning the same location)
        return name.hashCode() & 0x7FFFFFFF;
    }

    @Override
    public int glGetError() {
        return GL20.GL_NO_ERROR;
    }

    private static final class MockGLInvocationHandler implements InvocationHandler {
        private final MockGL mockGL;

        private MockGLInvocationHandler(MockGL mockGL) {
            this.mockGL = mockGL;
        }

        @Override
        public @Nullable Object invoke(Object proxy, Method method, Object[] args) throws Exception {
            try {
                // Call equivalent method in MockGL if it exists
                Method mockMethod = MockGL.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
                return mockMethod.invoke(mockGL, args);
            } catch (NoSuchMethodException nsme) {
                return null;
            }
        }
    }
}
