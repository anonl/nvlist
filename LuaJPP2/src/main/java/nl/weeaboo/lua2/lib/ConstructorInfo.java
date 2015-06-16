package nl.weeaboo.lua2.lib;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;

import nl.weeaboo.lua2.io.LuaSerializable;

@LuaSerializable
final class ConstructorInfo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int constrIndex;
	private Constructor<?> constr;
	
	private transient Class<?> params[];
	
	public ConstructorInfo(int index, Constructor<?> c) {
		constrIndex = index;
		constr = c;
	}
	
	//Functions
	private void readObject(ObjectInputStream in) throws ClassNotFoundException, IOException {
		Class<?> clazz = (Class<?>)in.readObject();
		constrIndex = in.readInt();
		
		Constructor<?> constrs[] = clazz.getConstructors();
		if (constrIndex < 0 || constrIndex >= constrs.length) {
			throw new IOException("Class format changed, constructor index " + constrIndex + " not found.");
		}
		
		constr = constrs[constrIndex];
	}

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeObject(constr.getDeclaringClass());
		out.writeInt(constrIndex);
	}
	
	@Override
	public int hashCode() {
		return constr.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ConstructorInfo) {
			ConstructorInfo ci = (ConstructorInfo)obj;
			return constr.equals(ci.constr);
		}
		return false;
	}
	
	//Getters
	public Constructor<?> getConstructor() {
		return constr;
	}
	public Class<?>[] getParams() {
		if (params == null) {
			params = constr.getParameterTypes();
		}
		return params;
	}
	
	//Setters
	
}