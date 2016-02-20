package nl.weeaboo.vn.save.impl;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public final class JsonUtil {

    private JsonUtil() {        
    }
    
    public static Json newJson() {
        return new Json();
    }
    
    public static <T> T fromJson(Class<T> type, String json) {
        return fromJson(type, parse(json));
    }    
    public static <T> T fromJson(Class<T> type, JsonValue value) {
        Json json = newJson();
        return json.readValue(type, value);
    }
    
    public static JsonValue parse(String json) {
        return new JsonReader().parse(json);        
    }
    
    public static String toJson(Object value) {
        Json json = newJson();
        return json.toJson(value);
    }
    
}
