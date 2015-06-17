package nl.weeaboo.vn.core.impl;

import java.util.HashMap;
import java.util.Map;

import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.settings.IPreferenceStore;
import nl.weeaboo.vn.core.INotifier;

public final class StaticEnvironment {

    public static final StaticRef<IFileSystem> FILE_SYSTEM = StaticRef.from("fileSystem", IFileSystem.class);
    public static final StaticRef<INotifier> NOTIFIER = StaticRef.from("notifier", INotifier.class);
    public static final StaticRef<IPreferenceStore> PREFS = StaticRef.from("prefs", IPreferenceStore.class);

    private static final StaticEnvironment INSTANCE = new StaticEnvironment();

    private final Map<String, Object> objects = new HashMap<String, Object>();

    private StaticEnvironment() {
    }

    public static StaticEnvironment getInstance() {
        return INSTANCE;
    }

    private Object get(String id) {
        synchronized (objects) {
            return objects.get(id);
        }
    }

    public <T> T get(StaticRef<T> ref) {
        String id = ref.getId();
        Class<T> type = ref.getType();

        Object value = get(id);
        return type.cast(value);
    }

    public <T> void set(StaticRef<T> ref, T value) {
        synchronized (objects) {
            objects.put(ref.getId(), value);
        }
    }

}
