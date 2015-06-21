package nl.weeaboo.settings;

public abstract class Preference<T> {

	private final String key;
    private final String name;
	private final Class<T> type;
	private final T defaultValue;
	private final boolean constant;
	private final String description;

	public Preference(String key, String name, Class<T> type, T defaultVal, boolean c, String description) {
		this.key = key;
        this.name = name;
		this.type = type;
		this.defaultValue = defaultVal;
		this.constant = c;
		this.description = description;
	}

	//Factory functions (regular)
	public static Preference<Boolean> newPreference(String key, String name, boolean defaultVal, String desc) {
		return new BasicPreference<Boolean>(key, Boolean.class, defaultVal, false, name, desc);
	}
	public static Preference<Integer> newPreference(String key, String name, int defaultVal, String desc) {
		return new BasicPreference<Integer>(key, Integer.class, defaultVal, false, name, desc);
	}
	public static Preference<Double> newPreference(String key, String name, double defaultVal, String desc) {
		return new BasicPreference<Double>(key, Double.class, defaultVal, false, name, desc);
	}
	public static Preference<String> newPreference(String key, String name, String defaultVal, String desc) {
		return new BasicPreference<String>(key, String.class, defaultVal, false, name, desc);
	}
	public static <E extends Enum<E>> Preference<E> newPreference(String key, String name, Class<E> clazz, E defaultVal, String desc) {
		return new EnumPreference<E>(key, clazz, defaultVal, false, name, desc);
	}

	//Factory functions (const)
	public static Preference<Boolean> newConstPreference(String key, String name, boolean defaultVal, String desc) {
		return new BasicPreference<Boolean>(key, Boolean.class, defaultVal, true, name, desc);
	}
	public static Preference<Integer> newConstPreference(String key, String name, int defaultVal, String desc) {
		return new BasicPreference<Integer>(key, Integer.class, defaultVal, true, name, desc);
	}
	public static Preference<Double> newConstPreference(String key, String name, double defaultVal, String desc) {
		return new BasicPreference<Double>(key, Double.class, defaultVal, true, name, desc);
	}
	public static Preference<String> newConstPreference(String key, String name, String defaultVal, String desc) {
		return new BasicPreference<String>(key, String.class, defaultVal, true, name, desc);
	}
	public static <E extends Enum<E>> Preference<E> newConstPreference(String key, String name, Class<E> clazz, E defaultVal, String desc) {
		return new EnumPreference<E>(key, clazz, defaultVal, true, name, desc);
	}

	//Functions
	public abstract T fromString(String string);
	public abstract String toString(T value);

	//Getters
	public String getKey() {
		return key;
	}
	public Class<T> getType() {
		return type;
	}
	public T getDefaultValue() {
		return defaultValue;
	}
	public boolean isConstant() {
		return constant;
	}
	public String getName() {
		return name;
	}
	public String getDescription() {
		return description;
	}

	/**
	 * @param value The value to check.
	 */
	public boolean isValidValue(T value) {
		return true;
	}

	//Setters

}
