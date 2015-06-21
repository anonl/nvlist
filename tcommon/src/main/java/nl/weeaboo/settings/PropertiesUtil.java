package nl.weeaboo.settings;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import nl.weeaboo.common.StringUtil;

public final class PropertiesUtil {

    private PropertiesUtil() {
    }

    /**
     * Loads a .properties file.
     */
    public static Map<String, String> load(InputStream in) throws IOException {
        Properties props = new Properties();
        props.load(new InputStreamReader(in, StringUtil.UTF_8));

        Map<String, String> map = new HashMap<String, String>();
        for (String name : props.stringPropertyNames()) {
            String value = props.getProperty(name);
            if (value != null) {
                map.put(name, value);
            }
        }
        return map;
    }

    /**
     * Saves a .properties file.
     */
    public static void save(OutputStream out, Map<String, String> properties) throws IOException {
        Properties props = new Properties();
        props.putAll(properties);

        props.store(new OutputStreamWriter(out, StringUtil.UTF_8), "");
    }

}
