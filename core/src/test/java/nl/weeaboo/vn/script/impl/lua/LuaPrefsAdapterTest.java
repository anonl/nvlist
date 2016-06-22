package nl.weeaboo.vn.script.impl.lua;

import static nl.weeaboo.settings.Preference.newPreference;

import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

import nl.weeaboo.settings.Preference;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.test.integration.LuaIntegrationTest;

public class LuaPrefsAdapterTest extends LuaIntegrationTest {

    @Test
    public void testGetDeclaredPrefs() {
        List<Preference<?>> declared = LuaPrefsAdapter.getDeclaredPrefs(PrefsHolder.class);
        Assert.assertEquals(PrefsHolder.getAllPrefs(), ImmutableSet.copyOf(declared));
    }

    @Test
    public void prefsAccessFromLua() {
        loadScript("prefs-getter.lvn");

        LuaTestUtil.assertGlobal("engineMinVersion", NovelPrefs.ENGINE_MIN_VERSION.getDefaultValue());
    }

    public static class PrefsHolder {

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

}
