package com.automation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;

/**
 * Reads settings from config.properties.
 * A JVM system property with the same key always wins, so any value can be overridden
 * from the command line: mvn test -DdeviceName=Pixel_7
 */
public final class ConfigReader {

    private static final String CONFIG_FILE = "config.properties";
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) {
                throw new IllegalStateException(CONFIG_FILE + " was not found on the classpath (src/main/resources)");
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read " + CONFIG_FILE, e);
        }
    }

    private ConfigReader() {
    }

    /** Returns the value for the key or fails if it is missing. */
    public static String get(String key) {
        return getOptional(key)
                .orElseThrow(() -> new IllegalStateException("Missing or empty config value: " + key));
    }

    /** Returns the value for the key, or empty if it is missing or blank. */
    public static Optional<String> getOptional(String key) {
        String value = System.getProperty(key, PROPS.getProperty(key));
        return Optional.ofNullable(value)
                .map(String::trim)
                .filter(v -> !v.isEmpty());
    }

    public static int getInt(String key) {
        return Integer.parseInt(get(key));
    }

    public static boolean getBoolean(String key) {
        return Boolean.parseBoolean(get(key));
    }
}
