package nl.weeaboo.settings;

public interface IPreferenceListener {

	public <T> void onPreferenceChanged(Preference<T> prop, T oldValue, T newValue);
	
}
