package nl.weeaboo.vn.impl.input;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.io.Resources;

import nl.weeaboo.vn.impl.save.JsonUtil;
import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.input.KeyCombination;
import nl.weeaboo.vn.input.VKey;

/**
 * Input configuration, containing the mapping between virtual keys ({@link VKey}) and physical keyboard/mouse buttons.
 */
public final class InputConfig implements Json.Serializable {

    // --- Uses manual serialization ---
    private final ListMultimap<VKey, KeyCombination> keyMapping = ArrayListMultimap.create();
    // --- Uses manual serialization ---

    // No-arg constructor required for JSON deserialization
    public InputConfig() {
    }

    /**
     * Loads the default input config from a classpath resource.
     *
     * @throws IOException If the input config can't be read.
     */
    public static InputConfig readDefaultConfig() throws IOException {
        String json = Resources.toString(InputConfig.class.getResource("input-config.json"), Charsets.UTF_8);
        return JsonUtil.fromJson(InputConfig.class, json);
    }

    /**
     * Adds an additional physical key for the virtual key {@code vkey}.
     *
     * @see #add(VKey, KeyCombination)
     */
    public void add(VKey vkey, KeyCode physicalKey) {
        add(vkey, new KeyCombination(ImmutableSet.of(physicalKey)));
    }

    /**
     * Adds an additional physical key combination for the virtual key {@code vkey}.
     */
    public void add(VKey vkey, KeyCombination physicalKeys) {
        keyMapping.put(vkey, physicalKeys);
    }

    /**
     * Returns a list of physical key combinations mapped to the supplied virtual key.
     */
    public List<KeyCombination> get(VKey vkey) {
        return keyMapping.get(vkey);
    }

    @Override
    public void write(Json json) {
        for (Map.Entry<VKey, Collection<KeyCombination>> entry : keyMapping.asMap().entrySet()) {
            json.writeArrayStart(entry.getKey().toString());
            for (KeyCombination keyCombination : entry.getValue()) {
                writeKeyCombination(json, keyCombination);
            }
            json.writeArrayEnd();
        }
    }

    private static void writeKeyCombination(Json json, KeyCombination keyCombination) {
        json.writeValue(Joiner.on('+').join(keyCombination.getKeys()));
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        for (JsonValue vkeyEntry : jsonData) {
            VKey vkey = VKey.fromString(vkeyEntry.name);
            for (JsonValue mappedEntry : vkeyEntry) {
                KeyCombination keyCombination = readKeyCombination(mappedEntry.asString());
                keyMapping.put(vkey, keyCombination);
            }
        }
    }

    private static KeyCombination readKeyCombination(String json) {
        List<KeyCode> keyCodes = Lists.newArrayList();
        for (String keyString : Splitter.on('+').split(json)) {
            keyCodes.add(KeyCode.valueOf(keyString));
        }
        return new KeyCombination(keyCodes);
    }
}
