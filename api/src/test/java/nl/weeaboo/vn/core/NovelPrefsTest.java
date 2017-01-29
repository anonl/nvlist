package nl.weeaboo.vn.core;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import nl.weeaboo.reflect.ReflectUtil;
import nl.weeaboo.prefsstore.Preference;

public class NovelPrefsTest {

    private Map<String, Preference<?>> prefs;

    @Before
    public void before() throws IllegalAccessException {
        // Get the declared preferences and map them by key
        prefs = new HashMap<>();
        for (Preference<?> pref : ReflectUtil.getConstants(NovelPrefs.class, Preference.class).values()) {
            prefs.put(pref.getKey(), pref);
        }
    }

    /** Check the default values of important prefs so they don't change accidentally between releases. */
    @Test
    public void checkDefaults() {
        assertDefault("width", 1280);
        assertDefault("height", 720);

        assertDefault("vn.engineMinVersion", "4.0");
        assertDefault("vn.engineTargetVersion", "4.0");

        assertDefault("vn.textSpeed", 0.5);
        assertDefault("vn.textLog.pageLimit", 50);
    }

    private void assertDefault(String prefId, Object expectedValue) {
        Preference<?> pref = prefs.get(prefId);
        Assert.assertNotNull("Pref doesn't exist: " + prefId, pref);
        Assert.assertEquals(expectedValue, pref.getDefaultValue());
    }

}
