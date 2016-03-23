package nl.weeaboo.vn.input.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.Map.Entry;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.google.common.base.Charsets;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.io.Resources;

import nl.weeaboo.vn.input.KeyCode;
import nl.weeaboo.vn.input.VKey;
import nl.weeaboo.vn.save.impl.JsonUtil;

public final class InputConfig implements Json.Serializable {

    // --- Uses manual serialization ---
    private final ListMultimap<VKey, KeyCode> keyMapping = ArrayListMultimap.create();
    // --- Uses manual serialization ---

    // No-arg constructor required for JSON deserialization
    public InputConfig() {
    }

    public static InputConfig readDefaultConfig() throws IOException {
        String json = Resources.toString(InputConfig.class.getResource("input-config.json"), Charsets.UTF_8);
        return JsonUtil.fromJson(InputConfig.class, json);
    }

    public void add(VKey vkey, KeyCode keyCode) {
        keyMapping.put(vkey, keyCode);
    }

    public Collection<KeyCode> get(VKey vkey) {
        return keyMapping.get(vkey);
    }

    @Override
    public void write(Json json) {
        for (Entry<VKey, Collection<KeyCode>> entry : keyMapping.asMap().entrySet()) {
            json.writeArrayStart(entry.getKey().toString());
            for (KeyCode keyCode : entry.getValue()) {
                json.writeValue(keyCode);
            }
            json.writeArrayEnd();
        }
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        for (JsonValue vkeyEntry : jsonData) {
            VKey vkey = VKey.fromString(vkeyEntry.name);
            for (JsonValue mappedEntry : vkeyEntry) {
                keyMapping.put(vkey, json.readValue(KeyCode.class, mappedEntry));
            }
        }
    }
}
