package nl.weeaboo.settings;

public interface IPreferenceListener {

    public <T> void onPreferenceChanged(Preference<T> pref, T oldValue, T newValue);

}
