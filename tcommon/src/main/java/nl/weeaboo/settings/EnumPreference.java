package nl.weeaboo.settings;

class EnumPreference<E extends Enum<E>> extends Preference<E> {

	public EnumPreference(String key, Class<E> type, E defaultVal, boolean c, String name, String desc) {
		super(key, name, type, defaultVal, c, desc);
	}

	@Override
	public E fromString(String string) {
		if (string == null) {
			return getDefaultValue();
		}
		
		Class<E> type = getType();
		return Enum.valueOf(type, string);
	}
	
	@Override
	public String toString(E value) {
		return value.name();
	}
	
}
