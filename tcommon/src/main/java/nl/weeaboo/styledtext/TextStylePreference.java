package nl.weeaboo.styledtext;

import nl.weeaboo.settings.Preference;

public class TextStylePreference extends Preference<TextStyle> {
	
	protected TextStylePreference(String key, TextStyle defaultVal, boolean c, String name, String desc) {
		super(key, name, TextStyle.class, defaultVal, c, desc);
	}

	public static Preference<TextStyle> newPreference(String key, TextStyle defaultVal, String name, String desc) {
		return new TextStylePreference(key, defaultVal, false, name, desc);
	}
	public static Preference<TextStyle> newConstPreference(String key, TextStyle defaultVal, String name, String desc) {
		return new TextStylePreference(key, defaultVal, true, name, desc);
	}
	
	@Override
	public TextStyle fromString(String string) {
		return TextStyle.fromString(string);
	}

	@Override
	public String toString(TextStyle value) {
		return (value != null ? value.toString() : "");
	}

}
