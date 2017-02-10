package nl.weeaboo.vn.impl.core;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.assets.AssetManager;

import nl.weeaboo.filesystem.IFileSystem;
import nl.weeaboo.filesystem.IWritableFileSystem;
import nl.weeaboo.prefsstore.IPreferenceStore;
import nl.weeaboo.styledtext.layout.IFontStore;
import nl.weeaboo.vn.core.IEnvironment;
import nl.weeaboo.vn.core.INotifier;
import nl.weeaboo.vn.core.INovel;
import nl.weeaboo.vn.core.ISystemEnv;
import nl.weeaboo.vn.gdx.res.GeneratedResourceStore;
import nl.weeaboo.vn.impl.image.GdxTextureStore;
import nl.weeaboo.vn.impl.image.ShaderStore;
import nl.weeaboo.vn.impl.sound.GdxMusicStore;
import nl.weeaboo.vn.input.IInput;

/**
 * The static environment contains services and interfaces that connect to the external system. Static references aren't
 * stored in save files like the {@link IEnvironment} is, but rather they're set once on startup.
 */
public final class StaticEnvironment {

    public static final StaticRef<IFileSystem> FILE_SYSTEM =
            StaticRef.from("fileSystem", IFileSystem.class);

    public static final StaticRef<IWritableFileSystem> OUTPUT_FILE_SYSTEM =
            StaticRef.from("outputFileSystem", IWritableFileSystem.class);

    public static final StaticRef<INotifier> NOTIFIER =
            StaticRef.from("notifier", INotifier.class);

    public static final StaticRef<IPreferenceStore> PREFS =
            StaticRef.from("prefs", IPreferenceStore.class);

    public static final StaticRef<IInput> INPUT =
            StaticRef.from("input", IInput.class);

    public static final StaticRef<ISystemEnv> SYSTEM_ENV =
            StaticRef.from("systemEnv", ISystemEnv.class);

    public static final StaticRef<INovel> NOVEL =
            StaticRef.from("novel", INovel.class);

    public static final StaticRef<AssetManager> ASSET_MANAGER =
            StaticRef.from("assetManager", AssetManager.class);

    public static final StaticRef<GdxTextureStore> TEXTURE_STORE =
            StaticRef.from("store.texture", GdxTextureStore.class);

    public static final StaticRef<GeneratedResourceStore> GENERATED_RESOURCES =
            StaticRef.from("generatedResources", GeneratedResourceStore.class);

    public static final StaticRef<ShaderStore> SHADER_STORE =
            StaticRef.from("store.shader", ShaderStore.class);

    public static final StaticRef<GdxMusicStore> MUSIC_STORE =
            StaticRef.from("store.music", GdxMusicStore.class);

    public static final StaticRef<IFontStore> FONT_STORE =
            StaticRef.from("store.font", IFontStore.class);

    private static final StaticEnvironment INSTANCE = new StaticEnvironment();

    private final Map<String, Object> objects = new HashMap<>();

    private StaticEnvironment() {
    }

    /**
     * Returns a reference the static environment.
     */
    public static StaticEnvironment getInstance() {
        return INSTANCE;
    }

    private Object get(String id) {
        synchronized (objects) {
            return objects.get(id);
        }
    }

    /** Fetches the value of a static reference. */
    public <T> T get(StaticRef<T> ref) {
        String id = ref.getId();
        Class<T> type = ref.getType();

        Object value = get(id);
        return type.cast(value);
    }

    /** Sets the value of a static reference. */
    public <T> void set(StaticRef<T> ref, T value) {
        synchronized (objects) {
            if (value == null) {
                objects.remove(ref.getId());
            } else {
                objects.put(ref.getId(), value);
            }
        }
    }

    /** Clears all static references in the environment. */
    public void clear() {
        synchronized (objects) {
            objects.clear();
        }
    }

}
