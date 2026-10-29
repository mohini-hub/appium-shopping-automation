package com.automation.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/** Loads JSON test data from the classpath (for example src/test/resources/testdata). */
public final class JsonDataReader {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private JsonDataReader() {
    }

    public static <T> T read(String resourcePath, Class<T> type) {
        try (InputStream in = open(resourcePath)) {
            return MAPPER.readValue(in, type);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to parse test data file: " + resourcePath, e);
        }
    }

    public static <T> List<T> readList(String resourcePath, Class<T> elementType) {
        try (InputStream in = open(resourcePath)) {
            return MAPPER.readValue(in, MAPPER.getTypeFactory().constructCollectionType(List.class, elementType));
        } catch (IOException e) {
            throw new IllegalStateException("Unable to parse test data file: " + resourcePath, e);
        }
    }

    private static InputStream open(String resourcePath) {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        if (in == null) {
            throw new IllegalStateException("Test data file not found on the classpath: " + resourcePath);
        }
        return in;
    }
}
