package nl.weeaboo.lua2.lib;

import static org.luaj.vm2.LuaValue.valueOf;

import java.io.ObjectStreamException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import nl.weeaboo.lua2.io.IReadResolveSerializable;
import nl.weeaboo.lua2.io.IWriteReplaceSerializable;
import nl.weeaboo.lua2.io.LuaSerializable;

@LuaSerializable
public final class ClassInfo implements IWriteReplaceSerializable {

	private static final Comparator<Method> methodSorter = new Comparator<Method>() {
		Collator c = Collator.getInstance(Locale.US);

        @Override
        public int compare(Method m1, Method m2) {
			return c.compare(m1.getName(), m2.getName());
		}
	};

	private final Class<?> clazz;
	private final boolean isArray;

	private transient ClassMetaTable metaTable;

	private transient ConstructorInfo[] constrs;
	private transient Map<LuaString, Field> fields;
	private transient Map<LuaString, MethodInfo[]> methods;

	public ClassInfo(Class<?> c) {
		clazz = c;
		isArray = c.isArray();
	}

	//Functions
    @Override
    public Object writeReplace() throws ObjectStreamException {
		return new ClassInfoRef(clazz);
	}

	public Object newInstance(Varargs luaArgs)
		throws IllegalArgumentException, InstantiationException,
				IllegalAccessException, InvocationTargetException
	{
		ConstructorInfo constr = findConstructor(luaArgs);
		if (constr == null) {
			throw new LuaError(String.format("No suitable constructor found for: %s\n", clazz.getName()));
		}

		Class<?>[] params = constr.getParams();
		Object[] javaArgs = new Object[params.length];
		CoerceLuaToJava.coerceArgs(javaArgs, luaArgs, params);
		return constr.getConstructor().newInstance(javaArgs);
	}

	@Override
	public int hashCode() {
		return clazz.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ClassInfo) {
			ClassInfo ci = (ClassInfo)obj;
			return clazz.equals(ci.clazz);
		}
		return false;
	}

	//Getters
	public Class<?> getWrappedClass() {
		return clazz;
	}

	public boolean isArray() {
		return isArray;
	}

	protected ConstructorInfo findConstructor(Varargs luaArgs) {
		ConstructorInfo bestMatch = null;
		int bestScore = Integer.MAX_VALUE;

		ConstructorInfo constrs[] = getConstructors();
		for (int n = 0; n < constrs.length; n++) {
			int score = CoerceLuaToJava.scoreParamTypes(luaArgs, constrs[n].getParams());
			if (score == 0) {
				return constrs[n]; //Perfect match, return at once
			} else if (score < bestScore) {
				bestScore = score;
				bestMatch = constrs[n];
			}
		}

		return bestMatch;
	}

	public ConstructorInfo[] getConstructors() {
		if (constrs == null) {
			Constructor<?> cs[] = clazz.getConstructors();

			constrs = new ConstructorInfo[cs.length];
			for (int n = 0; n < cs.length; n++) {
				constrs[n] = new ConstructorInfo(n, cs[n]);
			}
		}
		return constrs;
	}

	public ClassMetaTable getMetatable() {
		if (metaTable == null) {
			metaTable = new ClassMetaTable(this);
		}
		return metaTable;
	}

	public Field getField(LuaValue name) {
		if (fields == null) {
			fields = new HashMap<LuaString, Field>();
			for (Field f : clazz.getFields()) {
				fields.put(valueOf(f.getName()), f);
			}
		}
		return fields.get(name);
	}

	public MethodInfo[] getMethods(LuaValue name) {
		if (methods == null) {
			Method marr[] = clazz.getMethods();
			Arrays.sort(marr, methodSorter);

			methods = new HashMap<LuaString, MethodInfo[]>();

			String curName = null;
			List<MethodInfo> list = new ArrayList<MethodInfo>();
			for (Method m : marr) {
				if (!m.getName().equals(curName)) {
					if (curName != null) {
						methods.put(valueOf(curName), list.toArray(new MethodInfo[list.size()]));
					}
					curName = m.getName();
					list.clear();
				}
				list.add(new MethodInfo(m));
			}

			if (curName != null) {
				methods.put(LuaString.valueOf(curName), list.toArray(new MethodInfo[list.size()]));
			}
		}
		return methods.get(name);
	}

	public boolean hasMethod(LuaString name) {
		return getMethods(name) != null;
	}

	//Inner Classes
	@LuaSerializable
    private static class ClassInfoRef implements IReadResolveSerializable {

		private static final long serialVersionUID = 1L;

		private final Class<?> clazz;

		public ClassInfoRef(Class<?> clazz) {
			this.clazz = clazz;
		}

		@Override
        public Object readResolve() throws ObjectStreamException {
			return LuajavaLib.getClassInfo(clazz);
		}
	}


}
