package nl.weeaboo.lua2.lib;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;

import nl.weeaboo.lua2.io.LuaSerializable;

@LuaSerializable
final class MethodInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Method method;
	
	private transient Class<?> params[];
	
	public MethodInfo(Method m) {
		method = m;
	}
	
	//Functions
	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
		Class<?> clazz = (Class<?>)in.readObject();
		String methodName = in.readUTF();
		params = (Class<?>[])in.readObject();

		try {
			method = clazz.getMethod(methodName, params);
		} catch (SecurityException e) {
			//Too bad :(
		} catch (NoSuchMethodException e) {
			method = null;
		}
		
		if (method == null) {
			throw new IOException("Class format changed, method " + methodName + "(" + Arrays.toString(params) + ") could not be found.");
		}
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(method.getDeclaringClass());
		out.writeUTF(method.getName());
		out.writeObject(params != null ? params : method.getParameterTypes());
	}
	
	@Override
	public int hashCode() {
		return method.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MethodInfo) {
			MethodInfo ci = (MethodInfo)obj;
			return method.equals(ci.method);
		}
		return false;
	}
	
	//Getters
	public Method getMethod() {
		return method;
	}
	public Class<?>[] getParams() {
		if (params == null) {
			params = method.getParameterTypes();
		}
		return params;
	}
	
	//Setters
	
}