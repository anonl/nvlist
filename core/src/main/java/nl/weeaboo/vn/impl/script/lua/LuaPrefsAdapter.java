package nl.weeaboo.vn.impl.script.lua;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.prefsstore.Preference;
import nl.weeaboo.vn.core.NovelPrefs;
import nl.weeaboo.vn.impl.core.NovelPrefsStore;
import nl.weeaboo.vn.impl.core.StaticEnvironment;
import nl.weeaboo.vn.impl.core.StaticRef;

public class LuaPrefsAdapter {

    private static final Logger LOG = LoggerFactory.getLogger(LuaPrefsAdapter.class);

    private PrefsMetaFunction getter;

    public LuaPrefsAdapter() {
        getter = new PrefsMetaFunction(false, StaticEnvironment.PREFS);
    }

    /**
     * @return A Lua table with an appropriate metatable for accessing NVList preferences.
     */
    public LuaTable createPrefsTable() {
        LuaTable mt = new LuaTable();
        mt.rawset(LuaConstants.INDEX, getter);
        // mt.rawset(LuaConstants.NEWINDEX, prefsSetterFunction);

        LuaTable table = new LuaTable();
        table.setmetatable(mt);
        return table;
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

            // Add default preference holder
            prefHolderClasses.add(NovelPrefs.class.getName());
        }

        private Preference<?> getCachedPref(String requestedKey) {
            if (cachedPrefs == null) {
                cachedPrefs = new HashMap<>();
                for (String className : prefHolderClasses) {
                    LOG.debug("Registering preferences for class: {}", className);

                    try {
                        Class<?> clazz = Class.forName(className);
                        for (Preference<?> pref : NovelPrefsStore.getDeclaredPrefs(clazz)) {
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
                LOG.trace("Lua code tries to access unknown pref, key={}", key);
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
