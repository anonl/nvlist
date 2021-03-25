package nl.weeaboo.vn.buildtools.project;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Build-specific settings.
 */
public final class BuildProperties {

    private final Properties properties;

    public BuildProperties() {
        this(new Properties());
    }

    private BuildProperties(Properties properties) {
        this.properties = properties;
    }

    /**
     * Initializes a build properties object from a Java .properties file.
     *
     * @throws IOException If an I/O error occurs while trying to read the properties file.
     * @throws IllegalArgumentException If the properties file is malformed.
     */
    public static BuildProperties fromFile(Path file) throws IOException {
        Properties properties = new Properties();
        try (InputStream in = Files.newInputStream(file)) {
            properties.load(in);
        }

        return new BuildProperties(properties);
    }

    /**
     * Returns a property from the build-properties file.
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

}
