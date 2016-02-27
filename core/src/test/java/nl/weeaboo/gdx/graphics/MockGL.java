package nl.weeaboo.gdx.graphics;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.IntBuffer;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.profiling.GL20Profiler;

public class MockGL extends GL20Profiler {

    private int textureId;
    private int shaderId;
    private int programId;

    private MockGL() {
        super(null);
    }

    public static GL20 newInstance() {
        final MockGL mockGL = new MockGL();
        return (GL20)Proxy.newProxyInstance(MockGL.class.getClassLoader(),
            new Class<?>[] { GL20.class },
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
                    try {
                        // Call equivalent method in MockGL if it exists
                        Method mockMethod = MockGL.class.getDeclaredMethod(method.getName(), method.getParameterTypes());
                        return mockMethod.invoke(mockGL, args);
                    } catch (NoSuchMethodException nsme) {
                        return null;
                    }
                }
            });
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
        }

        super.glGetShaderiv(shader, pname, params);
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
        }

        super.glGetProgramiv(program, pname, params);
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

}
