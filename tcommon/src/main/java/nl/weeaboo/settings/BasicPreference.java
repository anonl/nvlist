package nl.weeaboo.settings;

class BasicPreference<T> extends Preference<T> {

	public BasicPreference(String key, Class<T> type, T defaultVal, boolean c, String name, String desc) {
		super(key, name, type, defaultVal, c, desc);
		
		if (!isBasicType(type)) {
			throw new IllegalArgumentException("Can't create BasicProperty of non-basic type: " + type);
		}
	}

	@Override
	public T fromString(String string) {
		if (string == null) {
			return getDefaultValue();
		}
		
		Class<T> type = getType();
		try {
			if (type == Boolean.class) {
				return type.cast(Boolean.parseBoolean(string));
			} else if (type == Byte.class) {
				return type.cast(Byte.parseByte(string));
			} else if (type == Short.class) {
				return type.cast(Short.parseShort(string));
			} else if (type == Integer.class) {
				return type.cast(Integer.parseInt(string));
			} else if (type == Long.class) {
				return type.cast(Long.parseLong(string));
			} else if (type == Float.class) {
				return type.cast(Float.parseFloat(string));
			} else if (type == Double.class) {
				return type.cast(Double.parseDouble(string));
			}
		} catch (NumberFormatException nfe) {
			
		}
		
		if (type == String.class) {
			return type.cast(string);
		}
		
		return getDefaultValue();
	}
	
	@Override
	public String toString(T value) {
		return value.toString();
	}

	public static boolean isBasicType(Class<?> c) {
		return c == Boolean.class || c == Byte.class || c == Short.class || c == Integer.class
			|| c == Long.class || c == Float.class || c == Double.class || c == String.class;
	}
	
}
