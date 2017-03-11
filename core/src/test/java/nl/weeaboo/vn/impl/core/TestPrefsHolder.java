package nl.weeaboo.vn.impl.core;

import static nl.weeaboo.prefsstore.Preference.newPreference;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

import nl.weeaboo.prefsstore.Preference;

public class TestPrefsHolder {

    public static final Preference<String> strPref = newPreference("str", "str", "value", "");
    public static final Preference<Integer> intPref = newPreference("int", "int", 111, "");

    // Non-static fields are ignored
    public final Preference<Integer> instanceField = newPreference("instance", "instance", 222, "");

    // Non-public fields are ignored
    @SuppressWarnings("unused")
    private static final Preference<Integer> privateField = newPreference("private", "private", 333, "");

    static Set<Preference<?>> getAllPrefs() {
        return ImmutableSet.<Preference<?>>of(strPref, intPref);
    }

}
