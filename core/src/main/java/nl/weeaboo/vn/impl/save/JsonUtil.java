package nl.weeaboo.vn.impl.save;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public final class JsonUtil {

    private JsonUtil() {
    }

    /** Constructor function. */
    public static Json newJson() {
        return new Json();
    }

    /**
     * Deserializes some JSON to a Java object of the specified type.
     */
    public static <T> T fromJson(Class<T> type, String json) {
        return fromJson(type, parse(json));
    }

    /**
     * Deserializes some JSON to a Java object of the specified type.
     */
    public static <T> T fromJson(Class<T> type, JsonValue value) {
        Json json = newJson();
        return json.readValue(type, value);
    }

    /**
     * Parses JSON text to a Java representation of the JSON structure.
     */
    public static JsonValue parse(String json) {
        return new JsonReader().parse(json);
    }

    /**
     * Serializes a Java object to JSON.
     */
    public static String toJson(Object value) {
        Json json = newJson();
        return json.toJson(value);
    }

}
