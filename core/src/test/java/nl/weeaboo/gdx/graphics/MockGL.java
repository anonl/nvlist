package nl.weeaboo.gdx.graphics;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.profiling.GL20Profiler;

public class MockGL extends GL20Profiler {

    private int textureId;

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
    public int glGetError() {
        return GL20.GL_NO_ERROR;
    }

}
