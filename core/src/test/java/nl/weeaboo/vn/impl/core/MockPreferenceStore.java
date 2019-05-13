package nl.weeaboo.vn.impl.core;

import java.util.HashMap;
import java.util.Map;

import nl.weeaboo.prefsstore.IPreferenceListener;
import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.prefsstore.Preference;

public final class MockPreferenceStore implements IPreferenceStore {

    private final Map<String, String> values = new HashMap<>();

    @Override
    public void addPreferenceListener(IPreferenceListener l) {
    }

    @Override
    public void removePreferenceListener(IPreferenceListener l) {
    }

    @Override
    public void loadVariables() {
    }

    @Override
    public void saveVariables() {
    }

    @Override
    public <T> T get(Preference<T> pref) {
        String raw = values.get(pref.getKey());
        if (raw == null) {
            return pref.getDefaultValue();
        }

        T val = pref.fromString(raw);
        if (pref.isValidValue(val)) {
            return val;
        } else {
            return pref.getDefaultValue();
        }
    }

    @Override
    public <T, V extends T> void set(Preference<T> pref, V value) {
        values.put(pref.getKey(), pref.toString(value));
    }

}
