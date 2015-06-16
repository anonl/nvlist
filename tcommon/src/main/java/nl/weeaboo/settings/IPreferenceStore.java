package nl.weeaboo.settings;

import java.io.IOException;
import java.util.Map;

public interface IPreferenceStore {

	public void addPreferenceListener(IPreferenceListener l);
	public void removePreferenceListener(IPreferenceListener l);
	
	public void init(Map<String, String> userOverrides) throws IOException;
	public void loadVariables() throws IOException;
	public void saveVariables() throws IOException;
	
	public <T> T get(Preference<T> pref);
	public <T, V extends T> void set(Preference<T> pref, V value);
	
}
