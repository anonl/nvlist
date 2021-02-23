package nl.weeaboo.vn.impl.core;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import nl.weeaboo.prefsstore.IPreferenceListener;
import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.prefsstore.Preference;

public final class PreferenceStoreMock implements IPreferenceStore {

    private final CopyOnWriteArrayList<IPreferenceListener> listeners = new CopyOnWriteArrayList<>();
    private final Map<String, String> values = new HashMap<>();

    @Override
    public void addPreferenceListener(IPreferenceListener ls) {
        listeners.add(ls);
    }

    @Override
    public void removePreferenceListener(IPreferenceListener ls) {
        listeners.remove(ls);
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
    public <T, V extends T> void set(Preference<T> pref, V newValue) {
        T oldValue = pref.fromString(values.put(pref.getKey(), pref.toString(newValue)));
        listeners.forEach(ls -> ls.onPreferenceChanged(pref, oldValue, newValue));
    }

}
