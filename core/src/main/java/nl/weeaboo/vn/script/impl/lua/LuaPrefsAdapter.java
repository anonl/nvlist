package nl.weeaboo.vn.script.impl.lua;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import nl.weeaboo.lua2.io.LuaSerializable;
import nl.weeaboo.lua2.lib.VarArgFunction;
import nl.weeaboo.lua2.luajava.CoerceJavaToLua;
import nl.weeaboo.lua2.luajava.CoerceLuaToJava;
import nl.weeaboo.lua2.vm.LuaConstants;
import nl.weeaboo.lua2.vm.LuaNil;
import nl.weeaboo.lua2.vm.LuaTable;
import nl.weeaboo.lua2.vm.LuaValue;
import nl.weeaboo.lua2.vm.Varargs;
import nl.weeaboo.reflect.ReflectUtil;
import nl.weeaboo.settings.IPreferenceStore;
import nl.weeaboo.settings.Preference;
import nl.weeaboo.vn.core.impl.StaticEnvironment;
import nl.weeaboo.vn.core.impl.StaticRef;

public class LuaPrefsAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(LuaPrefsAdapter.class);

    private PrefsMetaFunction getter;

    public LuaPrefsAdapter() {
        getter = new PrefsMetaFunction(false, StaticEnvironment.PREFS);
    }

    public LuaTable createPrefsTable() {
        LuaTable mt = new LuaTable();
        mt.rawset(LuaConstants.INDEX, getter);
        // mt.rawset(LuaConstants.NEWINDEX, prefsSetterFunction);

        LuaTable table = new LuaTable();
        table.setmetatable(mt);
        return table;
    }

    /**
     * @return All preferences declared in static fields
     */
    static List<Preference<?>> getDeclaredPrefs(Class<?> clazz) {
        List<Preference<?>> result = Lists.newArrayList();

        try {
            for (Preference<?> pref : ReflectUtil.getConstants(clazz, Preference.class).values()) {
                LOG.trace("Found declared preference: {}", pref.getKey());

                result.add(pref);
            }
        } catch (IllegalAccessException e) {
            LOG.warn("Error retrieving attributes from preference holder: {}", clazz, e);
        }
        return result;
    }

    @LuaSerializable
    private static class PrefsMetaFunction extends VarArgFunction {

        private static final long serialVersionUID = 1L;

        private final StaticRef<? extends IPreferenceStore> prefStoreRef;
        private final Set<String> prefHolderClasses = Sets.newLinkedHashSet();
        private final boolean isSetter;

        private transient Map<String, Preference<?>> cachedPrefs;

        public PrefsMetaFunction(boolean isSetter, StaticRef<? extends IPreferenceStore> prefStoreRef) {
            this.isSetter = isSetter;
            this.prefStoreRef = prefStoreRef;

            // Search for preference definitions in the preference store
            prefHolderClasses.add(prefStoreRef.get().getClass().getName());
        }

        private Preference<?> getCachedPref(String requestedKey) {
            if (cachedPrefs == null) {
                cachedPrefs = new HashMap<String, Preference<?>>();
                for (String className : prefHolderClasses) {
                    LOG.debug("Registering preferences for class: {}", className);

                    try {
                        Class<?> clazz = Class.forName(className);
                        for (Preference<?> pref : getDeclaredPrefs(clazz)) {
                            String key = pref.getKey();
                            if (key.startsWith("vn.")) {
                                key = key.substring(3); // Remove "vn." prefix
                            } else if (key.startsWith("vnds.")) {
                                key = key.substring(5); // Remove "vnds." prefix
                            }

                            cachedPrefs.put(key, pref);
                            LOG.debug("Registered preference in Lua: {}", key);
                        }
                    } catch (ClassNotFoundException cnfe) {
                        LOG.warn("Error loading preference holder class: {}", className, cnfe);
                    }
                }
            }
            return cachedPrefs.get(requestedKey);
        }

        @Override
        public Varargs invoke(Varargs args) {
            String key = args.tojstring(2);

            Varargs defaultResult = (isSetter ? LuaConstants.NONE : LuaNil.NIL);

            IPreferenceStore prefStore = prefStoreRef.getIfPresent();
            if (prefStore == null) {
                LOG.debug("Lua code tries to access unavailable prefStore: {}", prefStoreRef);
                return defaultResult;
            }

            Preference<?> pref = getCachedPref(key);
            if (pref == null) {
                LOG.debug("Lua code tries to access unknown pref, key={}", key);
                return defaultResult;
            }

            if (isSetter) {
                doSet(prefStore, pref, args.arg(3));
                return defaultResult;
            } else {
                return CoerceJavaToLua.coerce(prefStore.get(pref));
            }
        }

        // Needs to be in a different function to work with generics
        private static <T> void doSet(IPreferenceStore prefs, Preference<T> pref, LuaValue val) {
            prefs.set(pref, CoerceLuaToJava.coerceArg(val, pref.getType()));
        }

    }

}
