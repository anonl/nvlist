/*******************************************************************************
 * Copyright (c) 2009 Luaj.org. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package nl.weeaboo.lua2.lib;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaUserdata;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.PackageLib;
import org.luaj.vm2.lib.VarArgFunction;

import nl.weeaboo.lua2.io.LuaSerializable;

@LuaSerializable
public class LuajavaLib extends VarArgFunction {

	private static final long serialVersionUID = 1089400343490554068L;

	static final int INIT = 0;
	static final int BINDCLASS = 1;
	static final int NEWINSTANCE = 2;
	static final int NEW = 3;
	static final int CREATEPROXY = 4;
	static final int LOADLIB = 5;

	static final String[] NAMES = { "bindClass", "newInstance", "new", "createProxy", "loadLib", };

	static final int METHOD_MODIFIERS_VARARGS = 0x80;

	private static final Map<Class<?>, ClassInfo> classInfoMap = new HashMap<Class<?>, ClassInfo>();

	public LuajavaLib() {
	}

	@Override
	public Varargs invoke(Varargs args) {
		try {
			switch (opcode) {
			case INIT: {
				LuaTable t = new LuaTable();
				bind(t, LuajavaLib.class, NAMES, BINDCLASS);
				env.set("luajava", t);
				PackageLib.getCurrent().LOADED.set("luajava", t);
				return t;
			}
			case BINDCLASS: {
				final Class<?> clazz = classForName(args.checkjstring(1));
				return toUserdata(clazz, clazz);
			}
			case NEWINSTANCE:
			case NEW: {
				final LuaValue c = args.checkvalue(1);
				final Class<?> clazz;
				if (c.isuserdata(Class.class)) {
                    clazz = c.checkuserdata(Class.class);
				} else {
					clazz = classForName(c.tojstring());
				}
				ClassInfo info = getClassInfo(clazz);
				Object javaObject = info.newInstance(args.subargs(2));
				return LuaUserdata.userdataOf(javaObject, info.getMetatable());
			}
			case CREATEPROXY: {
				final int niface = args.narg() - 1;
				if (niface <= 0) throw new LuaError("no interfaces");
				final LuaValue lobj = args.checktable(niface + 1);

				// get the interfaces
				final Class<?>[] ifaces = new Class<?>[niface];
				for (int i = 0; i < niface; i++) {
					ifaces[i] = classForName(args.checkjstring(i + 1));
				}

				// create the invocation handler
				InvocationHandler handler = new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						String name = method.getName();
						LuaValue func = lobj.get(name);
                        if (func.isnil()) {
                            return null;
                        }

						boolean isvarargs = ((method.getModifiers() & METHOD_MODIFIERS_VARARGS) != 0);

						LuaValue[] v;
                        if (args == null) {
                            v = new LuaValue[0];
						} else {
                            int n = args.length;
                            if (isvarargs) {
                                Object o = args[--n];
                                int m = Array.getLength(o);
                                v = new LuaValue[n + m];
                                for (int i = 0; i < n; i++)
                                    v[i] = CoerceJavaToLua.coerce(args[i]);
                                for (int i = 0; i < m; i++)
                                    v[i + n] = CoerceJavaToLua.coerce(Array.get(o, i));
                            } else {
                                v = new LuaValue[n];
                                for (int i = 0; i < n; i++)
                                    v[i] = CoerceJavaToLua.coerce(args[i]);
                            }
						}
						LuaValue result = func.invoke(v).arg1();
						return CoerceLuaToJava.coerceArg(result, method.getReturnType());
					}
				};

				// create the proxy object
				Object proxy = Proxy.newProxyInstance(getClass().getClassLoader(), ifaces, handler);

				// return the proxy
				return LuaValue.userdataOf(proxy);
			}
			case LOADLIB: {
				// get constructor
				String classname = args.checkjstring(1);
				String methodname = args.checkjstring(2);
				Class<?> clazz = classForName(classname);
				Method method = clazz.getMethod(methodname, new Class[] {});
				Object result = method.invoke(clazz, new Object[] {});
				if (result instanceof LuaValue) {
					return (LuaValue) result;
				} else {
					return NIL;
				}
			}
			default:
				throw new LuaError("not yet supported: " + this);
			}
		} catch (LuaError e) {
			throw e;
		} catch (InvocationTargetException ite) {
			throw new LuaError("Error invoking LuajavaLib." + NAMES[opcode], ite.getCause());
		} catch (Exception e) {
			throw new LuaError("Error invoking LuajavaLib." + NAMES[opcode], e);
		}
	}

	static Class<?> classForName(String name) throws ClassNotFoundException {
		return Class.forName(name);
	}

	public static LuaUserdata toUserdata(Object obj, Class<?> clazz) {
		ClassInfo info = getClassInfo(clazz);
		return LuaUserdata.userdataOf(obj, info.getMetatable());
	}

	public static ClassInfo getClassInfo(Class<?> clazz) {
		ClassInfo info = classInfoMap.get(clazz);
		if (info == null) {
			info = new ClassInfo(clazz);
			classInfoMap.put(clazz, info);
		}
		return info;
	}

	@LuaSerializable
    public static class ConstrFunction extends LuaFunction {

		private static final long serialVersionUID = 6459092255782515933L;

		private ClassInfo ci;

		public ConstrFunction(Class<?> c) {
			ci = getClassInfo(c);
		}

		@Override
		public Varargs invoke(Varargs args) {
			try {
				Object javaObject = ci.newInstance(args);
				return LuaUserdata.userdataOf(javaObject, ci.getMetatable());
			} catch (InvocationTargetException ite) {
				throw new LuaError("Error invoking constructor: " + ci.getWrappedClass(), ite.getCause());
			} catch (Exception e) {
				throw new LuaError("Error invoking constructor: " + ci.getWrappedClass(), e);
			}
		}
	}

}