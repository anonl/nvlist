package nl.weeaboo.lua2.lib;

import java.io.ObjectStreamException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

import nl.weeaboo.lua2.io.IReadResolveSerializable;
import nl.weeaboo.lua2.io.IWriteReplaceSerializable;
import nl.weeaboo.lua2.io.LuaSerializable;

@LuaSerializable
final class ClassMetaTable extends LuaTable implements IWriteReplaceSerializable {

	private static final LuaString LENGTH = valueOf("length");
	private static final LuaValue ARRAY_LENGTH_FUNCTION = new ArrayLengthFunction();

	//--- Uses manual serialization, don't add variables ---
	private ClassInfo classInfo;
	private boolean seal;
	private transient Map<LuaValue, LuaMethod> methods;
	//--- Uses manual serialization, don't add variables ---

	ClassMetaTable(ClassInfo ci) {
		classInfo = ci;

		rawset(INDEX, newMetaFunction(classInfo, this, true));
		rawset(NEWINDEX, newMetaFunction(classInfo, this, false));
		if (ci.isArray()) {
			rawset(LEN, ARRAY_LENGTH_FUNCTION);
		}

		seal = true;

		methods = new HashMap<LuaValue, LuaMethod>();
	}

    @Override
    public Object writeReplace() throws ObjectStreamException {
		return new ClassMetaTableRef(classInfo);
	}

	private static MetaFunction newMetaFunction(ClassInfo ci, ClassMetaTable mt, boolean isGet) {
		if (ci.isArray()) {
			return new ArrayMetaFunction(ci, mt, isGet);
		}
		return new MetaFunction(ci, mt, isGet);
	}

	@Override
	protected void hashClearSlot(int i) {
		checkSeal();
		super.hashClearSlot(i);
	}

	@Override
	public void hashset(LuaValue key, LuaValue value) {
		checkSeal();
		super.hashset(key, value);
	}

	@Override
	public void rawset(int key, LuaValue value) {
		checkSeal();
		super.rawset(key, value);
	}
	@Override
	public void rawset(LuaValue key, LuaValue value) {
		checkSeal();
		super.rawset(key, value);
	}

	protected void checkSeal() {
		if (seal) {
			throw new LuaError("Can't write to a shared Java class metatable");
		}
	}

	@Override
	public String tojstring() {
		return "ClassMetaTable(" + classInfo.getWrappedClass().getSimpleName() + ")@" + hashCode();
	}

    LuaMethod getMethod(LuaValue name) {
		LuaMethod method = methods.get(name);
		if (method != null) {
			return method;
		} else {
			MethodInfo[] ms = classInfo.getMethods(name);
            if (ms != null && ms.length > 0) {
				method = new LuaMethod(classInfo, name, ms);
				methods.put(name, method);
				return method;
			}
		}
		return null;
	}

	//Inner Classes
	@LuaSerializable
    private static class ClassMetaTableRef implements IReadResolveSerializable {

		private static final long serialVersionUID = 1L;

		private final ClassInfo classInfo;

		public ClassMetaTableRef(ClassInfo classInfo) {
			this.classInfo = classInfo;
		}

        @Override
        public Object readResolve() throws ObjectStreamException {
			return classInfo.getMetatable();
		}
	}

	@LuaSerializable
    private static class MetaFunction extends VarArgFunction {

		private static final long serialVersionUID = 1L;

		protected final ClassInfo classInfo;
		protected final ClassMetaTable meta;
		protected final boolean isGet;

		public MetaFunction(ClassInfo ci, ClassMetaTable mt, boolean get) {
			classInfo = ci;
			meta = mt;
			isGet = get;
		}

		@Override
		public LuaValue call() {
			return error("Method cannot be called without instance");
		}

		@Override
		public LuaValue call(LuaValue arg) {
			return invokeMethod(arg.checkuserdata(), NIL, NIL);
		}

		@Override
		public LuaValue call(LuaValue arg1, LuaValue arg2) {
			return invokeMethod(arg1.checkuserdata(), arg2, NIL);
		}

		@Override
		public LuaValue call(LuaValue arg1, LuaValue arg2, LuaValue arg3) {
			return invokeMethod(arg1.checkuserdata(), arg2, arg3);
		}

		@Override
		public Varargs invoke(Varargs args) {
			return invokeMethod(args.arg1().checkuserdata(), args.arg(2), args.arg(3));
		}

		protected LuaValue invokeMethod(Object instance, LuaValue key, LuaValue val) {
			//Fields & Methods
			if (isGet) {
				LuaMethod method = meta.getMethod(key);
				if (method != null) {
					return method;
				}

				Field field = classInfo.getField(key);
				if (field != null) {
					try {
						Object o = field.get(instance);
						return CoerceJavaToLua.coerce(o);
					} catch (Exception e) {
						throw new LuaError("Error coercing field (" + key + ")", e);
					}
				}

				return NIL; //Invalid get returns nil
			} else {
				Field field = classInfo.getField(key);
				if (field != null) {
					Object v = CoerceLuaToJava.coerceArg(val, field.getType());
					try {
						field.set(instance, v);
					} catch (Exception e) {
						throw new LuaError("Error setting field: " + classInfo.getWrappedClass() + "." + key, e);
					}
					return NIL;
				} else {
					throw new LuaError("Invalid assignment, field does not exist in Java class: " + key);
				}
			}
		}

	}

	@LuaSerializable
	private static class ArrayMetaFunction extends MetaFunction {

		private static final long serialVersionUID = 1L;

		public ArrayMetaFunction(ClassInfo ci, ClassMetaTable mt, boolean get) {
			super(ci, mt, get);
		}

		@Override
		protected LuaValue invokeMethod(Object instance, LuaValue key, LuaValue val) {
			if (key.isinttype()) {
				int index = key.checkint() - 1;
				if (index < 0 || index >= Array.getLength(instance)) {
					throw new LuaError(new ArrayIndexOutOfBoundsException(index));
				}

				if (isGet) {
					return CoerceJavaToLua.coerce(Array.get(instance, index));
				} else {
					Class<?> clazz = classInfo.getWrappedClass();
					Object v = CoerceLuaToJava.coerceArg(val, clazz.getComponentType());
					Array.set(instance, key.checkint() - 1, v);
					return NIL;
				}
			} else if (key.equals(LENGTH)) {
				if (isGet) {
					return valueOf(Array.getLength(instance));
				}
			}

			return super.invokeMethod(instance, key, val);
		}

	}

	@LuaSerializable
	private static class ArrayLengthFunction extends OneArgFunction {

		private static final long serialVersionUID = 1L;

		@Override
		public LuaValue call(LuaValue arg) {
			Object instance = arg.checkuserdata();
			return valueOf(Array.getLength(instance));
		}

	}

}