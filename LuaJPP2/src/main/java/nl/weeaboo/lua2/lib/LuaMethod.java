package nl.weeaboo.lua2.lib;

import java.io.ObjectStreamException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import nl.weeaboo.lua2.io.IReadResolveSerializable;
import nl.weeaboo.lua2.io.IWriteReplaceSerializable;
import nl.weeaboo.lua2.io.LuaSerializable;

@LuaSerializable
final class LuaMethod extends VarArgFunction implements IWriteReplaceSerializable {

	private final ClassInfo classInfo;
	private final LuaValue methodName;

	private transient MethodInfo[] methodInfos;
	private transient Object[] javaArgsTemp;

	public LuaMethod(ClassInfo c, LuaValue nm, MethodInfo[] mis) {
		classInfo = c;
		methodName = nm;
		methodInfos = mis;
	}

	//Functions
	@Override
    public Object writeReplace() throws ObjectStreamException {
		return new LuaMethodRef(classInfo, methodName);
	}

	@Override
	public String toString() {
		return classInfo.getWrappedClass().getName() + "." + methodName + "()";
	}

	@Override
	public LuaValue call() {
		return error("Method cannot be called without instance");
	}

	@Override
	public LuaValue call(LuaValue arg) {
		return invokeMethod(arg.checkuserdata(), NONE);
	}

	@Override
	public LuaValue call(LuaValue arg1, LuaValue arg2) {
		return invokeMethod(arg1.checkuserdata(), arg2);
	}

	@Override
	public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
		return invokeMethod(arg1.checkuserdata(), varargsOf(arg2, arg3));
	}

	@Override
	public Varargs invoke(Varargs args) {
		return invokeMethod(args.checkuserdata(1), args.subargs(2));
	}

	private LuaValue invokeMethod(Object instance, Varargs args) {
		try {
			MethodInfo method = findMethod(args);
			if (method == null) {
				throw new NoSuchMethodException(String.format("Method %s with the specified parameter types doesn't exist", methodName));
			}
			return CoerceJavaToLua.coerce(invokeMethod(method, instance, args));
		} catch (InvocationTargetException ite) {
			Throwable cause = ite.getCause();
			String msg = "Error in invoked Java method: " + methodName + "(" + args + ")";
			if (cause != null) {
				msg += " :: " + cause;
			}
			throw new LuaError(msg, cause);
		} catch (Exception e) {
			throw new LuaError("Error invoking Java method: " + methodName + "(" + args + ")", e);
		}
	}

	private Object invokeMethod(MethodInfo mi, Object instance, Varargs args)
		throws IllegalArgumentException, IllegalAccessException, InvocationTargetException
	{
		Class<?>[] params = mi.getParams();
		if (javaArgsTemp == null || javaArgsTemp.length != params.length) {
			//Length must be exactly the same
			javaArgsTemp = new Object[params.length];
		}

		try {
			Method method = mi.getMethod();
			CoerceLuaToJava.coerceArgs(javaArgsTemp, args, params);
			return method.invoke(instance, javaArgsTemp);
		} finally {
			//Required to allow GC
			for (int n = 0; n < javaArgsTemp.length; n++) {
				javaArgsTemp[n] = null;
			}
		}
	}

	protected MethodInfo findMethod(Varargs args) {
		MethodInfo[] methods = getMethodInfos();
		if (methods.length == 1) {
			return methods[0];
		}

		MethodInfo bestMatch = null;
		int bestScore = Integer.MAX_VALUE;
		for (int n = 0; n < methods.length; n++) {
			Class<?>[] params = methods[n].getParams();
			int score = CoerceLuaToJava.scoreParamTypes(args, params);
			if (score == 0) {
				return methods[n]; //Perfect match, return at once
			} else {
				//System.out.println(methods[n].getMethod().getName() + " " + len + "," + params.length + " " + score);
				if (score < bestScore) {
					bestScore = score;
					bestMatch = methods[n];
				}
			}
		}

		return bestMatch;
	}

	//Getters
	protected MethodInfo[] getMethodInfos() {
		if (methodInfos == null) {
			methodInfos = classInfo.getMethods(methodName);
		}
		return methodInfos;
	}

	//Setters

	//Inner Classes
	@LuaSerializable
    private static class LuaMethodRef implements IReadResolveSerializable {

		private static final long serialVersionUID = 1L;

		private final ClassInfo classInfo;
		private final LuaValue name;

		public LuaMethodRef(ClassInfo classInfo, LuaValue name) {
			this.classInfo = classInfo;
			this.name = name;
		}

		@Override
        public Object readResolve() throws ObjectStreamException {
			return classInfo.getMetatable().getMethod(name);
		}
	}

}